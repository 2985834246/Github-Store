package zed.rainxch.githubstore.feature.details.data

import co.touchlab.kermit.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import zed.rainxch.githubstore.core.domain.model.PlatformType
import java.awt.Desktop
import java.io.File
import java.io.IOException

class DesktopInstaller(
    private val platform: PlatformType
) : Installer {

    override suspend fun isSupported(extOrMime: String): Boolean {
        val ext = extOrMime.lowercase().removePrefix(".")
        return when (platform) {
            PlatformType.WINDOWS -> ext in listOf("msi", "exe")
            PlatformType.MACOS -> ext in listOf("dmg", "pkg")
            PlatformType.LINUX -> ext in listOf("appimage", "deb", "rpm")
            else -> false
        }
    }

    override suspend fun ensurePermissionsOrThrow(extOrMime: String) = withContext(Dispatchers.IO) {
        val ext = extOrMime.lowercase().removePrefix(".")

        if (platform == PlatformType.LINUX && ext == "appimage") {
            try {
                val tempFile = File.createTempFile("appimage_perm_test", ".tmp")
                try {
                    val canSetExecutable = tempFile.setExecutable(true)
                    if (!canSetExecutable) {
                        throw IllegalStateException(
                            "Unable to set executable permissions. AppImage installation requires " +
                                    "the ability to make files executable."
                        )
                    }
                } finally {
                    tempFile.delete()
                }
            } catch (e: IOException) {
                throw IllegalStateException(
                    "Failed to verify permission capabilities for AppImage installation: ${e.message}",
                    e
                )
            } catch (e: SecurityException) {
                throw IllegalStateException(
                    "Security restrictions prevent setting executable permissions for AppImage files.",
                    e
                )
            }
        }
    }

    override suspend fun install(filePath: String, extOrMime: String) =
        withContext(Dispatchers.IO) {
            val file = File(filePath)
            if (!file.exists()) {
                throw IllegalStateException("File not found: $filePath")
            }

            val ext = extOrMime.lowercase().removePrefix(".")

            when (platform) {
                PlatformType.WINDOWS -> installWindows(file, ext)
                PlatformType.MACOS -> installMacOS(file, ext)
                PlatformType.LINUX -> installLinux(file, ext)
                else -> throw UnsupportedOperationException("Installation not supported on $platform")
            }
        }

    private fun installWindows(file: File, ext: String) {
        when (ext) {
            "msi" -> {
                val pb = ProcessBuilder("msiexec", "/i", file.absolutePath)
                pb.start()
            }

            "exe" -> {
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(file)
                } else {
                    val pb = ProcessBuilder(file.absolutePath)
                    pb.start()
                }
            }

            else -> throw IllegalArgumentException("Unsupported Windows installer: .$ext")
        }
    }

    private fun installMacOS(file: File, ext: String) {
        when (ext) {
            "dmg" -> {
                val pb = ProcessBuilder("open", file.absolutePath)
                pb.start()
            }

            "pkg" -> {
                val pb = ProcessBuilder("open", file.absolutePath)
                pb.start()
            }

            else -> throw IllegalArgumentException("Unsupported macOS installer: .$ext")
        }
    }

    private fun installLinux(file: File, ext: String) {
        when (ext) {
            "appimage" -> {
                installAppImage(file)
            }

            "deb" -> {
                installDebPackage(file)
            }

            "rpm" -> {
                installRpmPackage(file)
            }

            else -> throw IllegalArgumentException("Unsupported Linux installer: .$ext")
        }
    }

    private fun installDebPackage(file: File) {
        Logger.d { "Installing DEB package: ${file.absolutePath}" }

        val installMethods = listOf(
            listOf("pkexec", "apt", "install", "-y", file.absolutePath),

            listOf("pkexec", "sh", "-c", "dpkg -i '${file.absolutePath}' || apt-get install -f -y"),

            listOf("gdebi-gtk", file.absolutePath),

            null
        )

        for (method in installMethods) {
            if (method == null) {
                openTerminalForDebInstall(file.absolutePath)
                return
            }

            try {
                Logger.d { "Trying installation method: ${method.joinToString(" ")}" }
                val process = ProcessBuilder(method).start()
                val exitCode = process.waitFor()

                if (exitCode == 0) {
                    Logger.d { "DEB package installed successfully" }
                    return
                } else {
                    Logger.w { "Installation method failed with exit code: $exitCode" }
                }
            } catch (e: IOException) {
                Logger.w { "Installation method not available: ${e.message}" }
            }
        }

        throw IOException("Could not install DEB package. Please install it manually.")
    }

    private fun installRpmPackage(file: File) {
        Logger.d { "Installing RPM package: ${file.absolutePath}" }

        val installMethods = listOf(
            listOf("pkexec", "dnf", "install", "-y", file.absolutePath),

            listOf("pkexec", "yum", "install", "-y", file.absolutePath),

            listOf("pkexec", "zypper", "install", "-y", file.absolutePath),

            listOf("pkexec", "rpm", "-i", file.absolutePath),

            null
        )

        for (method in installMethods) {
            if (method == null) {
                openTerminalForRpmInstall(file.absolutePath)
                return
            }

            try {
                Logger.d { "Trying installation method: ${method.joinToString(" ")}" }
                val process = ProcessBuilder(method).start()
                val exitCode = process.waitFor()

                if (exitCode == 0) {
                    Logger.d { "RPM package installed successfully" }
                    return
                } else {
                    Logger.w { "Installation method failed with exit code: $exitCode" }
                }
            } catch (e: IOException) {
                Logger.w { "Installation method not available: ${e.message}" }
            }
        }

        throw IOException("Could not install RPM package. Please install it manually.")
    }

    private fun openTerminalForDebInstall(filePath: String) {
        Logger.d { "Opening terminal for DEB installation" }

        val terminals = listOf(
            listOf(
                "gnome-terminal",
                "--",
                "bash",
                "-c",
                "echo 'Installing $filePath...'; sudo dpkg -i '$filePath' && sudo apt-get install -f -y; echo ''; echo 'Installation complete. Press Enter to close...'; read"
            ),
            listOf(
                "konsole",
                "-e",
                "bash",
                "-c",
                "echo 'Installing $filePath...'; sudo dpkg -i '$filePath' && sudo apt-get install -f -y; echo ''; echo 'Installation complete. Press Enter to close...'; read"
            ),
            listOf(
                "xterm",
                "-e",
                "bash",
                "-c",
                "echo 'Installing $filePath...'; sudo dpkg -i '$filePath' && sudo apt-get install -f -y; echo ''; echo 'Installation complete. Press Enter to close...'; read"
            ),
            listOf(
                "xfce4-terminal",
                "-e",
                "bash -c \"echo 'Installing $filePath...'; sudo dpkg -i '$filePath' && sudo apt-get install -f -y; echo ''; echo 'Installation complete. Press Enter to close...'; read\""
            )
        )

        for (terminalCmd in terminals) {
            try {
                Logger.d { "Trying terminal: ${terminalCmd[0]}" }
                ProcessBuilder(terminalCmd).start()
                return
            } catch (e: IOException) {
                Logger.w { "Terminal not available: ${terminalCmd[0]}" }
            }
        }

        throw IOException("Could not find a terminal emulator to run package installation")
    }

    private fun openTerminalForRpmInstall(filePath: String) {
        Logger.d { "Opening terminal for RPM installation" }

        val terminals = listOf(
            listOf(
                "gnome-terminal",
                "--",
                "bash",
                "-c",
                "echo 'Installing $filePath...'; sudo dnf install -y '$filePath' || sudo yum install -y '$filePath' || sudo rpm -i '$filePath'; echo ''; echo 'Installation complete. Press Enter to close...'; read"
            ),
            listOf(
                "konsole",
                "-e",
                "bash",
                "-c",
                "echo 'Installing $filePath...'; sudo dnf install -y '$filePath' || sudo yum install -y '$filePath' || sudo rpm -i '$filePath'; echo ''; echo 'Installation complete. Press Enter to close...'; read"
            ),
            listOf(
                "xterm",
                "-e",
                "bash",
                "-c",
                "echo 'Installing $filePath...'; sudo dnf install -y '$filePath' || sudo yum install -y '$filePath' || sudo rpm -i '$filePath'; echo ''; echo 'Installation complete. Press Enter to close...'; read"
            )
        )

        for (terminalCmd in terminals) {
            try {
                Logger.d { "Trying terminal: ${terminalCmd[0]}" }
                ProcessBuilder(terminalCmd).start()
                return
            } catch (e: IOException) {
                Logger.w { "Terminal not available: ${terminalCmd[0]}" }
            }
        }

        throw IOException("Could not find a terminal emulator to run package installation")
    }


    private fun installAppImage(file: File) {
        Logger.d { "Installing AppImage: ${file.absolutePath}" }

        val desktopDir = getDesktopDirectory()
        Logger.d { "Desktop directory: ${desktopDir.absolutePath}" }
        Logger.d { "Desktop exists: ${desktopDir.exists()}, isDirectory: ${desktopDir.isDirectory}, canWrite: ${desktopDir.canWrite()}" }

        val destinationFile = File(desktopDir, file.name)

        val finalDestination = if (destinationFile.exists()) {
            Logger.d { "File already exists, generating unique name" }
            generateUniqueFileName(desktopDir, file.name)
        } else {
            destinationFile
        }

        Logger.d { "Final destination: ${finalDestination.absolutePath}" }

        try {
            Logger.d { "Copying file..." }
            file.copyTo(finalDestination, overwrite = false)
            Logger.d { "Copy successful, file size: ${finalDestination.length()} bytes" }

            val executableSet = finalDestination.setExecutable(true, false)
            Logger.d { "Set executable: $executableSet" }

            if (!finalDestination.exists()) {
                throw IllegalStateException("File was copied but doesn't exist at destination")
            }

            try {
                Logger.d { "Attempting to open desktop folder..." }
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(desktopDir)
                    Logger.d { "Desktop folder opened" }
                } else {
                    Logger.w { "Desktop not supported, trying xdg-open" }
                    ProcessBuilder("xdg-open", desktopDir.absolutePath).start()
                }
            } catch (e: Exception) {
                Logger.w { "Could not open desktop folder: ${e.message}" }
            }

            Logger.d { "AppImage installation completed successfully" }
        } catch (e: IOException) {
            Logger.e { "Failed to copy AppImage: ${e.message}" }
            e.printStackTrace()
            throw IllegalStateException(
                "Failed to copy AppImage to desktop: ${e.message}. " +
                        "Desktop path: ${desktopDir.absolutePath}. " +
                        "Please ensure you have write permissions to your Desktop folder.",
                e
            )
        } catch (e: SecurityException) {
            Logger.e { "Security exception: ${e.message}" }
            e.printStackTrace()
            throw IllegalStateException(
                "Security restrictions prevent copying AppImage to desktop.",
                e
            )
        } catch (e: Exception) {
            Logger.e { "Unexpected error: ${e.message}" }
            e.printStackTrace()
            throw IllegalStateException("Failed to install AppImage: ${e.message}", e)
        }
    }

    private fun getDesktopDirectory(): File {
        try {
            val process = ProcessBuilder("xdg-user-dir", "DESKTOP").start()
            val output = process.inputStream.bufferedReader().readText().trim()
            process.waitFor()

            if (output.isNotEmpty() && output != "DESKTOP") {
                val xdgDesktop = File(output)
                if (xdgDesktop.exists() && xdgDesktop.isDirectory) {
                    return xdgDesktop
                }
            }
        } catch (e: Exception) {
        }

        val homeDir = System.getProperty("user.home")
        val desktopCandidates = listOf(
            File(homeDir, "Desktop"),
            File(homeDir, "desktop"),
            File(homeDir, ".local/share/Desktop"),
            File(homeDir)
        )

        return desktopCandidates.firstOrNull { it.exists() && it.isDirectory }
            ?: File(homeDir, "Desktop").also { it.mkdirs() }
    }

    private fun generateUniqueFileName(directory: File, originalName: String): File {
        val nameWithoutExtension = originalName.substringBeforeLast(".")
        val extension = originalName.substringAfterLast(".", "")

        var counter = 1
        var candidateFile: File

        do {
            val newName = if (extension.isNotEmpty()) {
                "${nameWithoutExtension}_$counter.$extension"
            } else {
                "${nameWithoutExtension}_$counter"
            }
            candidateFile = File(directory, newName)
            counter++
        } while (candidateFile.exists() && counter < 1000)

        if (candidateFile.exists()) {
            throw IllegalStateException("Could not generate unique filename on desktop")
        }

        return candidateFile
    }

}
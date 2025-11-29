package zed.rainxch.githubstore.core.domain.model

enum class Architecture {
    X86_64,     // Intel/AMD 64-bit
    AARCH64,    // ARM 64-bit
    X86,        // Intel/AMD 32-bit
    ARM,        // ARM 32-bit
    UNKNOWN;

    companion object {
        fun fromString(arch: String): Architecture {
            val normalized = arch.lowercase().trim()
            return when {
                normalized in listOf("x86_64", "amd64", "x64") -> X86_64
                normalized in listOf("aarch64", "arm64") -> AARCH64
                normalized in listOf("x86", "i386", "i686") -> X86
                normalized in listOf("arm", "armv7l", "armv7") -> ARM
                else -> UNKNOWN
            }
        }
    }
}

expect fun getSystemArchitecture(): Architecture
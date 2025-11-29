package zed.rainxch.githubstore.core.domain.model

import android.os.Build

actual fun getSystemArchitecture(): Architecture {
    val arch = Build.SUPPORTED_ABIS.firstOrNull() ?: return Architecture.UNKNOWN
    return when {
        arch.contains("arm64") || arch.contains("aarch64") -> Architecture.AARCH64
        arch.contains("armeabi") -> Architecture.ARM
        arch.contains("x86_64") -> Architecture.X86_64
        arch.contains("x86") -> Architecture.X86
        else -> Architecture.UNKNOWN
    }
}
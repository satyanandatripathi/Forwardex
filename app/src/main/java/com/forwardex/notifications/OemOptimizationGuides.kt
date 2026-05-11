package com.forwardex.notifications

object OemOptimizationGuides {
    val guides = mapOf(
        "xiaomi" to "Lock app in recents, enable Autostart, disable battery restrictions.",
        "samsung" to "Add app to Never sleeping apps and allow background activity.",
        "oppo" to "Enable Auto-launch, allow background run, disable deep optimization.",
        "vivo" to "Enable background startup and whitelist battery management.",
        "oneplus" to "Set app battery optimization to Don't optimize.",
        "realme" to "Enable app auto-launch and foreground persistence.",
        "motorola" to "Disable adaptive battery restrictions for Forwardex."
    )
}

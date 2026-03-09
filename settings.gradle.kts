pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

buildCache {
    local {
        isEnabled = true
        directory = File(rootDir, "build-cache")
    }
}

rootProject.name = "Background Location Permission Rationale"
include(":app", ":appjava", ":permissionsui")

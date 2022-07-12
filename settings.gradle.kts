// https://github.com/otormaigh/playground-android/issues/27
//dependencyResolutionManagement {
//    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
//    repositories {
//        google()
//        mavenCentral()
//    }
//}

plugins {
    id("com.gradle.enterprise") version "3.10.3"
}

buildCache {
    local {
        isEnabled = true
        directory = File(rootDir, "build-cache")
        removeUnusedEntriesAfterDays = 30
    }
    remote<HttpBuildCache> {
        isEnabled = false
    }
}

rootProject.name = "Background Location Permission Rationale"
include(":app",":appjava", ":permissionsui")

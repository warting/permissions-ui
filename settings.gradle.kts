// https://github.com/otormaigh/playground-android/issues/27
//dependencyResolutionManagement {
//    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
//    repositories {
//        google()
//        mavenCentral()
//    }
//}

plugins {
    id("com.gradle.enterprise") version "3.17.1"
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

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            // from(files("../gradle/libs.versions.toml"))
        }
    }
}

rootProject.name = "Background Location Permission Rationale"
include(":app",":appjava", ":permissionsui")

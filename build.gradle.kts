

import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import java.util.Properties

// Top-level build file where you can add configuration options common to all sub-projects/modules.

plugins {
    id("com.android.application") version libs.versions.agp apply false
    id("com.android.library") version libs.versions.agp apply false
    id("org.jetbrains.kotlin.plugin.compose") version libs.versions.kotlin apply false
    alias(libs.plugins.com.github.ben.manes.versions)
    alias(libs.plugins.nl.littlerobots.version.catalog.update)
    id("io.gitlab.arturbosch.detekt") version "1.23.8"
    id("io.github.gradle-nexus.publish-plugin") version "1.3.0"
    id("org.jetbrains.kotlinx.binary-compatibility-validator") version "0.18.1"
    id("com.gladed.androidgitversion") version "0.4.14"
}

androidGitVersion {
    tagPattern = "^v[0-9]+.*"
}

val localProperties = Properties().apply {
    val localPropertiesFile = File(rootDir, "local.properties")
    if (localPropertiesFile.exists()) {
        localPropertiesFile.inputStream().use { load(it) }
    }
}

val gitOrLocalVersion: String =
    localProperties.getProperty("VERSION_NAME", androidGitVersion.name().replace("v", ""))

version = gitOrLocalVersion

apiValidation {
    ignoredProjects.add("app")
}

allprojects {
    apply(plugin = "io.gitlab.arturbosch.detekt")

    dependencies {
        detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.23.8")
    }

    detekt {
        autoCorrect = true
    }

    // https://github.com/otormaigh/playground-android/issues/27
    repositories {
        google()
        mavenCentral()
    }
}

detekt {
    autoCorrect = true
    buildUponDefaultConfig = true
    config.setFrom(files("$projectDir/config/detekt/detekt.yml"))
    baseline = file("$projectDir/config/detekt/baseline.xml")
}

fun isNonStable(version: String): Boolean {
    val stableKeyword = listOf("RELEASE", "FINAL", "GA").any { version.uppercase().contains(it) }
    val regex = "^[0-9,.v-]+(-r)?$".toRegex()
    val isStable = stableKeyword || regex.matches(version)
    return isStable.not()
}

tasks.named("dependencyUpdates", DependencyUpdatesTask::class.java).configure {
    rejectVersionIf {
        isNonStable(candidate.version)
    }
}

versionCatalogUpdate {
    sortByKey.set(true)
}

tasks.register<Delete>("clean") {
    delete(layout.buildDirectory)
}

apply(from = "${rootDir}/gradle/publish-root.gradle")

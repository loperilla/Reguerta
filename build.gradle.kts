
// Top-level build file where you can add configuration options common to all sub-projects/modules.

plugins {
    `version-catalog`
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kspPlugin) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.ben.manes.versions)
    alias(libs.plugins.version.catalog.update)
}

buildscript {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
    dependencies {
        classpath(libs.javapoet)
    }
    configurations["classpath"].resolutionStrategy { // Si algún plugin cuela otra versión, la forzamos
        force("com.squareup:javapoet:1.13.0")
        eachDependency {
            if (requested.group == "com.squareup" && requested.name == "javapoet") {
                useVersion("1.13.0")
                because("Hilt 2.57.x usa ClassName.canonicalName() (>= 1.13.0)")
            }
        }
    }
}

// Tarea auxiliar para inspeccionar el classpath de plugins
tasks.register("printBuildscriptClasspath") {
    doLast {
        val artifacts =
            buildscript.configurations.getByName("classpath").resolvedConfiguration.resolvedArtifacts
        artifacts.forEach { println("${it.moduleVersion.id.group}:${it.name}:${it.moduleVersion.id.version}") }
    }
}

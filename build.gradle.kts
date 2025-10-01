import org.gradle.api.artifacts.ComponentSelection
import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask

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

// Variable para pillar versiones estables
fun isNonStable(version: String): Boolean {
    val stableKeyword = listOf("RELEASE", "FINAL", "GA").any { version.uppercase().contains(it) }
    val regex = "^[0-9,.v-]+(-r)?$".toRegex()
    val isStable = stableKeyword || regex.matches(version)
    return isStable.not()
}

// Tarea para setear estas versiones estables al actualizar dependencias
tasks.withType<DependencyUpdatesTask>().configureEach {
    resolutionStrategy {
        componentSelection {
            all { selection: ComponentSelection ->
                val candidateVersion = selection.candidate.version
                if (isNonStable(candidateVersion)) {
                    selection.reject("Release candidate")
                }
            }
        }
    }
}
plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kspPlugin)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.androidJUnit5)
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "$GROUP_ID.presentation"
    compileSdk = configCompileSdkVersion

    defaultConfig {
        minSdk = configMinSdkVersion
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
        buildConfigField("Boolean", "IS_DEBUG", "true")
        buildConfigField("String", "VERSION_NAME", "\"$VERSION_NAME\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeCompiler {
        reportsDestination = layout.buildDirectory.dir("compose_compiler")
    }
}

kotlin {
    jvmToolchain(21)
}

dependencies {
    implementation(project(":domain"))
    implementation(project(":localData"))
    // Compose
    implementation(platform(libs.compose.bom))
    implementation(libs.androidx.activity)
    implementation(libs.bundles.compose)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.hilt.navigation)
    implementation(libs.splashscreen)
    implementation(libs.timber)
    implementation(libs.firebase.common.ktx)

    debugImplementation(libs.compose.ui.tooling)
    debugImplementation(libs.compose.ui.test.manifest)

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    //Test
    testImplementation(project(":testUtils"))
    testImplementation(libs.junit)
    testImplementation(libs.bundles.jupiter)
    testRuntimeOnly(libs.jupiter.engine)
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.turbine)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.assertk)

    androidTestImplementation(libs.test.ext.junit)
    androidTestImplementation(libs.test.espresso)
}
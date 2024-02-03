plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kspPlugin)
    alias(libs.plugins.kotlin.android)
    id("com.google.gms.google-services")
    alias(libs.plugins.androidJUnit5)
}

android {
    namespace = "$GROUP_ID.presentation"
    compileSdk = configCompileSdkVersion

    defaultConfig {
        minSdk = configMinSdkVersion

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
    }
}

dependencies {
    implementation(project(":data"))

    // Compose
    implementation(platform(libs.compose.bom))
    implementation(libs.androidx.activity)
    implementation(libs.bundles.compose)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.hilt.navigation)
    implementation(libs.splashscreen)
    implementation(libs.timber)

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
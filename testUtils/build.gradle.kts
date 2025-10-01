plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kspPlugin)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "$GROUP_ID.testutils"
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
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}

kotlin {
    jvmToolchain(21)
}

dependencies {
    implementation(project(":localData"))
    //Datastore
    implementation(libs.datastore)

    //Test
    implementation(libs.junit)
    implementation(libs.kotlinx.coroutines.test)
    implementation(libs.assertk)
    implementation(libs.androidx.runner)
    // Hilt
    implementation(libs.hilt.android.testing)
    ksp(libs.hilt.android.compiler)
    implementation(libs.bundles.jupiter)
    testRuntimeOnly(libs.jupiter.engine)
}


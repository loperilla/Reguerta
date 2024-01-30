@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kspPlugin)
    alias(libs.plugins.kotlin.android)
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
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
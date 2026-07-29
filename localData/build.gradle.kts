plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kspPlugin)
    alias(libs.plugins.hilt)
    alias(libs.plugins.androidx.room)
}

android {
    namespace = "$GROUP_ID.localdata"
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

extensions.configure<com.android.build.api.dsl.LibraryExtension>("android") {
    sourceSets.named("androidTest") {
        assets.directories += "$projectDir/schemas"
    }
}

kotlin {
    jvmToolchain(21)
}

room {
    schemaDirectory("$projectDir/schemas")
}

dependencies {
    //Datastore
    api(libs.datastore)

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // Room
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)

    // Tests
    testImplementation(libs.junit)
    androidTestImplementation(libs.test.ext.junit)
    androidTestImplementation(libs.test.espresso)
}

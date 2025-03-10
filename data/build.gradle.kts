plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kspPlugin)
    alias(libs.plugins.kotlin.android)
    id("dagger.hilt.android.plugin")
}

android {
    namespace = "$GROUP_ID.data"
    compileSdk = configCompileSdkVersion

    defaultConfig {
        minSdk = configMinSdkVersion
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }
    buildFeatures {
        buildConfig = true
    }
    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile(
                    "proguard-android-optimize.txt"
                ),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    kotlinOptions {
        jvmTarget = "21"
    }
}

dependencies {
    implementation(project(":localData"))
    //Datastore
    implementation(libs.datastore)

    // Hilt
    implementation(libs.hilt.android)
    implementation(libs.androidx.annotation)
    implementation(libs.androidx.legacy.support.v4)
    ksp(libs.hilt.compiler)

    //Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.bundles.firebase)

    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.test.ext.junit)
    androidTestImplementation(libs.test.espresso)
}


plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kspPlugin)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.androidJUnit5)
    id("dagger.hilt.android.plugin")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}

android {
    namespace = "$GROUP_ID.user"
    compileSdk = 34

    defaultConfig {
        applicationId = "${GROUP_ID}.user"
        minSdk = configMinSdkVersion
        targetSdk = configTargetSdkVersion
        versionCode = VERSION_CODE
        versionName = VERSION_NAME

        testInstrumentationRunner = hiltRunnerPackage
        vectorDrawables {
            useSupportLibrary = true
        }
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
            signingConfig = signingConfigs.getByName("debug")
        }
        debug {
            applicationIdSuffix = ".debug"
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "${JavaVersion.VERSION_17}"
    }
    buildFeatures {
        compose = true
        buildConfig = false
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(project(":data"))
    implementation(project(":presentation"))
    // Compose
    implementation(platform(libs.compose.bom))
    implementation(libs.androidx.activity)
    implementation(libs.bundles.compose)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.hilt.navigation)
    implementation(libs.splashscreen)
    implementation(libs.timber)

    // Hilt
    implementation(libs.hilt.android)
    testImplementation(project(":testUtils"))
    ksp(libs.hilt.compiler)

    //Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.bundles.firebase)

    //Test
    testImplementation(libs.junit)
    testImplementation(libs.bundles.jupiter)
    testRuntimeOnly(libs.jupiter.engine)
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.turbine)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.assertk)

    // Android test
    androidTestImplementation(project(":testUtils"))
    // Hilt Testing
    androidTestImplementation(libs.hilt.android.testing)
    kspTest(libs.hilt.compiler)

    androidTestImplementation(libs.test.ext.junit)
    androidTestImplementation(libs.test.espresso)
    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(libs.compose.ui.test)
    androidTestImplementation(libs.navigation.testing)
    debugImplementation(libs.compose.ui.tooling)
    debugImplementation(libs.compose.ui.test.manifest)
}

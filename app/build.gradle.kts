import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.devtools.ksp") version "2.0.20-1.0.24"
}

android {

    namespace = "com.sommerengineering.baraudio"
    compileSdk = 34

    defaultConfig {

        applicationId = "com.sommerengineering.baraudio"
        minSdk = 28
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        // import local.properties
        val localProperties = project.rootProject.file("local.properties")
        val properties = Properties()
        properties.load(localProperties.inputStream())

        // parse api keys
        val googleSignInClientId = properties.getProperty("googleSignInClientId")
        buildConfigField(
            type = "String",
            name = "googleSignInClientId",
            value = googleSignInClientId
        )

        // configure ksp for koin
        sourceSets {
            getByName("main"){
                java.srcDir("src/main/java")
                java.srcDir("src/main/kotlin")
            }
            getByName("test") {
                java.srcDir("src/test/java")
                java.srcDir("src/test/kotlin")
            }
        }

        applicationVariants.configureEach {
            kotlin.sourceSets {
                getByName(name) {
                    kotlin.srcDir("build/generated/ksp/${name}/kotlin")
                }
            }
        }
    }

    buildTypes {

        // debug keystore location
        // ~/.android/debug.keystore

        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    // default october 2024
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation(kotlin("script-runtime"))

    // navigation
    implementation(libs.androidx.navigation.compose)
    
    // google sign-in
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.googleid)

    // koin
    implementation(libs.koin.android)
}
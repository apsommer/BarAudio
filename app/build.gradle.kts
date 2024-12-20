import java.io.FileInputStream
import java.util.Properties

plugins {

    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.services)
    alias(libs.plugins.crashlytics)
}

// import release upload signing keystore
val keystoreProperties = Properties()
keystoreProperties.load(
    FileInputStream(
        rootProject.file(
            rootProject.projectDir.absolutePath + "/upload/keystore.properties")))

android {

    signingConfigs {
        create("release") {
            storeFile = file(keystoreProperties["storeFile"] as String)
            storePassword = keystoreProperties["storePassword"] as String
            keyAlias = keystoreProperties["keyAlias"] as String
            keyPassword = keystoreProperties["keyPassword"] as String
        }
    }

    namespace = "com.sommerengineering.baraudio"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.sommerengineering.baraudio"
        minSdk = 28
        targetSdk = 35
        versionCode = 41 // increment for each release
        versionName = "2.0.181224a" // major.minor.date

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        // import local.properties
        val localProperties = project.rootProject.file("local.properties")
        val properties = Properties()
        properties.load(localProperties.inputStream())

        // parse api keys
        val googleSignInWebClientId = properties.getProperty("googleSignInWebClientId")
        buildConfigField(
            type = "String",
            name = "googleSignInWebClientId",
            value = googleSignInWebClientId)
    }

    buildTypes {

        // debug keystore location
        // ~/.android/debug.keystore

        // enable all variables in debugger break points
        debug {
            isMinifyEnabled = false
            kotlinOptions {
                freeCompilerArgs = listOf("-Xdebug")
            }
        }

        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
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
    implementation(libs.koin.androidx.compose)

    // firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.auth)
    implementation(libs.play.services.auth)
    implementation(libs.firebase.messaging)
    implementation(libs.firebase.database)
    implementation(libs.firebase.crashlytics)

    // preferences datastore
    implementation(libs.androidx.datastore.preferences)

    // splash screen
    implementation(libs.androidx.core.splashscreen)

    // billing
    implementation(libs.billing)
    implementation(libs.billing.ktx)

    // play in-app update
    implementation(libs.app.update)
    implementation(libs.app.update.ktx)
}
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.services)
    alias(libs.plugins.crashlytics)
    alias(libs.plugins.compose.compiler)
//    alias(libs.plugins.kotlin.ksp)
//    alias(libs.plugins.hilt)

    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
}

// import release upload signing keystore
val keystoreProperties = Properties()
keystoreProperties.load(
    FileInputStream(
        rootProject.file(
            rootProject.projectDir.absolutePath + "/upload/keystore.properties")))

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.fromTarget("17")
        freeCompilerArgs = listOf("-Xdebug") // enable all variables in debugger break points
    }
}

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
    compileSdk = 36

    defaultConfig {
        applicationId = "com.sommerengineering.baraudio"
        minSdk = 28
        targetSdk = 36
        versionCode = 57 // increment for each release
        versionName = "2.9.110925a" // major.minor.date.letter

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
        val rapidApiKey = properties.getProperty("rapidApiKey")
        buildConfigField(
            type = "String",
            name = "rapidApiKey",
            value = rapidApiKey)
    }

    buildTypes {

        // debug keystore location
        // ~/.android/debug.keystore

        debug {
            isMinifyEnabled = false
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.compose.foundation.layout)
    implementation(libs.androidx.compose.material3)
    implementation(kotlin("script-runtime"))
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.googleid)
    implementation(libs.koin.core) // todo remove
    implementation(libs.koin.android)
    implementation(libs.koin.androidx.compose)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.auth)
    implementation(libs.play.services.auth)
    implementation(libs.firebase.messaging)
    implementation(libs.firebase.database)
    implementation(libs.firebase.crashlytics)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.billing)
    implementation(libs.billing.ktx)
    implementation(libs.app.update)
    implementation(libs.app.update.ktx)
    implementation(libs.dotlottie.android)
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    ksp(libs.kotlin.metadata)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}
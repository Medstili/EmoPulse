import java.util.Properties
import kotlin.collections.forEach
import kotlin.toString

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.medstili.emopulse"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.medstili.emopulse"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        // Load environment variables from local.properties
        val properties = Properties()
        val localPropertiesFile = rootProject.file("local.properties")
        if (localPropertiesFile.exists()) {
            properties.load(localPropertiesFile.inputStream())
            val allowedKeys = listOf("AGENT_SERVICE_BASE_URL", "API_KEY", "FIREBASE_URL")

            allowedKeys.forEach { key ->
                properties.getProperty(key)?.let { value ->
                    buildConfigField("String", key, "\"$value\"")
                }
            }
        }


    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.preference)
    implementation(libs.firebase.database)
    // Import the BoM for the Firebase platform
    implementation(platform(libs.firebase.bom))
    // Add the dependency for the Firebase Authentication library
    // When using the BoM, you don't specify versions in Firebase library dependencies
    implementation(libs.google.firebase.auth)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation (libs.pinview)
    implementation (libs.glide)
    annotationProcessor (libs.compiler)
    implementation(libs.navigation.fragment.ktx)
    implementation(libs.navigation.ui.ktx)
    implementation(libs.lottie)
    implementation (libs.mpandroidchart)
    implementation(libs.dotlottie.android)
    implementation (libs.material.calendarview)
    implementation(libs.play.services.auth)
    implementation (libs.firebase.storage)
    implementation (libs.retrofit)
    implementation (libs.converter.gson)


}



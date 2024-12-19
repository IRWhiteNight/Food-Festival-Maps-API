plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.google.gms.google.services)
    alias(libs.plugins.google.firebase.crashlytics)
}

android {
    namespace = "com.example.aegishub2"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.aegishub2"
        minSdk = 31
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

}

dependencies {
    implementation(libs.androidx.core.ktx) {
        exclude("com.intellij", "annotations")
    }
    implementation(libs.androidx.appcompat) {
        exclude("com.intellij", "annotations")
    }
    implementation(libs.material) {
        exclude("com.intellij", "annotations")
    }
    implementation(libs.androidx.activity.ktx.v140) {
        exclude("com.intellij", "annotations")
    }
    implementation(libs.androidx.constraintlayout) {
        exclude("com.intellij", "annotations")
    }
    implementation(libs.play.services.maps) {
        exclude("com.intellij", "annotations")
    }
    implementation(libs.play.services.location.v1900) {
        exclude("com.intellij", "annotations")
    }
    implementation(libs.androidx.room.compiler) {
        exclude("com.intellij", "annotations")
    }
    implementation(libs.androidx.fragment) {
        exclude("com.intellij", "annotations")
    }
    implementation(libs.places) {
        exclude("com.intellij", "annotations")
    }
    implementation(libs.android.maps.utils) {
        exclude("com.intellij", "annotations")
    }
    implementation(libs.androidx.fragment.ktx) {
        exclude("com.intellij", "annotations")
    }
    implementation(libs.firebase.crashlytics) {
        exclude("com.intellij", "annotations")
    }
    implementation(libs.firebase.analytics) {
        exclude("com.intellij", "annotations")
    }
    implementation(platform(libs.firebase.bom)) {
        exclude("com.intellij", "annotations")
    }
    implementation(libs.androidx.swiperefreshlayout)
    testImplementation(libs.junit) {
        exclude("com.intellij", "annotations")
    }
    androidTestImplementation(libs.androidx.junit.v113) {
        exclude("com.intellij", "annotations")
    }
    androidTestImplementation(libs.androidx.espresso.core.v340) {
        exclude("com.intellij", "annotations")
    }

}
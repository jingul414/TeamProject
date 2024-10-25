plugins {
    id("com.android.application")

    // Add the Google services Gradle plugin
     id("com.google.gms.google-services")
}

android {
    namespace = "com.donghaeng.withme"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.donghaeng.withme"
        minSdk = 23
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
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // 네비게이션바 관련
    implementation ("com.google.android.material:material:1.9.0")

    // 백그라운드
    implementation("androidx.work:work-runtime:2.7.1")

    // firebase
    // Import the Firebase BoM
    implementation(platform("com.google.firebase:firebase-bom:33.4.0"))

    // TODO: Add the dependencies for Firebase products you want to use
    implementation("com.google.firebase:firebase-analytics")    // analytics
    implementation("com.google.firebase:firebase-auth")         // authentication
    implementation("com.google.firebase:firebase-firestore")    // database
}
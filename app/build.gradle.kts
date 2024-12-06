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
        vectorDrawables.useSupportLibrary = true        // Vector 라이브러리 사용
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
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.room.runtime)
    implementation(libs.play.services.nearby)
    implementation(libs.room.common)
    implementation(libs.firebase.appcheck)
    implementation(libs.firebase.appcheck.debug)
    implementation(libs.firebase.appcheck.playintegrity)
    implementation(libs.security.crypto)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // 네비게이션바 관련
    implementation ("com.google.android.material:material:1.9.0")

    // 백그라운드
    implementation("androidx.work:work-runtime:2.7.1")

    // browser
    implementation("androidx.browser:browser:1.8.0")

    // Bcrypt 암호화
    implementation("org.mindrot:jbcrypt:0.4")

    // firebase
    // Import the Firebase BoM
    implementation(platform("com.google.firebase:firebase-bom:33.4.0"))

    // TODO: Add the dependencies for Firebase products you want to use
    implementation("com.google.firebase:firebase-analytics")    // analytics
    implementation("com.google.firebase:firebase-auth")         // authentication
    implementation("com.google.firebase:firebase-firestore")    // database
    implementation("com.google.firebase:firebase-messaging")    // firebase messaging
    implementation("com.squareup.okhttp3:okhttp:4.12.0")

    // CameraX
    implementation(libs.camera.core)
    implementation(libs.camera.camera2)
    implementation(libs.camera.lifecycle)
    implementation(libs.camera.view)

    // ML Kit
    implementation(libs.mlkit.barcode)
    annotationProcessor(libs.room.compiler)

    // Lifecycle components (for CameraX)
    val lifecycleVersion = "2.6.2"
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycleVersion")

    // Kotlin coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3")

    implementation("com.android.support:appcompat-v7:28.0.0")   // Vector Asset

    // QR Code
    implementation ("com.google.zxing:core:3.4.1")

    implementation("androidx.browser:browser:1.8.0")

    // Card View
    implementation ("androidx.cardview:cardview:1.0.0")
    implementation ("com.google.android.material:material:1.9.0") //MaterialCardView

    // JSON
    implementation("com.google.code.gson:gson:2.10.1")

    //gms
    implementation("com.google.android.gms:play-services-auth:20.7.0")
    implementation("com.google.android.gms:play-services-base:18.2.0")

    // 네비게이션 바
    val nav_version = "2.7.6"
    implementation ("androidx.navigation:navigation-fragment:$nav_version")
    implementation ("androidx.navigation:navigation-ui:$nav_version")

    // 이미지 로드 관련
    implementation ("com.squareup.picasso:picasso:2.8")
    implementation ("com.google.firebase:firebase-storage")

    // 보호자 가이드 입력
    implementation ("com.google.android.material:material:1.11.0")
    implementation ("com.google.code.gson:gson:2.10.1")
    implementation ("commons-io:commons-io:2.11.0")  // IOUtils 사용을 위해 필요

    // workmaneger
    implementation ("androidx.work:work-runtime:2.8.1")


}
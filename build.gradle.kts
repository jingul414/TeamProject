// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    // 문제시 아래 지우기
    alias(libs.plugins.android.application) apply false

    // Add the dependency for the Google services Gradle plugin
    id("com.google.gms.google-services") version "4.4.2" apply false

    alias(libs.plugins.kotlin.android) apply false
}

// buildscript 블록은 기존 plugins 블록과 동일한 레벨에 추가
buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        // 다른 클래스패스 의존성들도 필요하다면 여기에 추가
        classpath("com.google.gms:google-services:4.4.2")
    }
}
// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    // 문제시 아래 지우기
    alias(libs.plugins.android.application) apply false

    // Add the dependency for the Google services Gradle plugin
    id("com.google.gms.google-services") version "4.4.2" apply false
}
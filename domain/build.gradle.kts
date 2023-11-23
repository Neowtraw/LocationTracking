plugins {
    id("codingub.android.library")
    id("codingub.android.room")
}

android {
    namespace = "com.codingub.domain"
}

dependencies {
    implementation(libs.kotlinx.coroutines.core)
}
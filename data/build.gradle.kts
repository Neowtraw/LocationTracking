plugins {
    id("codingub.android.library")
    id("codingub.android.room")
}

android {
    namespace = "com.codingub.data"
}

dependencies {
    implementation(libs.kotlinx.coroutines.core)
}
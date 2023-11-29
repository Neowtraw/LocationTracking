import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    id("codingub.android.application")
    id("codingub.android.hilt")
    id("codingub.android.room")
    id("codingub.android.retrofit")
}

val properties = gradleLocalProperties(rootDir)


android {
    namespace = "com.codingub.locationtracking"

    defaultConfig {
        applicationId = AppConfig.applicationId
        versionCode = AppConfig.versionCode
        versionName = AppConfig.versionName

        applicationVariants.all {
            val variant = this
            variant.outputs
                .map { it as com.android.build.gradle.internal.api.BaseVariantOutputImpl }
                .forEach { output ->
                    val outputFileName =
                        "Codingub-${variant.baseName}-${variant.versionName}(${variant.versionCode}).apk"
                    output.outputFileName = outputFileName
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
            buildConfigField("String", "MAPKIT_API_KEY", "\"${properties.getProperty("MAPKIT_API_KEY")}\"")

        }
        debug {
            isMinifyEnabled = false
            buildConfigField("String", "MAPKIT_API_KEY", "\"${properties.getProperty("MAPKIT_API_KEY")}\"")

        }
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true

    }
}

dependencies {

    // androidx
    implementation(libs.androidx.work)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.fragment)
    implementation(libs.androidx.constraintlayout)

    // lifecycle
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)

    // ui
    implementation(libs.countryCodePicker)
    implementation(libs.circleIndicatorView)
    implementation(libs.google.material)


    // navigation
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)

    // firebase
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.firebase.auth.ktx)
    implementation(libs.firebase.auth)
    implementation(libs.google.play.services.auth)

    //geo
    implementation(libs.google.play.services.location)
    implementation(libs.yandex.maps)

    // lottie
    implementation(libs.lottie)

    // timber - test
    implementation(libs.timber)
}
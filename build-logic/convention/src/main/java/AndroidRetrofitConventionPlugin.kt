

import com.codingub.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class AndroidRetrofitConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            dependencies {
                "implementation"(libs.findLibrary("retrofit.core").get())
                "implementation"(libs.findLibrary("retrofit.converter.gson").get())
                "implementation"(libs.findLibrary("okhttp.logging").get())
                "implementation"(libs.findLibrary("google.gson").get())
            }
        }
    }
}
pluginManagement {
    includeBuild("build-logic")
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven {
            url = uri("http://maven.google.com/")
            isAllowInsecureProtocol = true
        }
    }
}

rootProject.name = "LocationTracking"
include(":app")
include(":domain")
include(":data")

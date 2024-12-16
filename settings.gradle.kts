pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        maven {
            url = uri("https://sdk.developer.deepar.ai/maven-android-repository/releases/")
        }
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven {
            url = uri("https://sdk.developer.deepar.ai/maven-android-repository/releases/")
        }
    }
}

rootProject.name = "RAIBurst"
include(":app")

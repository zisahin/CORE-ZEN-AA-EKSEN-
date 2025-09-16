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
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "AA Eksen"
include(":app")
include(":core")
include(":core:data")
include(":core:database")
include(":core:datastore")
include(":core:designsystem")
include(":core:domain")
include(":core:model")
include(":core:network")
include(":feature")
include(":feature:main")
include(":feature:short-news")
include(":feature:live")
include(":feature:account")
include(":build-logic")
include(":feature:news")
include(":feature:time-tunnel")
include(":feature:markdowns")
include(":feature:news-map")
include(":feature:games")
include(":core:ui")

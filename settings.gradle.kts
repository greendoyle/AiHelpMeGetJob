pluginManagement {
    repositories {
        // 1. 国内镜像优先
        maven { url = uri("https://maven.aliyun.com/repository/google") }
        maven { url = uri("https://maven.aliyun.com/repository/gradle-plugin") }
        maven { url = uri("https://mirrors.cloud.tencent.com/maven/google/") }
        maven { url = uri("https://mirrors.cloud.tencent.com/maven/gradle-plugin/") }

        // 2. 官方源兜底，必须加上！
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        // 国内镜像
        maven { url = uri("https://maven.aliyun.com/repository/google") }
        maven { url = uri("https://maven.aliyun.com/repository/public") }
        maven { url = uri("https://mirrors.cloud.tencent.com/maven/google/") }
        maven { url = uri("https://mirrors.cloud.tencent.com/maven/jcenter/") }

        // 官方源兜底
        google()
        mavenCentral()
    }
}

rootProject.name = "AiHelpMeGetJob"
include(":app")

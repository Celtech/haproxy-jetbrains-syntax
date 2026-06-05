plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "2.0.21"
    id("org.jetbrains.intellij.platform") version "2.1.0"
}

group = "com.timhinz"
// Use the git tag (e.g. v1.0.0 → 1.0.0) when building in CI, otherwise fall back to 1.0.0
version = (System.getenv("GITHUB_REF_NAME")?.removePrefix("v")) ?: "1.0.0"

kotlin {
    jvmToolchain(21)
}

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    intellijPlatform {
        // Targeting IC means the plugin works in ALL JetBrains IDEs (PhpStorm, WebStorm, etc.)
        intellijIdeaCommunity("2024.3")
        instrumentationTools()
    }
}

intellijPlatform {
    pluginConfiguration {
        ideaVersion {
            sinceBuild = "243"
        }
    }
    buildSearchableOptions = false

    signing {
        certificateChain = providers.environmentVariable("JETBRAINS_CERTIFICATE_CHAIN")
        privateKey        = providers.environmentVariable("JETBRAINS_PRIVATE_KEY")
        password          = providers.environmentVariable("JETBRAINS_PRIVATE_KEY_PASSWORD")
    }

    publishing {
        token = providers.environmentVariable("JETBRAINS_PUBLISH_TOKEN")
    }
}

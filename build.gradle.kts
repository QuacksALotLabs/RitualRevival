plugins {
    java
    id("com.gradleup.shadow") version "9.6.1"
    id("xyz.jpenilla.run-paper") version "3.1.0"
}

tasks {
    runServer {
        minecraftVersion("1.21.4")
        jvmArgs("-Ddisable.watchdog=true")

        downloadPlugins {
            hangar("CommandAPI", "12.1.0") // oder die aktuellste Version
        }
    }
}

group = project.property("group") as String
version = project.property("version") as String

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.codemc.io/repository/maven-public/")
    // TODO: add the repo that hosts your RevivalAPI `common` module
    // maven("https://jitpack.io")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.3-R0.1-SNAPSHOT")
    compileOnly("net.kyori:adventure-api:4.17.0")
    compileOnly("net.kyori:adventure-text-minimessage:4.17.0")

    // TODO: point at the jar that contains SoulGraveAPI + SoulGraveCreatedEvent
    // compileOnly("dev.quacksalot:revival-api:1.0.0")

    // TODO: ItemUtils coordinates
    // implementation("dev.quacksalot:itemutils:1.0.0")

    implementation("dev.jorel:commandapi-paper-shade:12.1.0")
    implementation("dev.dejvokep:boosted-yaml:1.3")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.17.2")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.17.2")

    testImplementation("org.junit.jupiter:junit-jupiter:5.10.2")
    testImplementation("org.mockbukkit.mockbukkit:mockbukkit-v1.21:4.0.0")
    testImplementation("io.papermc.paper:paper-api:1.21.3-R0.1-SNAPSHOT")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

tasks {
    compileJava {
        options.encoding = "UTF-8"
        options.release.set(21)
    }
    processResources {
        val props = mapOf("version" to project.version)
        filesMatching("paper-plugin.yml") {
            expand(props)
        }
    }
    shadowJar {
        archiveClassifier.set("")
        minimize()
        // CommandAPI: do NOT relocate (it relies on class loading)
        relocate("dev.dejvokep.boostedyaml", "dev.quacksalot.ritualrevival.libs.boostedyaml")
        relocate("com.fasterxml.jackson", "dev.quacksalot.ritualrevival.libs.jackson")
    }
    build { dependsOn(shadowJar) }
    test { useJUnitPlatform() }
}
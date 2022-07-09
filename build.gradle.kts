plugins {
    java
    `java-library`
    `maven-publish`
    id("io.freefair.lombok") version "6.3.0"
}

group = "com.github.rephrasing.cloud"
version = "1.0.0"

repositories {
    mavenCentral()
    maven {
        name = "spigot"
        url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    }
    maven {
        name = "sonatype"
        url = uri("https://oss.sonatype.org/content/repositories/snapshots/")
    }
}

dependencies {
    implementation("org.reflections:reflections:0.10.2")
    compileOnly("org.spigotmc:spigot-api:1.8.8-R0.1-SNAPSHOT")
    api("org.mongodb:mongodb-driver-sync:4.6.0")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}
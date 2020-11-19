import kotlinx.atomicfu.plugin.gradle.sourceSets
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    repositories {
        jcenter()
        maven(url = "https://plugins.gradle.org/m2/")
    }

    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.4.10")
        classpath("org.jetbrains.kotlin:kotlin-serialization:1.4.10")
        classpath("com.jfrog.bintray.gradle:gradle-bintray-plugin:1.8.5")
        classpath("org.jetbrains.kotlinx:atomicfu-gradle-plugin:0.14.4")
    }
}

plugins {
    id("org.jetbrains.kotlin.jvm") version "1.4.10"
}

allprojects {
    group = "cl.lucyteam"
    version = "1.0-SNAPSHOT"

    ext {
        set("ktorVersion", "1.4.1")
        set("kordVersion", "0.6.9")
        set("logbackVersion", "1.2.1")
        set("exposedVersion", "0.28.1")
        set("postgresqlVersion", "42.2.1")
        set("hikariVersion", "3.4.5")
        set("kodeinVersion", "7.1.0")
        set("discord4jVersion", "3.1.1")
    }

    repositories {
        jcenter()
        mavenCentral()
        mavenLocal()
        maven { url = uri("https://kotlin.bintray.com/ktor") }
        maven { url = uri("https://jitpack.io") }
        maven { url = uri("https://dl.bintray.com/kordlib/Kord") }
    }
}


subprojects {
    apply(plugin = "java")
    apply(plugin = "kotlin")
    apply(plugin = "kotlin-kapt")

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict")
            jvmTarget = "1.8"
            incremental = false
        }
    }
}
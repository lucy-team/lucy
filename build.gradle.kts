import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.10" apply false
}

allprojects {
    group = "cl.lucyteam"
    version = "1.0-SNAPSHOT"

    ext {
        set("ktorVersion", "1.4.1")
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
    }
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "kotlin")
    apply(plugin = "kotlin-kapt")

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict",
                "-XXLanguage:+InlineClasses",
                "-Xopt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
                "-Xopt-in=kotlin.Experimental")
            jvmTarget = "1.8"
            incremental = false
        }
    }
}
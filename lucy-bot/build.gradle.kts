val discord4jVersion: String by ext
val ktorVersion: String by ext

plugins {
    id("org.jetbrains.kotlin.kapt")
}

val jar by tasks.getting(Jar::class) {
    manifest {
        attributes["Main-Class"] = "MainKt"
    }

    from(sourceSets.main.get().output)
    dependsOn(configurations.runtimeClasspath)

    from({
        configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
    })
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(project(":lucy-database"))

    implementation("org.koin:koin-core:2.1.6")
    implementation("com.discord4j:discord4j-core:$discord4jVersion")

    implementation("com.gitlab.kordlib.kordx:kordx-commands-runtime:0.3.4")
    implementation("info.debatty:java-string-similarity:1.2.1")
    implementation("io.github.cdimascio:dotenv-kotlin:6.2.2")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.0.1")

    kapt("com.gitlab.kordlib.kordx:kordx-commands-processor:0.3.4")
    //kapt

    implementation("com.sedmelluq:lavaplayer:1.3.60")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.0-M1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:1.4.0-M1")
    implementation("org.kodein.di:kodein-di:7.1.0")
    implementation("io.ktor:ktor-client-cio:$ktorVersion")
    implementation("io.ktor:ktor-client-json:$ktorVersion")
    implementation("io.ktor:ktor-client-serialization-jvm:$ktorVersion")
    // Falta libreria para ver los casos de testing
}

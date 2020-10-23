val discord4jVersion: String by ext

plugins {
    id("org.jetbrains.kotlin.kapt")
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(project(":lucy-database"))

    implementation("org.koin:koin-core:2.1.6")
    implementation("com.discord4j:discord4j-core:$discord4jVersion")
    implementation("com.gitlab.kordlib.kordx:kordx-commands-runtime:0.3.4")
    implementation("info.debatty:java-string-similarity:1.2.1")

    kapt("com.gitlab.kordlib.kordx:kordx-commands-processor:0.3.4")
    //kapt

    implementation("com.sedmelluq:lavaplayer:1.3.50")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.0-M1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:1.4.0-M1")
    implementation("org.kodein.di:kodein-di:7.1.0")
    // Falta libreria para ver los casos de testing
}

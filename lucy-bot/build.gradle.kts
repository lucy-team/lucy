val discord4jVersion: String by ext

plugins {
    kotlin("jvm")
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(project(":lucy-common"))

    implementation("com.discord4j:discord4j-core:$discord4jVersion")
    // Falta libreria para ver los casos de testing
}

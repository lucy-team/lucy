val ktorVersion: String by ext
val logbackVersion: String by ext
val kodeinVersion: String by ext
val postgresqlVersion: String by ext
val hikariVersion: String by ext
val exposedVersion: String by ext
val kordVersion: String by ext

plugins {
    application
    kotlin("jvm")
}

application {
    mainClassName = "io.ktor.server.netty.EngineMain"
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(project(":lucy-database"))

    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("ch.qos.logback:logback-classic:$logbackVersion")
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-jackson:$ktorVersion")

    implementation("org.kodein.di:kodein-di-framework-ktor-server-jvm:$kodeinVersion")
    implementation("com.zaxxer:HikariCP:$hikariVersion") // JDBC Connection Pool
    implementation("org.postgresql:postgresql:$postgresqlVersion") // JDBC Connector for PostgreSQL

    implementation("com.gitlab.kordlib.kord:kord-rest:$kordVersion")

    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")

    testImplementation("io.ktor:ktor-server-tests:$ktorVersion")
}

kotlin.sourceSets["main"].kotlin.srcDirs("src")
kotlin.sourceSets["test"].kotlin.srcDirs("test")

sourceSets["main"].resources.srcDirs("resources")
sourceSets["test"].resources.srcDirs("testresources")
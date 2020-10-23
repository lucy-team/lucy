val exposedVersion: String by ext
val postgresqlVersion: String by ext
val hikariVersion: String by ext

plugins {
    kotlin("jvm")
}

dependencies {
    implementation(kotlin("stdlib"))

    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")

    implementation("com.zaxxer:HikariCP:$hikariVersion") // JDBC Connection Pool
    implementation("org.postgresql:postgresql:$postgresqlVersion") // JDBC Connector for PostgreSQL
}

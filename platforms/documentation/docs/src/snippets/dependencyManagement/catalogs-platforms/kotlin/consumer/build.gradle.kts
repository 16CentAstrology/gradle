plugins {
    application
}

repositories {
    mavenCentral()
}

// tag::usage[]
dependencies {
    // Platform
    implementation(platform(project(":platform")))
    // Catalog
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.jupiter.launcher)
    implementation(libs.guava)
}
// end::usage[]

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

application {
    mainClass = "org.example.App"
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.10"
    application
}

group = "me.tim"
version = ""

repositories {
    mavenCentral()
    mavenLocal()
//    maven("https://dl.bintray.com/kotlin/kotlin-eap")
    maven("https://plugins.gradle.org/m2/")
    maven("https://jitpack.io")
}

dependencies {
    implementation("com.github.stelar7:R4J:2.0.7")
    implementation("dev.kord:kord-core:0.8.0-M8")
    implementation("com.google.code.gson:gson:2.8.9")
    implementation("com.github.twitch4j:twitch4j:1.6.0")
    implementation(kotlin("stdlib-jdk8"))
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

application {
    mainClass.set("MainKt")
}

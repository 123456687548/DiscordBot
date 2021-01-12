import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.21"
    application
}

group = "me.tim"
version = "1.0-SNAPSHOT"

application {
    mainClass.set("Main")
}

repositories {
    mavenCentral()
    jcenter()
    maven("https://dl.bintray.com/kordlib/Kord")
}

dependencies {
    implementation("com.merakianalytics.orianna:orianna:4.0.0-rc7")
    implementation("dev.kord:kord-core:0.7.0-RC")
    implementation("com.google.code.gson:gson:2.8.6")
    implementation("com.github.twitch4j:twitch4j:1.1.2")
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "1.8"
}
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.21"
    application
}

group = "me.tim"
version = ""

application {
    mainClass.set("Main")
}

repositories {
    mavenCentral()
    mavenLocal()
    jcenter()
    maven("https://oss.sonatype.org/content/repositories/snapshots")
    maven("https://dl.bintray.com/kotlin/kotlin-eap")
    maven("https://jitpack.io")
}

dependencies {
    implementation("com.github.stelar7:R4J:2.0.7")
    implementation("dev.kord:kord-core:0.8.0-M4")
    implementation("com.google.code.gson:gson:2.8.6")
    implementation("com.github.twitch4j:twitch4j:1.1.2")
    implementation(kotlin("stdlib-jdk8"))
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "1.8"
}
val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "1.8"
}
val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = "1.8"
}

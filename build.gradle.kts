/*
 * Copyright (c) enveeed 2019 - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

import java.io.ByteArrayOutputStream

plugins {
    id("java")
    id("application")
    id("idea")

    id("de.fuerstenau.buildconfig") version "1.1.8"
}

// ======

// gets the git hash of the last commit to use it in the version number
val gitHash = {
    val stdout = ByteArrayOutputStream()
    exec {
        commandLine("git", "-C", project.projectDir, "rev-parse", "--short", "HEAD")
        standardOutput = stdout
    }
    stdout.toString().trim()
}.invoke()

// ======

version = "2019.0.0-beta-$gitHash"
group = "cynoodle"

// ===

buildConfig {
    appName = project.name
    version = project.version.toString()
    packageName = "cynoodle.core"
}

// ===

repositories {
    mavenCentral()
    jcenter()

    maven("https://jitpack.io") // Jitpack.io
    maven("https://dl.bintray.com/enveeed/carambola")
}

// ===

application {
    applicationName = "cynoodle-core"
    mainClassName = "cynoodle.core.Launcher"
}

// ===

delegateClosureOf<Test> {
    useJUnitPlatform()
}

// ===

dependencies {

    // APIs

    implementation(group = "net.dv8tion",                   name = "JDA",                       version = "3.8.3_460") // JDA (Discord API)
    implementation(group = "org.mongodb",                   name =  "mongo-java-driver",        version = "3.10.1") // MongoDB Java Driver (MongoDB API)

    // Utilities / Libraries

    implementation(group = "com.google.guava",              name = "guava",                     version = "27.0.1-jre") // Google Guava (Common Java Utils)
    implementation(group = "com.google.flogger",            name = "flogger",                   version = "0.3.1") // Google Flogger API (Fluent Logging)
    implementation(group = "com.google.flogger",            name = "flogger-system-backend",    version = "0.3.1") // TODO Google Flogger System Backend (Fluent Logging)
    implementation(group = "com.google.code.gson",          name = "gson",                      version = "2.8.5") // Google GSON (JSON library)

    implementation(group =  "org.eclipse.collections",      name = "eclipse-collections",       version = "9.2.0")
    implementation(group =  "org.eclipse.collections",      name = "eclipse-collections-api",   version = "9.2.0")

    implementation(group = "com.fasterxml.jackson.core",    name = "jackson-core",              version = "2.9.8") // Jackson JSON API

    implementation(group = "com.github.jillesvangurp",      name = "jsonj",                     version = "v2.56") // JsonJ fluent JSON API

    implementation(group = "enveeed.carambola",             name = "carambola-core",            version = "0.0.1-beta6")

    implementation(group = "enveeed.carambola",             name = "carambola-flogger",         version = "0.0.1-beta6")
    implementation(group = "enveeed.carambola",             name = "carambola-slf4j",           version = "0.0.1-beta6")

    // base:fm

    implementation(group = "de.u-mass",                     name = "lastfm-java",               version = "0.1.2") // Last FM API wrapper (TODO replace with own wrapper)
    
    // Unit Testing

    testImplementation( group = "org.junit.jupiter", name = "junit-jupiter-api",    version = "5.3.2") // JUnit API
    testRuntime(        group = "org.junit.jupiter", name = "junit-jupiter-engine", version = "5.3.2") // JUnit Engine
}
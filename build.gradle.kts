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
    implementation(group = "net.dv8tion",                   name = "JDA",                       version = "3.8.3_462")  // JDA (Discord)
    implementation(group = "org.mongodb",                   name =  "mongo-java-driver",        version = "3.10.1")     // MongoDB Java Driver
    implementation(group = "de.u-mass",                     name = "lastfm-java",               version = "0.1.2")      // last.fm

    // Utilities / Libraries
    implementation(group = "com.google.guava",              name = "guava",                     version = "27.0.1-jre") // Google Guava
    implementation(group = "com.google.flogger",            name = "flogger",                   version = "0.3.1")      // Google Flogger API
    implementation(group = "org.eclipse.collections",       name = "eclipse-collections",       version = "9.2.0")      // Eclipse Collections
    implementation(group = "org.eclipse.collections",       name = "eclipse-collections-api",   version = "9.2.0")      // Eclipse Collections API
    implementation(group = "com.fasterxml.jackson.core",    name = "jackson-core",              version = "2.9.8")      // Jackson (JSON)
    implementation(group = "com.github.jillesvangurp",      name = "jsonj",                     version = "v2.56")      // JsonJ (JSON)


    // Logging
    implementation(group = "enveeed.carambola",             name = "carambola-core",            version = "0.0.10")      // carambola
    implementation(group = "enveeed.carambola",             name = "carambola-flogger",         version = "0.0.10")      // carambola (Flogger)
    implementation(group = "enveeed.carambola",             name = "carambola-slf4j",           version = "0.0.10")      // carambola (SLF4J)
    implementation(group = "enveeed.carambola",             name = "carambola-jul",             version = "0.0.10")      // carambola (JUL)

    // API
    implementation(group = "com.graphql-java",              name = "graphql-java",              version = "12.0")       // GraphQL

    // Legacy TODO This is for legacy imports only
    implementation(group = "com.google.protobuf",           name = "protobuf-java",             version = "+")          // Google Protobuf

    // Testing
    testImplementation( group = "org.junit.jupiter", name = "junit-jupiter-api",    version = "5.3.2")  // JUnit API
    testRuntime(        group = "org.junit.jupiter", name = "junit-jupiter-engine", version = "5.3.2")  // JUnit Engine

}
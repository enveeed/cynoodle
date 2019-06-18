/*
 * cynoodle, a bot for the chat platform Discord
 *
 * Copyright (C) 2019 enveeed
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 * All trademarks are the property of their respective owners, including, but not limited to Discord Inc.
 */

import java.io.ByteArrayOutputStream

// plugins

plugins {
    id("java")
    id("idea")
}

// gets the git hash of the last commit to use it in the version number
val gitHash = {
    val stdout = ByteArrayOutputStream()
    exec {
        commandLine("git", "-C", project.projectDir, "rev-parse", "--short", "HEAD")
        standardOutput = stdout
    }
    stdout.toString().trim()
}.invoke()

//

version = "2019.1.0-dev-$gitHash"
group = "cynoodle"


// configuration for all module projects
configure(
        allprojects.filter {
            it.projectDir.startsWith(file("modules"))
        }) {

    apply(plugin = "java")
    apply(plugin = "idea")
    
    version = rootProject.version
    group = "modules"

    repositories {
        mavenCentral()
        jcenter()
        maven("https://jitpack.io") // Jitpack.io
    }

    delegateClosureOf<Test> {
        useJUnitPlatform()
    }

    dependencies {
        implementation(project(":cynoodle"))
    }
}
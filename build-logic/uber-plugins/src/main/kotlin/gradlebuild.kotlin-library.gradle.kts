/*
 * Copyright 2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import gradlebuild.basics.accessors.kotlin
import org.gradle.api.internal.initialization.DefaultClassLoaderScope
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile


plugins {
    kotlin("jvm")
    id("gradlebuild.java-library")
    id("gradlebuild.ktlint")
}

configurations.transitiveSourcesElements {
    val main = sourceSets.main.get()
    main.kotlin.srcDirs.forEach {
        outgoing.artifact(it)
    }
}

tasks {
    withType<KotlinCompile>().configureEach {
        configureKotlinCompilerForGradleBuild()
    }

    codeQuality {
        dependsOn(ktlintCheck)
    }

    runKtlintCheckOverKotlinScripts {
        // Only check the build files, not all *.kts files in the project
        includes += listOf("*.gradle.kts")
    }

    withType<Test>().configureEach {

        shouldRunAfter(ktlintCheck)

        // enables stricter ClassLoaderScope behaviour
        systemProperty(
            DefaultClassLoaderScope.STRICT_MODE_PROPERTY,
            true
        )
    }
}

fun KotlinCompile.configureKotlinCompilerForGradleBuild() {
    compilerOptions {
        allWarningsAsErrors.set(true)
        apiVersion.set(KotlinVersion.KOTLIN_1_8)
        languageVersion.set(KotlinVersion.KOTLIN_1_8)
        jvmTarget.set(JvmTarget.JVM_1_8)
        freeCompilerArgs.addAll(
            "-Xjsr305=strict",
            "-java-parameters",
            "-Xsam-conversions=class",
            "-Xskip-metadata-version-check",
        )
    }
}

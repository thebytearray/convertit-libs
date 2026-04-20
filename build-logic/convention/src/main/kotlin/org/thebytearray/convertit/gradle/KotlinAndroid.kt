package org.thebytearray.convertit.gradle

import org.gradle.api.Project
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

/**
 * Shared Kotlin options for Android modules (AGP 9+ provides Kotlin support without
 * `org.jetbrains.kotlin.android`; we still align all [KotlinCompile] tasks to JVM 17).
 */
internal fun Project.configureKotlinAndroid() {
    tasks.withType<KotlinCompile>().configureEach {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
            freeCompilerArgs.add("-java-parameters")
        }
    }
}

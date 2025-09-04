import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.KotlinJsCompile

plugins {
    kotlin("multiplatform") version "2.2.10"
    kotlin("plugin.serialization") version "2.2.10"
}

group = "de.stefan_oltmann"

repositories {
    mavenCentral()
}

kotlin {

    js(IR) {
        nodejs()
        binaries.executable()
    }

    sourceSets {

        jsMain.dependencies {

            /* Standard libraries */
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
            implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.7.1-0.6.x-compat")

            /* JSON */
            implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.9.0")

            /* JWT */
            implementation("com.appstractive:jwt-kt-js:1.2.1")
            implementation("com.appstractive:jwt-ecdsa-kt:1.2.1")
        }
    }
}

tasks.withType<KotlinJsCompile>().configureEach {
    compilerOptions {
        target.set("es2015")
    }
}

tasks.named("jsProductionExecutableCompileSync") {

    val entrypointFile = "${layout.buildDirectory.asFile.get()}/js/packages/${project.name}/kotlin/${project.name}.mjs"

    outputs.file(entrypointFile)

    val jsEntrypoint = """
            /* The entrypoint expected by Cloudflare */
            export default {
                fetch(request, env, ctx) {
                    return handleRequest(request, env, ctx);
                },
            };
        """.trimIndent()

    doLast {
        File(entrypointFile).appendText(jsEntrypoint)
    }
}

/*
 * Copyright 2020 Marco Gomiero
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.util.Properties;

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("com.mikepenz.aboutlibraries.plugin")
}

val local = Properties()
val localProperties: File = rootProject.file("keystore.properties")
if (localProperties.exists()) {
    localProperties.inputStream().use { local.load(it) }
}

android {
    namespace = "com.prof18.secureqrreader"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.prof18.secureqrreader"
        minSdk = 24
        targetSdk = 33
        versionCode = 20000
        versionName = "2.0.0"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    signingConfigs {
        create("release") {
            keyAlias = local.getProperty("keyAlias")
            keyPassword = local.getProperty("keyPassword")
            storeFile = file(local.getProperty("storeFile") ?: "NOT_FOUND")
            storePassword = local.getProperty("storePassword")
        }
    }

    buildTypes {
        getByName("release") {
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("release")
        }
    }

    buildFeatures { compose = true }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        freeCompilerArgs = freeCompilerArgs + "-opt-in=com.google.accompanist.permissions.ExperimentalPermissionsApi" + "-opt-in=androidx.compose.animation.ExperimentalAnimationApi"
        jvmTarget = libs.versions.java.get()
    }
}

dependencies {
    implementation(libs.bundles.compose)
    implementation(libs.bundles.accompanist)
    implementation(libs.androidx.datastore.preference)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.android.material)
    implementation(libs.accompanist.systemuicontroller)

    // Not upgrade, because otherwise will work only on api > 24
    implementation(libs.zxing.android.embedded) {
        isTransitive = false
    }
    // Not upgrade, because otherwise will work only on api > 24
    implementation(libs.zxing.android.embedded)

    implementation(libs.bundles.about.libraries)

}

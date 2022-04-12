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
import org.apache.commons.io.output.ByteArrayOutputStream

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("com.mikepenz.aboutlibraries.plugin")
}

android {

    compileSdk = 32
    defaultConfig {
        applicationId = "com.prof18.secureqrreader"
        minSdk = 23
        targetSdk = 32
        versionCode = /*getVersionCode()*/ 10002 // TODO: maybe change it
        versionName = getVersionName()
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

//    signingConfigs {
//        create("release") {
//            keyAlias = local.getProperty("keyAlias")
//            keyPassword = local.getProperty("keyPassword")
//            storeFile = file(local.getProperty("storeFile") ?: "NOT_FOUND")
//            storePassword = local.getProperty("storePassword")
//        }
//    }

    buildTypes {
        getByName("release") {
//            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            isMinifyEnabled = false
//            signingConfig = signingConfigs.getByName("release")
        }
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.get()
    }

    buildFeatures { compose = true }
}

dependencies {
    implementation(libs.bundles.compose)
    implementation("androidx.core:core-ktx:1.3.1")
    implementation("androidx.appcompat:appcompat:1.1.0")
    implementation("com.google.android.material:material:1.1.0")
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("androidx.recyclerview:recyclerview:1.1.0")
    implementation("androidx.constraintlayout:constraintlayout:1.1.3")
    // Not upgrade, because otherwise will work only on api > 24
    implementation("com.journeyapps:zxing-android-embedded:4.1.0") {
        isTransitive = false
    }
    // Not upgrade, because otherwise will work only on api > 24
    implementation("com.google.zxing:core:3.3.0")
    implementation("pub.devrel:easypermissions:3.0.0")

    implementation("com.mikepenz:aboutlibraries-core:10.1.0")
    implementation("com.mikepenz:aboutlibraries:10.1.0")

    testImplementation("junit:junit:4.12")
    androidTestImplementation("androidx.test.ext:junit:1.1.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.2.0")

}


fun getVersionCode(): Int {
    val outputStream = ByteArrayOutputStream()
    project.exec {
        commandLine = "git rev-list HEAD --first-parent --count".split(" ")
        standardOutput = outputStream
    }
    return outputStream.toString().trim().toInt()
}


fun getVersionName(): String {
    val outputStream = ByteArrayOutputStream()
    project.exec {
        commandLine = "git describe --tags --abbrev=0".split(" ")
        standardOutput = outputStream
    }
    return outputStream.toString().trim()
}
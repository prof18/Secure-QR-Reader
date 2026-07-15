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
    alias(libs.plugins.android.application)
    alias(libs.plugins.about.libraries)
    alias(libs.plugins.triplet.play)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.baseline.profile)
}

val local = Properties()
val localProperties: File = rootProject.file("keystore.properties")
if (localProperties.exists()) {
    localProperties.inputStream().use { local.load(it) }
}
fun revenueCatApiKey(propertyName: String): String = providers.gradleProperty(propertyName)
    .orElse(local.getProperty(propertyName) ?: "")
    .get()

val testStoreRevenueCatApiKey = revenueCatApiKey("revenueCatTestStoreApiKey")
val productionRevenueCatApiKey = revenueCatApiKey("revenueCatProductionApiKey")

android {
    namespace = "com.prof18.secureqrreader"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.prof18.secureqrreader"
        minSdk = 24
        targetSdk = 36
        versionCode = 20010
        versionName = "2.0.9"
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
        getByName("debug") {
            buildConfigField("String", "REVENUECAT_API_KEY", "\"$testStoreRevenueCatApiKey\"")
        }

        getByName("release") {
            buildConfigField("String", "REVENUECAT_API_KEY", "\"$productionRevenueCatApiKey\"")
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            isMinifyEnabled = true
            isShrinkResources = true
            signingConfig = signingConfigs.getByName("release")
        }
    }

    sourceSets {
        getByName("debug").res.srcDir("build/generated/aboutLibraries/debug/res")
        getByName("release").res.srcDir("build/generated/aboutLibraries/release/res")
    }

    buildFeatures {
        buildConfig = true
        compose = true
    }

}

play {
    // The play_config.json file will be provided on CI
    serviceAccountCredentials.set(file("../play_config.json"))
    track.set("internal")
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.bundles.compose)
    implementation(libs.accompanist.permissions)
    implementation(libs.androidx.datastore.preference)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.android.material)
    implementation(libs.zxing.android.embedded)
    implementation(libs.bundles.about.libraries)
    implementation(libs.revenuecat.purchases)
    implementation(libs.revenuecat.purchases.ui)
    testImplementation(libs.kotlin.test.junit)
    baselineProfile(project(":baseline-profile"))
}

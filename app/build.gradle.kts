/*
 * Copyright (c) 2024 Ashish Yadav <mailtoashish693@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

// Release workflow passes versionCode + versionName via -P flags (see
// .github/workflows/release.yml) so we don't have to commit a version bump
// to ship a release. Local builds fall back to the hardcoded values below.
val overrideVersionCode: Int? = (project.findProperty("versionCode") as? String)?.toIntOrNull()
val overrideVersionName: String? = project.findProperty("versionName") as? String

android {
    namespace = "com.criticalay.neer"
    compileSdk = 36

    signingConfigs {
        create("release") {
            val storePath = System.getenv("KEYSTORE_FILE")
            if (!storePath.isNullOrBlank()) {
                storeFile = file(storePath)
                storePassword = System.getenv("KEYSTORE_PASSWORD")
                keyAlias = System.getenv("KEY_ALIAS")
                keyPassword = System.getenv("KEY_PASSWORD")
            }
        }
    }

    defaultConfig {
        applicationId = "com.criticalay.neer"
        minSdk = 25
        targetSdk = 36

        // The version number is of the form:
        // <major>.<minor>.<maintenance>[dev|alpha<build>|beta<build>|]
        // The <build> is only present for alpha and beta releases (e.g., 2.0.4alpha2 or 2.0.4beta4), developer builds do
        // not have a build number (e.g., 2.0.4dev) and official releases only have three components (e.g., 2.0.4).
        //
        // The version code is derived from the version name as follows:
        // AbbCCtDD
        // A: 1-digit decimal number representing the major version
        // bb: 2-digit decimal number representing the minor version
        // CC: 2-digit decimal number representing the maintenance version
        // t: 1-digit decimal number representing the type of the build
        // 0: developer build
        // 1: alpha release
        // 2: beta release
        // 3: public release
        // DD: 2-digit decimal number representing the build
        // 00 for internal builds and public releases
        // alpha/beta build number for alpha/beta releases
        //
        // This ensures the correct ordering between the various types of releases (dev < alpha < beta < release) which is
        // needed for upgrades to be offered correctly.
        versionCode = overrideVersionCode ?: 10300101
        versionName = overrideVersionName ?: "1.03alpha1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            // Sign with the release keystore when env vars are supplied (CI);
            // otherwise fall back to the debug key so local `assembleRelease`
            // still produces an installable APK.
            signingConfig = if (System.getenv("KEYSTORE_FILE").isNullOrBlank())
                signingConfigs.getByName("debug")
            else
                signingConfigs.getByName("release")
        }
        debug {
            versionNameSuffix = "-debug"
            isDebuggable = true
            applicationIdSuffix = ".debug"
        }
    }
    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.runtime.ktx)
    implementation(libs.androidx.navigation.compose)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.material.icons.extended)
    implementation(libs.timber)
    // room
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)
    // hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)
    // desugaring
    coreLibraryDesugaring(libs.desugar.jdk.libs.nio)
    implementation( libs.androidx.datastore.preferences)
    implementation(libs.accompanist.permissions)
    // widgets
    implementation(libs.androidx.glance.appwidget)
    implementation(libs.androidx.glance.material3)

}

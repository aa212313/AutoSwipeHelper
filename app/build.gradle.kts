import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

// 签名信息只从环境变量读取，不落盘、不进仓库；缺失时自动跳过签名配置
val ksPath = System.getenv("AUTOSWIPE_KEYSTORE")
val ksPass = System.getenv("AUTOSWIPE_KEYSTORE_PASSWORD")
val keyAliasEnv = System.getenv("AUTOSWIPE_KEY_ALIAS")
val keyPass = System.getenv("AUTOSWIPE_KEY_PASSWORD")

android {
    namespace = "com.autoswipe.helper"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.autoswipe.helper"
        minSdk = 26
        targetSdk = 34
        versionCode = 2
        versionName = "1.1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        if (ksPath != null) {
            create("release") {
                storeFile = file(ksPath)
                storePassword = ksPass
                keyAlias = keyAliasEnv
                keyPassword = keyPass
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            // 有签名配置才启用，否则产出未签名包
            signingConfig = signingConfigs.findByName("release")
        }
        debug {
            isMinifyEnabled = false
            applicationIdSuffix = ".debug"
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    // AndroidX Core
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.activity:activity-ktx:1.8.1")
    implementation("androidx.fragment:fragment-ktx:1.6.2")

    // Material Design
    implementation("com.google.android.material:material:1.11.0")

    // Lifecycle
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-service:2.7.0")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // DataStore (替代 SharedPreferences)
    implementation("androidx.datastore:datastore-preferences:1.0.0")

    // Notification (AndroidX)
    implementation("androidx.core:core:1.12.0")
}

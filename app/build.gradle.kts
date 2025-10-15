plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    kotlin("plugin.serialization") version "1.9.20"
}

import java.util.Properties

// Load .env file
val envFile = rootProject.file(".env")
val envProperties = Properties()
if (envFile.exists()) {
    envFile.inputStream().use { envProperties.load(it) }
}

fun getEnvProperty(key: String, defaultValue: String = ""): String {
    return envProperties.getProperty(key) ?: System.getenv(key) ?: defaultValue
}

android {
    namespace = "com.example.engpu"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.engpu"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Add BuildConfig fields from .env
        buildConfigField("String", "SUPABASE_URL", "\"${getEnvProperty("SUPABASE_URL")}\"")
        buildConfigField("String", "SUPABASE_ANON_KEY", "\"${getEnvProperty("SUPABASE_ANON_KEY")}\"")
        buildConfigField("String", "OPENAI_API_KEY", "\"${getEnvProperty("OPENAI_API_KEY")}\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }

    packaging {
        resources {
            excludes += "/META-INF/LICENSE.md"
            excludes += "/META-INF/NOTICE.md"
            excludes += "/META-INF/LICENSE"
            excludes += "/META-INF/NOTICE"
        }
    }
}

dependencies {
    // Core Android dependencies
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    
    // Compose Navigation
    implementation("androidx.navigation:navigation-compose:2.7.7")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    
    // Icons and animations
    implementation("androidx.compose.material:material-icons-extended:1.6.2")
    implementation("androidx.compose.animation:animation:1.6.2")
    
    // Data storage
    implementation("androidx.datastore:datastore-preferences:1.0.0")
    
    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    
    // System UI Controller
    implementation("com.google.accompanist:accompanist-systemuicontroller:0.32.0")
    
    // Supabase
    implementation("io.github.jan-tennert.supabase:postgrest-kt:2.1.6")
    implementation("io.github.jan-tennert.supabase:gotrue-kt:2.1.6")
    implementation("io.github.jan-tennert.supabase:realtime-kt:2.1.6")
    implementation("io.ktor:ktor-client-android:2.3.7")
    implementation("io.ktor:ktor-client-core:2.3.7")
    implementation("io.ktor:ktor-utils:2.3.7")
    implementation("io.ktor:ktor-client-content-negotiation:2.3.7")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.7")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.2")

    // Apache POI for Excel parsing
    implementation("org.apache.poi:poi:5.2.5")
    implementation("org.apache.poi:poi-ooxml:5.2.5")

    // JavaMail for SMTP email sending
    implementation("com.sun.mail:android-mail:1.6.7")
    implementation("com.sun.mail:android-activation:1.6.7")
    
    // Test dependencies
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}
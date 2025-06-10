plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android") // Terapkan plugin utama Kotlin
    id("kotlin-kapt")                 // Sekarang Kapt akan dikenali sebagai bagian dari Kotlin
}

android {
    namespace = "com.example.komikfinale"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.komikfinale"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.legacy.support.v4)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // ViewModel & LiveData (untuk arsitektur)
    implementation("androidx.lifecycle:lifecycle-viewmodel:2.8.2")
    implementation("androidx.lifecycle:lifecycle-livedata:2.8.2")

// Retrofit (untuk mengambil data dari API)
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

// Glide (untuk memuat gambar dari URL)
    implementation("com.github.bumptech.glide:glide:4.16.0")

// RecyclerView (untuk menampilkan daftar)
    implementation("androidx.recyclerview:recyclerview:1.3.2")

// Navigation Component (persiapan untuk Fase 2)
    implementation("androidx.navigation:navigation-fragment:2.7.7")
    implementation("androidx.navigation:navigation-ui:2.7.7")

    val room_version = "2.6.1"

    implementation("androidx.room:room-runtime:$room_version")
    // Ganti 'annotationProcessor' dengan 'kapt'
    kapt("androidx.room:room-compiler:$room_version")
    // --- AKHIR DARI DEPENDENSI ROOM ---

}
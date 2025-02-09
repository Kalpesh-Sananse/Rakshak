plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.kalpesh.women_safety"
    compileSdk = 35



        viewBinding {
            enable = true
        }


    defaultConfig {
        applicationId = "com.kalpesh.women_safety"
        minSdk = 24
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.firebase.database)
    implementation("com.google.firebase:firebase-database:20.2.2")
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation ("com.squareup.okhttp3:logging-interceptor:4.9.3")
    implementation ("com.squareup.okhttp3:okhttp:4.11.0") // For OkHttp
    implementation  ("com.squareup.retrofit2:retrofit:2.9.0") // For Retrofit
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0") // Optional for JSON parsing
    implementation ("androidx.recyclerview:recyclerview:1.2.1")
    implementation ("androidx.core:core-ktx:1.9.0")
    implementation ("com.google.firebase:firebase-auth:21.4.3")
    implementation ("com.google.android.gms:play-services-location:21.0.1")
    implementation("com.squareup.picasso:picasso:2.8")
    implementation ("androidx.core:core:1.9.0")
    implementation("io.coil-kt:coil:2.4.0")
    implementation("io.coil-kt:coil-base:2.4.0") // Optional for basic image loading

    implementation ("com.google.firebase:firebase-database:20.2.2")
    implementation( "com.github.bumptech.glide:glide:4.15.1")
    implementation("io.coil-kt:coil:2.4.0")
    implementation(libs.androidx.lifecycle.service)

    val cameraxVersion = "1.3.1"
    implementation("androidx.camera:camera-core:$cameraxVersion")
    implementation("androidx.camera:camera-camera2:$cameraxVersion")
    implementation("androidx.camera:camera-lifecycle:$cameraxVersion")
    implementation("androidx.camera:camera-view:$cameraxVersion")

    // ML Kit
    implementation("com.google.mlkit:face-detection:16.1.5")

    // Other dependencies
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation(libs.firebase.auth.ktx)
    implementation(libs.androidx.recyclerview)
    implementation(libs.volley)


    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.example.gymtracker"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.gymtracker"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        /*testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"*/
        testInstrumentationRunner = "com.example.gymtracker.utils.CustomTestRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
        jvmTarget = JavaVersion.VERSION_17.toString()
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.3"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    val room_version = "2.6.1"
    val nav_version = "2.7.5"

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation("androidx.activity:activity-compose:1.8.1")
    implementation(platform("androidx.compose:compose-bom:2023.03.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation ("androidx.compose.material3:material3:1.1.2")
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.jetbrains.kotlin:kotlin-test:1.9.10")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.03.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
    implementation ("androidx.lifecycle:lifecycle-runtime-compose:2.6.2")
    implementation ("androidx.lifecycle:lifecycle-extensions:2.2.0")

    // Navigation
    implementation("androidx.navigation:navigation-compose:$nav_version")
    androidTestImplementation ("androidx.navigation:navigation-testing:$nav_version")

    // COmpose animation
    implementation("androidx.compose.animation:animation:1.5.4")

    //Immutable lists
    implementation("org.jetbrains.kotlinx:kotlinx-collections-immutable:0.3.6")

    //Room
    implementation("androidx.room:room-runtime:$room_version")
    ksp("androidx.room:room-compiler:$room_version")
    implementation("androidx.room:room-ktx:$room_version")
    testImplementation("androidx.room:room-testing:$room_version")

    //Hilt
    implementation("com.google.dagger:hilt-android:2.48.1")
    ksp("com.google.dagger:hilt-android-compiler:2.48.1")
    implementation ("androidx.hilt:hilt-navigation-compose:1.1.0")
    androidTestImplementation("com.google.dagger:hilt-android-testing:2.48.1")
    kspAndroidTest("com.google.dagger:hilt-android-compiler:2.48.1")

    //Coroutines test
    testImplementation ("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")

    //Preferences Datastore
    implementation ("androidx.datastore:datastore-preferences:1.0.0")

}



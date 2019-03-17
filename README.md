# Boiler

Generates boilerplate codes for parsing class to Map and vice versa.

## Adding Dependency

Add the below lines in the project level build.gradle file.

```groovy
buildscript {
    repositories {
        maven {
            url  "https://dl.bintray.com/mraj0045/maven"
        }
    }
}
```

Add the following code in the app level gradle file (app/build.gragle)

```groovy
apply plugin: 'kotlin-kapt'

android {
    compileOptions {
        sourceCompatibility JavaVersion.VERSION_1_8
        targetCompatibility JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation 'com.boiler:boiler:0.0.1'
    kapt 'com.boiler:boiler-compiler:0.0.1'
}
```
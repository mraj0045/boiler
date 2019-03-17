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

## Usage

```kotlin
@MapBuilder(suffix = "query")
data class Post(
    @Entry(key = "name")
    val name: String?,
    @Entry(key = "age")
    val age: Int?,
    @Ignore
    val test: String? = "",
    val dob: String?
)
```
Annotation are explained below,
* MapBuilder - Annotate the class to generate a builder class. Generated class will be "class name"+"Builder" format. "Builder" can be changed using the suffix value.
* Entry - Annotating fields with Entry is optional. Maps key will be taken from the field name else from the "key" attribute if provided.
* Ignore - If annotated, will ignore the field while processing.


## Using Generated code
Once annotated build the project to generate code and can be used like below,

```kotlin

//Convert Object to HashMap
 val post = PostQuery.init().setName("Mohan").setAge(26)
 Log.e("Map Data", post.toMap().toString())
 
 //Convert HashMap to Object
 val map = hashMapOf("name" to "Superman", "age" to "30")
 Log.e("Object Data", PostQuery.init().toObject(map).toString())
 
```

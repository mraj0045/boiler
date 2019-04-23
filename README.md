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
    implementation 'com.boiler:boiler:0.0.2'
    kapt 'com.boiler:boiler-compiler:0.0.2'
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
    @Entry(typeConverter = DateConverter::class)
    val dob: Date?
)

class DateConverter : TypeConverter<Date> {

    private val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH)

    override fun format(t: Date?): String {
        return try {
            sdf.format(t)
        } catch (e: Exception) {
            ""
        }
    }

    override fun parse(value: String?): Date? {
        return try {
            sdf.parse(value)
        } catch (e: Exception) {
            null
        }
    }

}
```
Annotation are explained below,
* MapBuilder - Annotate the class to generate a builder class. Generated class will be "class name"+"Builder" format. "Builder" can be changed using the suffix value.
* Entry - Annotating fields with Entry is optional. It accepts two params which is described below
  * HashMap key will be taken from the field name else from the "key" attribute if provided.
  * If custom logic to convert the data(e.g., Convert Date to String & vice-versa) use "typeConverter" property
* Ignore - If annotated, will ignore the field while processing.

Implement the TypeConverter<> class to write custom logic and pass the class to "typeConverter" of Entry annotation.

## Using Generated code
Once annotated build the project to generate code and can be used like below,

```kotlin

//Convert Object to HashMap
 val post = PostQuery.init().setName("Mohan").setAge(26).setDob(Date())
 Log.e("Map Data", post.toMap().toString())
 
 //Convert HashMap to Object
 val map = hashMapOf("name" to "Superman", "age" to "30")
 Log.e("Object Data", PostQuery.init().toObject(map).toString())
 
```

# Whats New
* **0.0.2**
  * Type Converter added 
* 0.0.1 - base version

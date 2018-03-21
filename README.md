* * *

**Not Maintained!**
*This project is no longer being actively maintained. Use at your own risk!*

* * *

# WordPress REST Client for Android

## Build

To build the library, invoke the following `gradle` command in the project root directory:

    $ ./gradlew build

This will create an `aar` package at this location: `WordPressComRest/build/outputs/aar/WordPressComRest.aar`. Feel free to use it directly or put it in a maven repository.

## Usage

If you don't want to compile and host it, the easiest way to use it in your Android project is to add it as a library in your `build.gradle` file:

```groovy
dependencies {
    // use the latest 1.x version
    compile 'com.automattic:rest:1.+'
}
```

## Publish it to bintray

```shell
$ ./gradlew assemble publishToMavenLocal bintrayUpload -PbintrayUser=FIXME -PbintrayKey=FIXME -PdryRun=false
```

## LICENSE

This library is dual licensed under MIT and GPL v2.

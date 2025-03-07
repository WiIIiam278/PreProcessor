<!--suppress ALL-->
<p align="center">
    <h1>PreProcessor</h1>
    <a href="https://repo.william278.net/#/releases/net/william278/preprocessor/">
        <img src="https://repo.william278.net/api/badge/latest/releases/net/william278/preprocessor?color=00fb9a&name=Maven&prefix=v" />
    </a> 
    <a href="https://discord.gg/tVYhJfyDWG">
        <img src="https://img.shields.io/discord/818135932103557162.svg?label=&logo=discord&logoColor=fff&color=7389D8&labelColor=6A7EC2" />
    </a> 
</p>
<br/>

A Gradle plugin for preprocessing code. Based on [ToCraft's PreProcessor](https://github.com/ToCraft/PreProcessor) which is based on [RePlayMod's PreProcessor](https://github.com/ReplayMod/preprocessor).

## Build File

Here's how you can apply the plugin in a `build.gradle` file:

~~~groovy
plugins {
    id 'java'
    id 'net.william278.preprocessor' version '1.0'
}
~~~

The Kotlin implementation for a `build.gradle.kts` file looks similar:

~~~kotlin
plugins {
    id("org.jetbrains.kotlin.jvm") version "2.0.0"
    id("net.william278.preprocessor") version "1.0"
}
~~~

Now, you'll need to define variables so the preprocessor can evaluate the if-statements.
~~~kotlin
preprocess {
    vars.put("a", "1")
}
~~~

This sets the value of the variable `a` to `1`. You can define any value object you want as long as the key is a `String`.

## Gradle Tasks

By default, the plugin registers the following tasks per source set:
* `preProcessJava` & `applyPreProcessJava`, if the source set contains java sources
* `preProcessKotlin` & `applyPreProcessKotlin`, if the source set contains kotlin sources
* `preProcessResources` & `applyPreProcessResources`, if the source set contains resources

It automatically adapts the tasks `compileJava`, `compileKotlin` and `processResources` to use the outputs of the above tasks.
The `applyPreProcess*`-tasks cause the plugin to update the sources and comment lines with `//$$` that won't run since their if-condition is `false`.
This is **not** required for the plugin to preprocess, but for better code readability.
{:.note}

The plugin also registers one task simply called `applyPreProcess`, which automatically applies every `applyPreProcess*` task for every source set in this build file.

## Code Example

An example Java test class looks like this;

~~~java
package test;

class Test {
    public static void main(String... args) {
        //#if a
        //$$ System.out.println("Test succeeded.");
        //#else
        System.out.println("Test failed.");
        //#endif
    }
}
~~~

This will be preprocessed to the following, if `a` exists:

~~~java
package test;

class Test {
    public static void main(String... args) {
        //#if a
        System.out.println("Test succeeded.");
        //#else
        //$$ System.out.println("Test failed.");
        //#endif
    }
}
~~~

## Keywords

You'll notice the `//#if`, `//#$$`, `//#else` and `//#endif`. There is also a `//#elseif` keyword.
These will work as if-statements. Every if-statement **must** start with `//#if` and **must** end with `//#endif`.

You can change these keywords with the following `build.gradle` structure:
~~~groovy
preprocess {
    keywords.put("json", new Keywords("//#if","//#elseif","//#endif","//\$\$"))
}
~~~

This will add custom keywords for every file ending with `json`.

## Possible Conditions

Now, this condition is `true`, if `a` exists and is not `0` or `null`:
~~~
//#if a
~~~

You can also chain conditions:

~~~
//#if a && b || c
~~~

Of course you can also compare integer values with `==`, `!=`, `>=`, `<=`, `>` and `<`.

~~~
//#if a == 1
~~~

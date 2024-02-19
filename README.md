> [!WARNING]
> This README is a template for new Platform Extensions.
> Notes in this format are instructions for you, the author, and must be deleted before the project is complete.

# Example

> [!WARNING]
> This is a high-level summary of the functionality this extension provides.

This extension provides the ability for Brightspot to print the words "Hello World" to the log when certain records are saved.

* [Prerequisites](#prerequisites)
* [Installation](#installation)
* [Usage](#usage)
* [Documentation](#documentation)
* [Versioning](#versioning)
* [Contributing](#contributing)
* [Local Development](#local-development)
* [License](#license)

## Prerequisites

> [!WARNING]
> This section should list any prerequisites that must be met before the extension can be installed or used. 
> If a specific version of Brightspot is needed, it should be listed here.
> If any external APIs are used (AWS, GCP, or any other third party service), they should be listed here.

This extension requires an instance of [Brightspot](https://www.brightspot.com/) and access to the project source code.

## Installation

Gradle:
```groovy
api 'com.brightspot:platform-extension-example:1.0.0'
```

Maven:
```xml
<dependency>
    <groupId>com.brightspot</groupId>
    <artifactId>platform-extension-example</artifactId>
    <version>1.0.0</version>
</dependency>
```

Substitute `1.0.0` for the desired version found on the [releases](/releases) list.

## Usage

> [!WARNING]
> This section describes how a developer would use this extension in their project.
> It should include code samples, if applicable, as well as a link to the end user documentation. 

To opt in to this behavior, implement the `SaysHelloWorld` interface on your content type:

```java
public class MyContentType extends Content implements SaysHelloWorld {
    // ...
}
```

Now, when a `MyContentType` record is saved, the words "Hello World" will be printed to the log.

## Documentation

- [Javadocs](https://artifactory.psdops.com/public/com/brightspot/platform-extension-example/%5BRELEASE%5D/platform-extension-example-%5BRELEASE%5D-javadoc.jar!/index.html)

## Versioning

The version numbers for this extension will strictly follow [Semantic Versioning](https://semver.org/).

## Contributing

If you have feedback, suggestions or comments on this open-source platform extension, please feel free to make them publicly on the issues tab [here](https://github.com/brightspot/content-review-cycle/issues).

Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

## Local Development

Assuming you already have a local Brightspot instance up and running, you can 
test this extension by running the following command from this project's root 
directory to install a `SNAPSHOT` to your local Maven repository:

```shell
./gradlew -Prelease=local publishToMavenLocal
```

Next, ensure your project's `build.gradle` file contains 

```groovy
repositories {
    mavenLocal()
}
```

Then, add the following to your project's `build.gradle` file:

```groovy
dependencies {
    api 'com.brightspot:platform-extension-example:local'
}
```

Finally, compile your project and run your local Brightspot instance.

## License

See: [LICENSE](LICENSE).

# Flat Sitemap

This extension enabled Brightspot to create a "flat" `sitemap.xml` for smaller sites that do not benefit from the "year/month" sitemap partitioning strategy. 

The Flat Sitemap XML is regenerated every 6 hours.

* [Prerequisites](#prerequisites)
* [Installation](#installation)
* [Usage](#usage)
* [Documentation](#documentation)
* [Versioning](#versioning)
* [Contributing](#contributing)
* [Local Development](#local-development)
* [License](#license)

## Prerequisites

This extension requires an instance of [Brightspot](https://www.brightspot.com/) and access to the project source code.

## Installation

Gradle:
```groovy
api 'com.brightspot:flat-sitemap:1.0.0'
```

Maven:
```xml
<dependency>
    <groupId>com.brightspot</groupId>
    <artifactId>flat-sitemap</artifactId>
    <version>1.0.0</version>
</dependency>
```

Substitute `1.0.0` for the desired version found on the [releases](../../releases) list.

## Usage

To enable this behavior, enable the "Flat" sitemap type in Sites &amp; Settings.

If 50,000 or fewer sitemap items are found, the following files structure will be generated:

- `/flat-sitemap.xml` (Index)
    - `/flat-sitemap-content.xml` (All sitemap content)

If greater than 50,000 sitemap items are found, the following files will be generated:

- `/flat-sitemap.xml` (Index)
    - `/flat-sitemap-content.1.xml` (First 50,000 URLs)
    - `/flat-sitemap-content.2.xml` (Next 50,000 URLs, etc.)

> [!NOTE]
> If the "Standard" sitemap type is _not enabled_, the `flat-sitemap-content` files are also available at the following paths:
> - `/sitemap.xml` (alias of `/sitemap.1.xml`)
> - `/sitemap.1.xml` (also available at `/sitemap.xml`)
> - `/sitemap.2.xml`

So, it is recommended to:
- First enable the Flat sitemap type
- Test and validate using the URLs found in `/flat-sitemap.xml`
- Finally disable the Standard sitemap type to enable the `/sitemap.xml` URL rewrites.

## Documentation

- [Javadocs](https://artifactory.psdops.com/public/com/brightspot/flat-sitemap/%5BRELEASE%5D/flat-sitemap-%5BRELEASE%5D-javadoc.jar!/index.html)

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
    api 'com.brightspot:flat-sitemap:local'
}
```

Finally, compile your project and run your local Brightspot instance.

## License

See: [LICENSE](LICENSE).

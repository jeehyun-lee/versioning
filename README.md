# Reliza Versioning

This tool allows for automatic generation and bumping of [CalVer](https://calver.org/) or [SemVer](https://semver.org/) or custom version schemas.

## Installing

### I For command-line usage or integration with CI/CD tools (such as Jenkins)
1. Use docker image, to get the image:
```
docker pull relizaio/versioning
```

2. Compile locally (requires Java 8+ and maven)
from the project directory run
```
mvn clean compile assembly:single
```
This will produce .jar file in the target directory that can be run with "java -jar versioning_jar_file--jar-with-dependencies.jar"

### II To use as a java library
1. Use as maven dependency from maven central:
For Maven:
```
<dependency>
  <groupId>io.reliza</groupId>
  <artifactId>versioning</artifactId>
  <version>2019.05.Stable.3</version>
</dependency>
```

For Gradle:
```
implementation 'io.reliza:versioning:2019.05.Stable.3'
```

See more options on the [Maven Central page](https://search.maven.org/artifact/io.reliza/versioning/)

2. Compile locally (requires Java 8+ and maven)
from the project directory run
```
mvn clean package
```

And include resulting .jar file from the target directory in your project. Then use io.reliza.versioning.VersionApi class for most common operations. More documentation coming soon.

## Running the tests (requires Java 8+ and maven)
From the project directory run
```
mvn clean test
```

## Usage instructions
Reliza Versioning understands following elements of versioning schema (case insensitive):
Major, Minor, Micro or Patch, Year or YYYY, YY, OY, MM, OM, DD, OD

Dot (.), underscore(_) may be used as separators. Dash (-) or plus (+) may be used as separators once each specifically for modifier and metadata. We recommend using dash for modifier and plus for metadata as per SemVer conventions, which then would be treated by the tool as optional elements.

Reliza versioning also understands "SemVer" as a code for "major.minor.patch-identifier+metadata" (where identifier and metadata are treated as optional).

### 1. Specific Command Line usage and samples
Assuming, that we use a compiled jar with dependencies called versioning.jar, call from CLI as
```
java -jar versioning.jar -h
```
which will produce help summary page.

Note that in any usage case other than help page -s (schema) parameter is required.

If using docker image instead, call for the same page:
```
docker run --rm relizaio/versioning -h
```

### 2. Sample Command Line usage
2.1. Generate Reliza flavor CalVer with "Stable" modifier
```
java -jar versioning.jar -s YYYY.0M.Modifier.Micro+Metadata -i Stable
```
or with docker:
```
docker run --rm relizaio/versioning -s YYYY.0M.Modifier.Micro+Metadata -i Stable
```

2.2. Bump patch in existing SemVer version (will produce 2.4.8)
```
java -jar versioning.jar -s semver -v 2.4.7 -a Bump
```
or with docker:
```
docker run --rm relizaio/versioning -s semver -v 2.4.7 -a Bump
```

2.3. Sample call inside Reliza Versioning itself to bump version in the project's pom file, with snapshot option (-t flag) set to true:
```
mvn versions:set -DnewVersion="$(java -jar path_to_versioning\versioning.jar -s yyyy.0m.modifier.patch -i Stable -t True)"
```
or with docker:
```
mvn versions:set -DnewVersion="$(docker run --rm relizaio/versioning -s yyyy.0m.modifier.patch -i Stable -t True)"
```
Note that this example is using versions-maven-plugin (that Reliza Versioning is using too).

Similarly, Reliza Versioning can be included into Jenkins by being called from the bash scripts.

### 3. Usage as Java Library
Use methods exposed in the VersionApi class to create vresions. More documentation is coming soon.

## Versioning

We use Reliza flavor of CalVer for versioning, which has the following schema: YYYY.0M.Modifier.Micro+Metadata

## Authors

This project is created and open-sourced by [Reliza](https://reliza.io)

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details

## Acknowledgments

* This project currently uses Java, Maven, Apache Commons and JUnit.

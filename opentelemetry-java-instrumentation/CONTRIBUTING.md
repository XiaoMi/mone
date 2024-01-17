## Contributing

Pull requests for bug fixes are welcome, but before submitting new features
or changes to current functionality [open an
issue](https://github.com/open-telemetry/opentelemetry-java-instrumentation/issues/new)
and discuss your ideas or propose the changes you wish to make. After a
resolution is reached a PR can be submitted for review.

In order to build and test this whole repository you need JDK 11+.
Some instrumentations and tests may put constraints on which java versions they support.
See [Running the tests](./docs/contributing/running-tests.md) for more details.

### Building

#### Snapshot builds

For developers testing code changes before a release is complete, there are
snapshot builds of the `main` branch. They are available from
the Sonatype OSS snapshots repository at https://oss.sonatype.org/content/repositories/snapshots/ ([browse](https://oss.sonatype.org/content/repositories/snapshots/io/opentelemetry/))

#### Building from source

Build using Java 11:

```bash
java -version
```

```bash
./gradlew assemble
```

and then you can find the java agent artifact at

`javaagent/build/libs/opentelemetry-javaagent-<version>-all.jar`.

### IntelliJ setup

See [IntelliJ setup](docs/contributing/intellij-setup.md)

### Style guide

See [Style guide](docs/contributing/style-guideline.md)

### Running the tests

See [Running the tests](docs/contributing/running-tests.md)

### Writing instrumentation

See [Writing instrumentation](docs/contributing/writing-instrumentation.md)

### Understanding the javaagent components

See [Understanding the javaagent components](docs/contributing/javaagent-jar-components.md)

### Understanding the javaagent instrumentation testing components

See [Understanding the javaagent instrumentation testing components](docs/contributing/javaagent-test-infra.md)

### Debugging

See [Debugging](docs/contributing/debugging.md)

### Understanding Muzzle

See [Understanding Muzzle](docs/contributing/muzzle.md)

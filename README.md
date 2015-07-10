# Meter

shall enable us to obtain fine-grained metrics for our application, without writing the implementation details for the different types.
We aim to resolve this goal by the usage of *meta annotations* and the *evaluation of runtime parameters* in a 'static' way.

This prototype shall demonstrate the current state and your feedback is highly appreciated.

Please do not use this in production unless you know what you do :)

## Build

The project follows a multi-module maven setup and can be build using `mvn clean package` or `mvn clean install` on the root directory
of this project.

## Demonstration

You can find an example app in the corresponding *example* directory.
A jetty plugin has been registered in the maven pom.xml and you can start the application by simply typing
`mvn jetty:run` on the command line within this directory. So please change the prompt prior of executing this command and
wait for the following log-entry *[INFO] Started Jetty Server* to appear.

The server can be found on `http://localhost:8080/bamboo/of?locale=en`, which is a mapping to the `de.synyx.meter.example.restlet.Bamboo` RESTlet.

NOTE

- Please build the whole project/artifacts first
- If you want to use a different server, like tomcat for instance, you have to make sure that a few `javax.` jars are on the classpath as they
have been marked as `provided` in maven pom file. While jetty will include them to the classpath - if configured in the plugin configuration - others may not.
Please verify that your classpath is consistent if you encounter `ClassNotFoundException` exceptions for `javax.` in the log files.

## TODO, Roadmap

- We appreciate any discussion about usage, missing features, roadmap, ..
- λ (6 + Guava) → Java 8
- Extend the Test base
- Concept/Implementation for different REST technologies
  - RESTEasy
  - Spring
  - ..
- Concept/Implementation for different/custom metric provider
  - Netflix/servo
  - ..

## License

[synyx/meter](http://github.com/synyx/meter) is licensed under the [Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0)

All logos, brands and trademarks are **not** under the Apache License 2.0 and may not be used without permission from [synyx](http://www.synyx.de/).

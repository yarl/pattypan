![Logo](http://i.imgur.com/Wjti8vi.png)

Tool that simplifies [Wikimedia Commons](https://commons.wikimedia.org/) batch file uploading for [GLAM institution](https://outreach.wikimedia.org/wiki/GLAM) volunteers and employees. Created thanks to Wikimedia Foundation [IEG Grant](https://meta.wikimedia.org/wiki/Grants:IEG/Batch_uploader_for_small_GLAM_projects).

For more information on usage, see [Commons:Pattypan](https://commons.wikimedia.org/wiki/Commons:Pattypan).

__[:arrow_down: Download](https://github.com/yarl/pattypan/releases)__

----

### Build and run
[Apache Maven](https://maven.apache.org/) is used for building Pattypan. You need to have JDK 11 or later installed as well as Maven.

In order to download the source code, build, and run the application, do the following:

```bash
# Clone the repository
git clone https://github.com/yarl/pattypan.git
cd pattypan

# Build the project (compiles code and creates JAR in target/)
mvn clean package

# Run the application directly using Maven (recommended for development)
mvn javafx:run

# Alternatively, run the created JAR file (requires Java 11+)
# Note: Ensure your JAVA_HOME points to a JDK with JavaFX modules included, or handle JavaFX modules separately.
# java -jar target/pattypan-1.0.0.jar
```

Maven automatically downloads all required dependencies, including OpenJFX. No manual downloads are needed.

You can also run the application with specific Wikimedia sites:

```bash
# Using mvn javafx:run (pass arguments after --)
mvn javafx:run -- -wiki=test.wikipedia.org
mvn javafx:run -- -wiki=test2.wikipedia.org -protocol=https:// -scriptPath=/w

# Using the JAR file
# java -jar target/pattypan-1.0.0.jar wiki="test.wikipedia.org"
# java -jar target/pattypan-1.0.0.jar wiki="test2.wikipedia.org" protocol="https://" scriptPath="/w"
```

Please note, that on test server file upload may be disabled for regular users. Admin account is suggested, you can request rights [here](https://test.wikipedia.org/wiki/Wikipedia:Requests/Permissions). If you have problems with program running, check [article on project wiki](https://github.com/yarl/pattypan/wiki/Run).

### License
Copyright (c) 2016 Paweł Marynowski.

Source code is available under the [MIT License](https://github.com/yarl/pattypan/blob/master/LICENSE). See the LICENSE file for more info. Program dependencies are managed via Maven and listed in the `pom.xml` file.

#### Credits
Name by Wojciech Pędzich. Logo by [Rickterto](//commons.wikimedia.org/wiki/User:Rickterto), licensed under the Creative Commons [BY-SA 4.0](https://creativecommons.org/licenses/by-sa/4.0/deed.en) license.



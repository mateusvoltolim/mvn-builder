# mvn-builder

Project created to help build multi projects in maven and clean garbage in local repositories.

## Configurations

The configurations are made via json file. See an example:

```json
{
  "dirBaseCheckout": "/home/mateus/development/personal/repositories/",
  "mavenHome": "/usr",
  "goals": [
    "clean",
    "install",
    "-U",
    "-Dfile.encoding=UTF-8"
  ],
  "localRepository": {
    "enable": true,
    "maxAgeFiles": 30,
    "path": "/home/mateus/.m2/repository/tools"
  },
  "modules": [
    "j4dev"
  ],
  "throwFailure": true
}
```

### Parameters

* **dirBaseCheckout:** It is the path where is located your repositories.
* **mavenHome:** In case when Environment Variable *MAVEN_HOME* or *M2_HOME* isn't defined, it is possible define the path where is the binaries of maven.
* **goals:** It is an array, and should be defined in ordered of execution. Specifies the parameters for the build.
* **localRepository:** This object has 3 fields.
  * **enable:** It is a boolean to define if it should remove old dependencies of your projects.
  * **maxAgeFiles:** Defines the max age of the files to be considered in the filter to be removed.
  * **path** The path where is located the dependencies of your own projects (and not of the third party dependencies).
* **modules:** It is an array, and should be in ordered of execution. Defines the maven projects names located in the path defined in *dirBaseCheckout*.
* **throwFailure:** It is a boolean to define if it should be thrown an exception and stop the execution.


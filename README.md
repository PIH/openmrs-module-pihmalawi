This represents the overall distribution and customization module for PIH Malawi

## Build PIH Malawi development environment with OpenMRS SDK

**NOTE**
The following are variables which can be modified:
```
serverId = malawi
database = openmrs_neno
source code = ~/workspace
module name = pihmalawi for openmrs-module-pihmalawi
```
### Linux instructions 

#### Install prerequisites

1. [Install Java and Maven](https://wiki.openmrs.org/display/docs/OpenMRS+SDK#OpenMRSSDK-Installation)
2. Install git
   - sudo apt-get install git
3. To setup the OpenMRS SDK, you just need to open up a terminal/console and enter:
   - mvn org.openmrs.maven.plugins:openmrs-sdk-maven-plugin:setup-sdk

#### Source the PIH Malawi database

Download a *de-identified* backup and source the database:
```
mysql -u root -p mysql
(enter password)

CREATE DATABASE openmrs_neno
DEFAULT CHARACTER SET utf8
DEFAULT COLLATE utf8_general_ci;

use openmrs_neno;

source *name of openmrs neno database*.sql
```

#### Install OpenMRS SDK
1. Setup source code directory
   - cd ~
   - mkdir workspace
   - cd workspace
2. Get PIH Malawi module from git
   - git clone https://github.com/PIH/openmrs-module-pihmalawi.git pihmalawi
3. Install the PIH Malawi omod in your local maven repo
    - from the directory you have PIH Malawi checked out:
    - mvn clean install -DskipTests
4. Setup SDK
   - mvn openmrs-sdk:setup -DserverId=malawi 
   - Type '1' to setup a distribution
   - Set custom distribution = 'org.openmrs.module:pihmalawi:6.7.0-SNAPSHOT' 
   - Set -DdbUri=jdbc:mysql://localhost:3306/openmrs_neno
5. Run SDK
   - mvn openmrs-sdk:run -DserverId=malawi 
6. If everything starts up, but when you try to go to the login page (or any other page) you get a big stack trace that looks like it is due to Groovy, you need to remove the groovy jar file from the lib file.  Use the desktop file UI to open the openmrs-1.9.x.war file and remove the groovy jar from the logic omod.
   - Using the system file UI, double click the openmrs war file (ie. ~/openmrs/malawi/openmrs-1.9.11.war)
   - navigate to the WEB-INF/bundledModules folder
   - find the logic omod and double click on the logic omod
   - find the lib/groovy jar file and delete the groovy jar file
   - delete the ~/openmrs/malawi/tmp directory before running 'mvn openmrs-sdk:run'
7. Run SDK
   - mvn openmrs-sdk:run -DserverId=malawi

## Using your local development environment

With the Chrome browser, use this URL:

> http://localhost:8080/openmrs

## Modify development environment

If you want to modify and test the PIH Malawi module, you can "watch" the the module for the "malawi" project.  You can add a module as watched by the selected server executing the openmrs-sdk:watch command in module's project directory. 
```
cd ~/workspace/pihmalawi
mvn openmrs-sdk:watch -DserverId=malawi
mvn openmrs-sdk:run -DserverId=malawi
```

### Additional references

- [OpenMRS SDK](https://wiki.openmrs.org/display/docs/OpenMRS+SDK)









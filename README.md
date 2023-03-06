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

##### Configure mySQL Server to work with OpenMRS
1. Edit MySQL server config file
   - On Mac OS, the config file is located at **_/etc/my.cnf_**:
    ```
   default-time-zone=-4:00
    ```
   - On Ubuntu
    ```
    default-time-zone=”America/New_York"
    ```
    - I think in Malawi the GMT offset is +2:00? 
2. Restart mysql server
3. Log on mysql server and check the time zone:
   ```
   mysql -u root -p
   SELECT @@global.time_zone;   
   ```
4. It should display something like:
   ```
   +--------------------+
   | @@global.time_zone |
   +--------------------+
   | -04:00             |
   +--------------------+
   ```    

#### Source the PIH Malawi database

Download a *de-identified* backup and source the database:
```
mysql -u root -p mysql
(enter password)

CREATE DATABASE openmrs_neno DEFAULT CHARACTER SET utf8 DEFAULT COLLATE utf8_general_ci;
GRANT ALL ON `openmrs_neno`.* TO 'openmrs'@'localhost' IDENTIFIED BY 'openmrs';

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
    - from the directory you have PIH Malawi checked out build the pihmalawi distribution:
    ```
    mvn clean install -DskipTests -Pdistribution
    ```
4. Setup SDK
   - mvn openmrs-sdk:setup -DserverId=malawi 
   - Type '1' to setup a distribution
   - Set custom distribution = 'org.openmrs.distro:pihmalawi:X-Y-Z-SNAPSHOT' (see pom.xml for current version) 
   - Set -DdbUri=jdbc:mysql://localhost:3306/openmrs_neno
   - or, just run the following command
   ```
   mvn openmrs-sdk:setup -DserverId=malawi -Ddistro=org.openmrs.distro:pihmalawi:6.9.0-SNAPSHOT -DdbUri=jdbc:mysql://localhost:3306/openmrs_neno -DdbUser=openmrs -DdbPassword=openmrs
   ```

5. Install configuration
   - cd configuration
   - ./install.sh <serverId> (eg. ./install.sh malawi)

6. Run SDK
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
## Release to maven a new version of pihmalawi module
1. Navigate to CI [Bamboo pihmalawi project](http://bamboo.pih-emr.org:8085/browse/MLW-PML)

2. Select Actions --> Configuration
3. Click Run
4. Once the Default Stage Stage completes
5. Click on the Run button/icon next to the **Release to Maven** stage
6. A pop-up screen will be displayed where you have to override the Maven variables:
   - maven.release.version = THE_NEW_RELEASE_VERSION (e.g. 6.8.0)
   - maven.development.version = THE_NEXT_SNAPSHOT_VERSION (e.g. 6.9.0-SNAPSHOT)
![Release to maven](https://github.com/PIH/openmrs-module-pihmalawi/blob/master/scripts/misc/mvn_variables.png)
7. Click Run

### Additional references

- [OpenMRS SDK](https://wiki.openmrs.org/display/docs/OpenMRS+SDK)

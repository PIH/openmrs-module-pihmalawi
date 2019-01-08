#!/bin/bash

OPENMRS_WAR_URL="https://openmrs.jfrog.io/openmrs/releases/org/openmrs/web/openmrs-webapp/1.9.11/openmrs-webapp-1.9.11.war"
IC3_PWA_URL="http://bamboo.pih-emr.org/pwa-repo/unstable/openmrs-pwa-apzu-ic3.tar.gz"
MODULES_ZIP_URL="http://bamboo.pih-emr.org/malawi-repo/modules.zip"
INITIAL_SQL_ZIP="http://bamboo.pih-emr.org/artifacts/malawi-initial-db.zip"

TARGET_DIR="./malawi-artifacts"
mkdir $TARGET_DIR

echo "Downloading Web Apps..."
WEBAPPS_DIR="$TARGET_DIR/webapps"
mkdir $WEBAPPS_DIR
wget -q --show-progress -O $WEBAPPS_DIR/openmrs.war $OPENMRS_WAR_URL
wget -q --show-progress -O $WEBAPPS_DIR/workflow.tar.gz $IC3_PWA_URL
echo "Web Apps downloaded to $WEBAPPS_DIR"

echo "Downloading modules..."
MODULES_DIR="$TARGET_DIR/modules"
mkdir $MODULES_DIR
wget -q --show-progress -O $MODULES_DIR/modules.zip $MODULES_ZIP_URL
unzip -j $MODULES_DIR/modules.zip -d $MODULES_DIR
echo "Modules downloaded to $MODULES_DIR"

echo "Downloading initial SQL..."
wget -q --show-progress -O $TARGET_DIR/malawi-initial-db.zip $INITIAL_SQL_ZIP
unzip -j malawi-initial-db.zip -d $TARGET_DIR
echo "Initial SQL downloaded to $TARGET_DIR"

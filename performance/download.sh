#!/bin/bash

# Download jmeter tar.gz
JMETER_VERSION=5.3
JMETER_FILE="apache-jmeter-$JMETER_VERSION.zip"
JMETER_URL="http://mirrors.up.pt/pub/apache/jmeter/binaries/$JMETER_FILE"
JMETER_PATH=jmeter

downloadFile() {
    if [ -d "$JMETER_PATH" ]; then
        echo "$JMETER_PATH folder exist, delete..."
        rm -rf jmeter
    fi
    if [ -f "$JMETER_FILE" ]; then
        echo "$JMETER_FILE exist, exiting..."
    else 
        echo "$JMETER_FILE does not exist, lets download!"
        echo "Downloading JMeter URL: $JMETER_URL"
        curl $JMETER_URL --output $JMETER_FILE
        echo "Download JMeter Complete!"
    fi
}

extract() {
    # Extract jmeter tar.gz
    echo "Extracting JMeter : $JMETER_FILE"
    unzip $JMETER_FILE
    mv "apache-jmeter-$JMETER_VERSION" "$JMETER_PATH"
    echo "Extract JMeter : $JMETER_FILE Complete!"

    rm $JMETER_FILE
}

plugins() {
    echo "Extracting JMeter plugins" 
    curl -L "https://jmeter-plugins.org/get/" --output "$JMETER_PATH/lib/ext/jmeter-plugins-manager.jar"
    curl "http://search.maven.org/remotecontent?filepath=kg/apc/cmdrunner/2.2/cmdrunner-2.2.jar"  --output "$JMETER_PATH/lib/cmdrunner-2.2.jar"
    java -cp $JMETER_PATH/lib/ext/jmeter-plugins-manager.jar  org.jmeterplugins.repository.PluginManagerCMDInstaller
    ./$JMETER_PATH/bin/PluginsManagerCMD.sh install-for-jmx performance.jmx
}

downloadFile
extract
plugins

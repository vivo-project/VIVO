#!/bin/sh

if  [ -z "$1" ]
  then
    echo 'New version number required eg. 1.11.0-RC-1'
    exit 1
fi

mvn versions:set -DgenerateBackupPoms=false -DnewVersion=$1
cd installer
mvn versions:set -DgenerateBackupPoms=false -DnewVersion=$1
cd ..

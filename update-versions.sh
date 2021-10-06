#!/bin/sh

if  [ -z "$1" ]
  then
    echo 'New version number required eg. 1.12.0-RC1'
    exit 1
fi

mvn versions:set -DgenerateBackupPoms=false -DnewVersion=$1
cd installer
mvn versions:set -DgenerateBackupPoms=false -DnewVersion=$1
cd ..

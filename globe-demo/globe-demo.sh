#!/bin/bash

clear

# change directory to where the script is
ABSPATH=$(cd "${0%/*}" && echo $PWD/${0##*/})
PATH_ONLY=`dirname "$ABSPATH"`
cd "${PATH_ONLY}"

echo Globe Demo
echo ----------
echo

export DEVELOPMENT=on
export LOW_END=off
#export ARCHITECTURE="32"

#export EXTRA_CLASSPATH="../globe/libs/JMF-2.1.1e/lib/jmf.jar"

../globe/runjava.sh es.igosoftware.globe.demo.GGlobeDemo $*

#!/bin/bash


function show_message() {
    if [ "$DEVELOPMENT" = "on" ] ; then
        echo $*
    fi
}


if [ "$DEVELOPMENT" = "on" ] ; then
    DEVELOPMENT=on
else
    DEVELOPMENT=off
fi


if [ "$EXTRA_JAVAVM_ARG" = "" ] ; then
    EXTRA_JAVAVM_ARG=""
fi


if [ "$LOW_END" = "on" ] ; then
    LOW_END=on
else
    LOW_END=off
fi



DETECTED_ARCHITECTURE=`uname -m`
if [ "$DETECTED_ARCHITECTURE" = "x86_64" ] || [ "$DETECTED_ARCHITECTURE" = "ia64" ]; then
    DETECTED_ARCHITECTURE="64"
else
    DETECTED_ARCHITECTURE="32"
fi


if [ "$DETECTED_ARCHITECTURE" = "32" ] ; then
    show_message = Detected Architecture: 32 bits =
else
    show_message = Detected Architecture: 64 bits =
fi
show_message


if [ "$ARCHITECTURE" = "" ] ; then
    echo = Automatic Architecture Detection =
    ARCHITECTURE="${DETECTED_ARCHITECTURE}"
fi


OS=`uname`
if [ "$OS" = "Darwin" ]; then
    echo == Mac OS X ==

    LIBS=../3D/libs
    export CLASSPATH=../3d:.
    export CLASSPATH=${CLASSPATH}:${LIBS}/args4j-2.0.9/args4j-2.0.9.jar
    export CLASSPATH=${CLASSPATH}:${LIBS}/lookandfeel/substance.jar
    export CLASSPATH=${CLASSPATH}:${LIBS}/lookandfeel/substance-extras.jar
    export CLASSPATH=${CLASSPATH}:${LIBS}/jmf/customizer.jar
    export CLASSPATH=${CLASSPATH}:${LIBS}/jmf/jmf.jar
    export CLASSPATH=${CLASSPATH}:${LIBS}/jmf/mediaplayer.jar
    export CLASSPATH=${CLASSPATH}:${LIBS}/jmf/multiplayer.jar
    export CLASSPATH=${CLASSPATH}:${LIBS}/joystick/Joystick.jar
    export CLASSPATH=${CLASSPATH}:${LIBS}/jython2.2.1/jython.jar
    export CLASSPATH=${CLASSPATH}:${LIBS}/jung2-2_0/jung-api-2.0.jar
    export CLASSPATH=${CLASSPATH}:${LIBS}/jung2-2_0/jung-graph-impl-2.0.jar
    export CLASSPATH=${CLASSPATH}:${LIBS}/jung2-2_0/jung-algorithms-2.0.jar
    export CLASSPATH=${CLASSPATH}:${LIBS}/JNA/jna.jar
    export CLASSPATH=${CLASSPATH}:${LIBS}/jCharts-0.7.5/jCharts-0.7.5.jar
    export CLASSPATH=${CLASSPATH}:${LIBS}/bsh/bsh-2.0b4.jar


    /System/Library/Frameworks/JavaVM.framework/Versions/1.6.0/Home/bin/java \
        ${EXTRA_JAVAVM_ARG} \
        -client \
        -Xms2000M \
        -Xmx2000M \
        $*

    exit
fi


# Test for local libs
if test -x libs ; then
    LIBS=./libs
else
    #LIBS=../tools/libs/thirdparty
    LIBS=../3d/libs
fi

IGOLIBS=../igolibs/libs

# Don't use MToolkit
export AWT_TOOLKIT=XToolkit

export CLASSPATH=${IGOLIBS}:../igolibs/:../3d/:../euclid/:.
export CLASSPATH=${CLASSPATH}:${LIBS}/args4j-2.0.9/args4j-2.0.9.jar
export CLASSPATH=${CLASSPATH}:${LIBS}/java3d_152/j3dcore.jar
export CLASSPATH=${CLASSPATH}:${LIBS}/java3d_152/j3dutils.jar
export CLASSPATH=${CLASSPATH}:${LIBS}/java3d_152/vecmath.jar
export CLASSPATH=${CLASSPATH}:${IGOLIBS}/lookandfeel/substance.jar
export CLASSPATH=${CLASSPATH}:${IGOLIBS}/lookandfeel/substance-extras.jar
export CLASSPATH=${CLASSPATH}:${LIBS}/jmf/customizer.jar
export CLASSPATH=${CLASSPATH}:${LIBS}/jmf/jmf.jar
export CLASSPATH=${CLASSPATH}:${LIBS}/jmf/mediaplayer.jar
export CLASSPATH=${CLASSPATH}:${LIBS}/jmf/multiplayer.jar
export CLASSPATH=${CLASSPATH}:${LIBS}/joystick/Joystick.jar
export CLASSPATH=${CLASSPATH}:${LIBS}/jython2.2.1/jython.jar
export CLASSPATH=${CLASSPATH}:${LIBS}/jung2-2_0/jung-api-2.0.jar
export CLASSPATH=${CLASSPATH}:${LIBS}/jung2-2_0/jung-graph-impl-2.0.jar
export CLASSPATH=${CLASSPATH}:${LIBS}/jung2-2_0/jung-algorithms-2.0.jar
export CLASSPATH=${CLASSPATH}:${LIBS}/JNA/jna.jar
export CLASSPATH=${CLASSPATH}:${LIBS}/jCharts-0.7.5/jCharts-0.7.5.jar
export CLASSPATH=${CLASSPATH}:${IGOLIBS}/bsh/bsh-2.0b4.jar

if [ "$EXTRA_CLASSPATH" != "" ] ; then
    export CLASSPATH=${CLASSPATH}:${EXTRA_CLASSPATH}
fi

if [ "$EXTRA_JAVA_LIBRARY_PATH" = "" ] ; then
    EXTRA_JAVA_LIBRARY_PATH=""
fi

#echo CLASSPATH=${CLASSPATH}

if [ "$JAVAVM" = "" ] ; then 
    if test -x jre ; then
        if [ "$ARCHITECTURE" = "32" ] ; then
            JAVAVM=./jre/linux/32/bin/java
        else
            JAVAVM=./jre/linux/64/bin/java
        fi
    else
        if test -x ../tools/jre ; then
            if [ "$ARCHITECTURE" = "32" ] ; then
                JAVAVM=../tools/jre/linux/32/bin/java
            else
                JAVAVM=../tools/jre/linux/64/bin/java
            fi
        else
            JAVAVM=java
        fi
    fi
fi

#if [ "$DEVELOPMENT" = "on" ] ; then
#    show_message ----------------------------------------------------------------------
#    show_message Using Java:
#    show_message 
#    ${JAVAVM} -client -version
#    show_message ----------------------------------------------------------------------
#    show_message 
#fi

if test -x /usr/bin/padsp ; then
    show_message = Redirecting OSS through PulseAudio =
    JAVA="exec /usr/bin/padsp -n 3D ${JAVAVM}"
else
    JAVA="exec ${JAVAVM}"
fi

show_message = Running with "${JAVA}" =

if [ "$LOW_END" = "on" ] ; then 
    LOWEND_JAVAVM_ARG="-Dlowend=on"

    if [ "$ARCHITECTURE" = "32" ] ; then
        MEMORY_JAVAVM_ARG="-Xms1000M -Xmx1000M"
    else
        MEMORY_JAVAVM_ARG="-Xms2048M -Xmx2048M"
    fi
else
    LOWEND_JAVAVM_ARG="-Dlowend=off"

    if [ "$ARCHITECTURE" = "32" ] ; then
        if [ "$DETECTED_ARCHITECTURE" = "64" ] ; then
            MEMORY_JAVAVM_ARG="-Xms2048M -Xmx2048M"
            #MEMORY_JAVAVM_ARG="-Xms1845M -Xmx1845M"
        else
            MEMORY_JAVAVM_ARG="-Xms1768M -Xmx1768M"
        fi
        #MEMORY_JAVAVM_ARG="-Xms2048M -Xmx2048M"
        #MEMORY_JAVAVM_ARG="-Xms750M -Xmx750M"
    else
        MEMORY_JAVAVM_ARG="-Xms6G -Xmx6G"
        #MEMORY_JAVAVM_ARG="-Xmx6656M"
        #MEMORY_JAVAVM_ARG="-Xms1768M -Xmx1768M"
    fi
fi

if [ "$DEVELOPMENT" = "on" ] ; then
    DEVELOPMENT_JAVAVM_ARG="-Ddevelopment=on"
else
    DEVELOPMENT_JAVAVM_ARG="-Ddevelopment=off"
fi

# show_message MEMORY_JAVAVM_ARG=${MEMORY_JAVAVM_ARG}
# show_message LOWEND_JAVAVM_ARG=${LOWEND_JAVAVM_ARG}
# show_message DEVELOPMENT_JAVAVM_ARG=${DEVELOPMENT_JAVAVM_ARG}

if [ "$ARCHITECTURE" = "32" ] ; then
    show_message = Running in 32 bits =
    show_message

    ${JAVA} \
        -client \
        -Xshare:off \
        ${MEMORY_JAVAVM_ARG} \
        ${EXTRA_JAVAVM_ARG} \
        ${DEVELOPMENT_JAVAVM_ARG} \
        ${LOWEND_JAVAVM_ARG} \
        -Dpython.cachedir=/tmp \
        -Djava.library.path=${LIBS}/java3d_152:${LIBS}/joystick:${EXTRA_JAVA_LIBRARY_PATH} \
        $*
else
    show_message = Running in 64 bits =
    show_message

    ${JAVA} \
        -Xshare:off \
        ${MEMORY_JAVAVM_ARG} \
        ${EXTRA_JAVAVM_ARG} \
        ${DEVELOPMENT_JAVAVM_ARG} \
        ${LOWEND_JAVAVM_ARG} \
        -Dpython.cachedir=/tmp \
        -Djava.library.path=${LIBS}/java3d_152-64b:${LIBS}/joystick:${EXTRA_JAVA_LIBRARY_PATH} \
        $*
fi

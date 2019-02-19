#!/bin/bash


dir=("floatwindow" "sample" )

# clean  cache
function clean()
{
    for element in ${dir[@]}
    do
        # clean sub dir
        rm -rf $element/build/
        rm -rf $element/bin/
        rm -rf $element/gen/
        rm -rf $element/.externalNativeBuild
    done
    # clean root dir
    rm -rf build/
    rm -rf release/
}


# gradlew build
function build_gradlew()
{
    ./gradlew release
}

# gradle build
function build_gradle()
{
    gradle release
}


: '
    编译程序的入口
'

# 1. 清除缓存
clean

# 2. 编译并处理异常情况
build_gradlew

if  [ $# == 0 ]; then
    echo "gradlew build success"
    pwd=$(pwd)
    echo "gradlew build success. path: $(pwd)/release/"
else
    echo "gradlew build failed"
    build_gradle
    if  [ $# == 0 ]; then
        echo "gradle build success"
        pwd=$(pwd)
        echo "gradle build success. path: $(pwd)/release/"
    fi
fi
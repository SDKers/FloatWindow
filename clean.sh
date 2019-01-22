#!/bin/bash

: '
 清除程序入口
'
echo ">>>>clean project<<<<"
dir=("floatwindow" "sample" )
for element in ${dir[@]}
do
    #clean task
    rm -rf $element/build/
    rm -rf $element/bin/
    rm -rf $element/gen/
    rm -rf $element/.externalNativeBuild
done

rm -rf build/
rm -rf release/


if  [ $# == 0 ]; then
    echo " clean project success. "
else
    echo ">>clean project Failed!<<"
fi

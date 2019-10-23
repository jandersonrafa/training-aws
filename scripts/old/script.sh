#!/bin/bash
for filename in ./origin/*.sh
do 
	sh $filename
	echo $filename
done

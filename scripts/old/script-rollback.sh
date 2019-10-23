#!/bin/bash
for filename in ./rollback/*-rollback.sh
do 
	sh $filename
	echo $filename
done

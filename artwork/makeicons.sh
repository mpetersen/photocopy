#!/usr/bin/env bash
SCALE=36
BORDER=4
TARGET="../src/main/resources/icons"

mkdir -p $TARGET
convert icon.png -scale $SCALEx$SCALE -bordercolor none -border $BORDERx$BORDER $TARGET/menubar.png
convert icon.png -scale $SCALEx$SCALE -bordercolor none -border $BORDERx$BORDER -negate $TARGET/menubar_dark.png
makeicns -in icon.png

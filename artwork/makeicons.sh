#!/usr/bin/env bash
SCALE=36
BORDER=4
TARGET="../src/main/resources/icons"

mkdir -p $TARGET
convert menubar_icon.png -scale $SCALEx$SCALE -bordercolor none -border $BORDERx$BORDER $TARGET/menubar.png
convert menubar_icon.png -scale $SCALEx$SCALE -bordercolor none -border $BORDERx$BORDER -channel RGB -negate $TARGET/menubar_dark.png
makeicns -in icon.png

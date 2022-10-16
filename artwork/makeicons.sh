#!/usr/bin/env bash
SCALE=38
BORDER=2
TARGET="../src/main/resources/icons"

mkdir -p $TARGET
convert icon2.png -scale $SCALEx$SCALE -bordercolor none -border $BORDERx$BORDER $TARGET/menubar.png
convert icon2.png -scale $SCALEx$SCALE -bordercolor none -border $BORDERx$BORDER -channel RGB -negate $TARGET/menubar_dark.png
makeicns -in icon.png

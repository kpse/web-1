#!/bin/sh
set -e
# check to see if protobuf folder is empty
if [ ! -d "$HOME/play-${PLAY_VERSION}/play" ]; then
  wget http://downloads.typesafe.com/play/${PLAY_VERSION}/play-${PLAY_VERSION}.zip
  unzip -q play-${PLAY_VERSION}.zip
else
  echo 'Using cached directory.';
fi
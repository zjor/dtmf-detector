# DTFM Detector

## Overview

The device captures input from the microphone and decodes digit encoded using DTFM.

## Schematic

![schematic](https://raw.githubusercontent.com/zjor/dtmf-detector/master/schematic_bb.png)

## Connections

### Display to Arduino

|Display|Arduino|
|-------|-------|
|VCC    |5V     |
|GND    |GND    |
|DIN    |D11    |
|CLK    |D13    |
|CS     |D9     |

### Mic to Arduino

|Mic    |Arduino|
|-------|-------|
|VCC    |3.3V   |
|GND    |GND    |
|Out    |A0     |

Note: Connect 3.3V to AREF

## Video

[![video](https://img.youtube.com/vi/vfhiGLL-tyk/0.jpg)](https://www.youtube.com/watch?v=vfhiGLL-tyk)

## How to build

Run `gradlew fatJar` from processing folder

## Capturing serial output to file
1. `script -a -t0 serial.txt`
2. `screen -L /dev/tty.usbXXXXX <bauds>`
3. Stop screen: Ctrl+A + Ctrl+K
4. Stop script: Ctrl+D

## References

- [DTFM tone generator](http://onlinetonegenerator.com/dtmf.html)
- http://www.hex705.com/logging-serial-communication-to-a-text-file-arduino-and-osx/
- [Goertzel algorithm description](http://ru.dsplib.org/content/goertzel/goertzel.html)
- https://xantorohara.github.io/led-matrix-editor

- [habr](https://habr.com/post/432498/)
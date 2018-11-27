# DTMF Detector

## How to build

1. Run `processing/install-deps.sh` to install dependencies from `libs` to local maven repo (otherwise it's impossible to build executable jar)
2. Run `mvn clean install`

## Capturing serial output to file
1. `script -a -t0 serial.txt`
2. `screen -L /dev/tty.usbXXXXX <bauds>`
3. Stop screen: Ctrl+A + Ctrl+K
4. Stop script: Ctrl+D

## References

- http://onlinetonegenerator.com/dtmf.html
- http://www.hex705.com/logging-serial-communication-to-a-text-file-arduino-and-osx/
- http://ru.dsplib.org/content/goertzel/goertzel.html
- https://xantorohara.github.io/led-matrix-editor
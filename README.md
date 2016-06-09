# osgi-train

OSGi implementation for a modular architecture, using various sensors and the RPi.

The sensor modules are based on:
- PWM: [Hirt.se](http://hirt.se/blog/?p=625)
- TCS34527: [OlivierLD](https://github.com/OlivierLD/raspberry-pi4j-samples/blob/master/I2C.SPI/src/i2c/sensor/TCS34725.java)
- PN532: [hsilomedus](https://github.com/hsilomedus/raspi-pn532)

The Pi4J bundle is generated by using the executables from [the Pi4J project](http://pi4j.com), adding a manifest before compressing to JAR.

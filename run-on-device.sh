#!/bin/bash
./gradlew installDebug && adb shell am start pl.edu.pw.elka.hexancientempires/.ConnectActivity

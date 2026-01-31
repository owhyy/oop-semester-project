# Song Editor

Audio editing app with a Java Swing UI and C++ backend, bridged via JNA.

## Prerequisites

- Java 17+
- Maven
- CMake 3.14+
- C++ compiler (g++, clang++)

## Build

### 1. Build the C++ library

```bash
cd cpp
mkdir -p build && cd build
cmake ..
make
```

This produces `libaudiolib.so` in `cpp/build/`.

### 2. Build the Java app

```bash
mvn compile
```

## Run

Point Java to the native library location:

```bash
java -Djava.library.path=./cpp/build -jar target/song-editor-1.0-SNAPSHOT.jar
```

Or with Maven:

```bash
mvn exec:java -Dexec.mainClass="app.App" -Dexec.args="" -Djava.library.path=./cpp/build
```

Or compile and run manually:

```bash
mvn compile
java -Djava.library.path=./cpp/build -cp "target/classes:$HOME/.m2/repository/net/java/dev/jna/jna/5.14.0/jna-5.14.0.jar" app.App
```

## Project structure

```
├── pom.xml              # Maven (Java + JNA)
├── src/main/java/app/
│   ├── App.java         # Swing entry point
│   └── AudioLib.java    # JNA interface → C++
├── cpp/
│   ├── CMakeLists.txt
│   ├── include/audio_lib.h
│   └── src/audio_lib.cpp
└── README.md
```

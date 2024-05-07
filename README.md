<p align="center"> 
    <img src="resources\logo_s.png" width="200" title="logo">
</p>

<h1 align="center">HelixOS</h1>

Helix is a small x86 OS featuring virtual memory, garbage collection, cooperative scheduling and a GUI library.
This project was created as part of my operating systems course and is written in Java (yes Java(-subset)) by using the [Small Java Compiler](https://fam-frenz.de/stefan/compiler.html).

## Build

### Prerequisites

- Java (`java` in path)
- [SJC](https://fam-frenz.de/stefan/compiler.html)
- QEMU (`qemu-system-x86_64` in path)

### Build Script

```
python3 build.py --sjc path_to_sjc_jar
```

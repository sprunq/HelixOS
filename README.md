# ToOS

## Build
### Prerequisites
- Java (`java` in path)
- [SJC](https://fam-frenz.de/stefan/compiler.html)
- QEmu (`qemu-system-i386` in path)

### Build Script
To make the build process easier and speed it up I created a python build script, 
which executes the compiler and calls QEmu if compilation was successful. 
It copies all needed files to a temporary subdirectory to keep the project directory clean.

The general build command looks like:
```
python3 build.py --sjc path_to_sjc_jar
```

Example:
```
python3 build.py --sjc "C:\Users\fires\Documents\SJC\sjc.jar"
```

More sub-commands can be displayed with `--help`, which will be useless for most users.

### Commands
The build script is basically a wrapper around the these two commands.

Build:
```
java -jar path_to_sjc_jar src -o boot   
```

Run QEmu:
```
qemu-system-i386 -m 32 -boot a -drive file=BOOT_FLP.IMG,format=raw,if=floppy
```
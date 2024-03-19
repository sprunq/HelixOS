import os
import shutil
import subprocess
import signal
import platform
import sys

BUILD_DIR = "os_build"
RESOURCE_DIR = "res"

def clean_dir(dir : str):
    if os.path.exists(dir):
        shutil.rmtree(dir)
    os.makedirs(dir)

def get_qemu() -> str:
   if platform.system() == 'Windows':
        return "qemu-system-i386w"
   else:
        return "qemu"

def build():
    clean_dir(BUILD_DIR)
    shutil.copy(f"{RESOURCE_DIR}/bootconf.txt", BUILD_DIR)
    shutil.copy(f"{RESOURCE_DIR}/bts_dsk.bin", BUILD_DIR)
    os.chdir(BUILD_DIR)
    
    output = subprocess.run(["java", "-jar", "..\\res\\sjc.jar", "..\\src", "-o", "boot"], 
                       capture_output=True, 
                       text=True) 
    
    print(output.stdout)

    if not os.path.exists("BOOT_FLP.IMG"):
        print("Detected errors during compilation. Aborting build..")
        return
    
    subprocess.run([get_qemu(), "-m", "32", "-boot", "a", "-fda", "BOOT_FLP.IMG"])

    if len(sys.argv) > 1 and sys.argv[1] == "True":
        # Close the terminal.
        # I need to do this since I run the script via the IntelliJ run button, which launches a new Console every run and
        # after too many runs it becomes very slow.
        if platform.system() != 'Linux':
            signal.SIGHUP = 1

        os.kill(os.getppid(), signal.SIGHUP)

if __name__ == "__main__":
    build()
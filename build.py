import os
import shutil
import subprocess

BUILD_DIR = "os_build"
RESOURCE_DIR = "res"

def clean_dir(dir : str):
    if os.path.exists(dir):
        shutil.rmtree(dir)
    os.makedirs(dir)

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
    
    
    subprocess.run(["qemu-system-i386w", "-m", "32", "-boot", "a", "-fda", "BOOT_FLP.IMG"])

if __name__ == "__main__":
    build()
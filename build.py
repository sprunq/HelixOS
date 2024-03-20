import os
import shutil
import subprocess
import signal
import platform
import sys
import argparse

BUILD_DIR = "os_build"
RESOURCE_DIR = "res"
START_DIR = os.getcwd()

def clean_dir(dir : str):
    if os.path.exists(dir):
        shutil.rmtree(dir)
    os.makedirs(dir)

def get_qemu() -> str:
   if platform.system() == 'Windows':
        return "qemu-system-i386w"
   else:
        return "qemu"

def build(sjc_jar_path_arg : str, cleanup : bool, autoclose : bool):
    sjc_jar_absolute = os.path.abspath(sjc_jar_path_arg)
    if not os.path.exists(sjc_jar_absolute):
        print(f"unable to locate SJC at {sjc_jar_absolute}")
        exit()

    clean_dir(BUILD_DIR)
    shutil.copy("bootconf.txt", BUILD_DIR)
    shutil.copy("bts_dsk.bin", BUILD_DIR)
    os.chdir(BUILD_DIR)

    output = subprocess.run(["java", "-jar", sjc_jar_absolute, "..\\src", "-o", "boot"],
                       capture_output=True,
                       text=True) 
    
    print(output.stdout)
    if output.stderr:
        print(output.stderr)

    if not os.path.exists("BOOT_FLP.IMG"):
        print("Detected errors during compilation. Aborting build..")
        exit()
    
    subprocess.run([get_qemu(), "-m", "32", "-boot", "a", "-fda", "BOOT_FLP.IMG"])

    # Cleanup
    os.chdir(START_DIR)
    
    if cleanup and os.path.exists(BUILD_DIR):
        shutil.rmtree(BUILD_DIR)

    if autoclose:
        # Close the terminal.
        # I need to do this since I run the script via the IntelliJ run button, which launches a new Console every run and
        # after too many runs it becomes very slow.
        if platform.system() != 'Linux':
            signal.SIGHUP = 1

        os.kill(os.getppid(), signal.SIGHUP)

if __name__ == "__main__":
    parser = argparse.ArgumentParser(prog='ToOS Build tool')
    parser.add_argument('--sjc', metavar='path_to_jar', type=str, required=True, help='path to the sjc jar file')
    parser.add_argument('--autoclose', action='store_true', help='auto close terminal after completion')
    parser.add_argument('--cleanup', action='store_true', help='delete build directory after completion')
    
    args = parser.parse_args()
    
    build(args.sjc, args.cleanup, args.autoclose)
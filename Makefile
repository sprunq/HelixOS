compilerdir = sjc
compiler_jar = sjc.jar
sourcedir = src
builddir = build/
includedir = include

install-sjc:
	curl https://www.fam-frenz.de/stefan/compsnpe.zip -o tools.zip
	mkdir -p $(compilerdir)
	unzip -d $(compilerdir) -j tools.zip  
	rm tools.zip

copy-to-build:
	mkdir -p $(builddir)
	cp -r $(includedir)/* $(builddir)


compile: copy-to-build
	cd $(builddir) 
	ls
	java -jar $(compilerdir)/$(compiler_jar) src/main -o raw -p $(builddir) -y -s 1440k -t ia32 -T nsop -x -u rte -g -G          

clean:
	rm -rf $(builddir)                 


run: compile
	 mkisofs -o cdimage.iso -N -b raw_out.bin -no-emul-boot -boot-load-seg 0x7C0 -boot-load-size 4 -V "SJCCD" -A "SJC compiled bootable OS" -graft-points CDBOOT/BOOT_ISO.IMG=raw_out.bin raw_out.bin 
	 qemu-system-x86_64 -m 1024 -net none -boot d -cdrom cdimage.iso -rtc base=localtime -serial file:$(builddir)/serial.log




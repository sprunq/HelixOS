compilerdir = sjc
compiler_jar = sjc.jar
path_compiler_jar = $(compilerdir)/$(compiler_jar)

sourcedir = src
builddir = build
includedir = include

raw_out = raw_out.bin
bootloader_img_name = bbk_iso.bin

cd_img_name = cdimage.iso


clean: 
	rm -rf $(builddir)   
	rm -rf $(compilerdir)   

install-sjc: 
	curl https://www.fam-frenz.de/stefan/compsnpe.zip -o tools.zip
	mkdir -p $(compilerdir)
	unzip -d $(compilerdir) -j tools.zip  
	rm tools.zip

install-sjc-if-needed:
	if [ ! -d $(compilerdir) ]; then make install-sjc; fi

compile: \
	install-sjc-if-needed
	mkdir -p $(builddir)
	cp -r $(includedir)/* $(builddir)
	java \
		-jar $(path_compiler_jar) \
		src/main \
		-o raw \
		-O $(builddir)/$(raw_out) \
		-p $(builddir) \
		-y \
		-s 500M \
		-t ia32 -T nsop \
		-x \
		-u rte \
		-g \
		-G \
		-D sym $(builddir)/symbols.txt

run: \
	compile
	mkisofs \
		-o $(builddir)/$(cd_img_name) \
		-N -b $(bootloader_img_name) \
		-no-emul-boot \
		-boot-load-seg 0x7C0 \
		-boot-load-size 4 \
		-V "SJCCD" \
		-A "SJC compiled bootable OS" \
		-graft-points CDBOOT/BOOT_ISO.IMG=$(builddir)/$(raw_out) \
		$(includedir)/$(bootloader_img_name)

	qemu-system-x86_64 \
		-m 1024 \
		-rtc base=localtime \
		-serial file:$(builddir)/serial.log \
		-boot d -cdrom $(builddir)/$(cd_img_name) 

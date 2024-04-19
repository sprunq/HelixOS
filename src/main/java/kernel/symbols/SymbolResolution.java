package kernel.symbols;

import kernel.MemoryLayout;
import rte.SClassDesc;
import rte.SMthdBlock;
import rte.SPackage;
import util.logging.Logger;

public class SymbolResolution {
    private static SClassDesc cd;
    private static SMthdBlock bootloader;

    public static void initialize() {
        cd = new SClassDesc();
        cd.name = "Bootloader";
        bootloader = new SMthdBlock();
        bootloader.namePar = "bootloader()";
        bootloader.nextMthd = null;
        bootloader.owner = cd;

        Logger.info("SymRes", "Initialized SymbolResolution");
    }

    @SJC.Inline
    public static SMthdBlock resolve(int addr) {
        if (MemoryLayout.BOOTLOADER_START <= addr && addr <= MemoryLayout.BOOTLOADER_END) {
            return bootloader;
        }
        return resolveInPackage(addr, SPackage.root);
    }

    @SJC.Inline
    public static SMthdBlock resolveInPackage(int addr, SPackage pkg) {
        while (pkg != null) {
            SMthdBlock found = resolveInPackage(addr, pkg.subPacks);
            if (found != null) {
                return found;
            }

            found = resolveInClass(addr, pkg.units);
            if (found != null) {
                return found;
            }
            pkg = pkg.nextPack;
        }
        return null;
    }

    @SJC.Inline
    public static SMthdBlock resolveInClass(int addr, SClassDesc cls) {
        while (cls != null) {
            SMthdBlock found = resolveInMethodBlock(addr, cls.mthds);
            if (found != null) {
                return found;
            }
            cls = cls.nextUnit;
        }
        return null;
    }

    @SJC.Inline
    public static SMthdBlock resolveInMethodBlock(int addr, SMthdBlock mths) {
        while (mths != null) {
            int start = MAGIC.cast2Ref(mths);
            int end = start + mths._r_scalarSize;
            if (start <= addr && addr <= end) {
                return mths;
            }
            mths = mths.nextMthd;
        }
        return null;
    }
}

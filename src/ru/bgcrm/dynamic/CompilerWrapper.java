package ru.bgcrm.dynamic;

import java.io.File;
/**
 * Враппер для компилятора.
 */
@Deprecated
public final class CompilerWrapper extends org.bgerp.custom.java.CompilerWrapper {
    public CompilerWrapper(File srcDir) {
        super(srcDir);
    }

    @Override
    protected File getClassSrc(String className) {
        return DynamicClassManager.getClassFile(className);
    }
}
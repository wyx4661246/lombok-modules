package com.marzaha.lombok.modules.utils;


import com.marzaha.lombok.modules.enums.BinaryTagEnum;
import com.marzaha.lombok.modules.enums.JavaVersionEnum;

import javax.lang.model.SourceVersion;

public class JavacConvertUtil {
    public JavacConvertUtil() {
    }

    public static int convertBinary(BinaryTagEnum binaryTagEnum) {
        if (binaryTagEnum == null) {
            return 0;
        } else {
            String code = binaryTagEnum.getCode();
            Object plus;
            if (BinaryTagEnum.PLUS.getCode().equals(code)) {
                plus = classForNameStaticAttr("com.sun.tools.javac.tree.JCTree", "PLUS");
                return plus == null ? 0 : (Integer)plus;
            } else if (BinaryTagEnum.EQ.getCode().equals(code)) {
                plus = classForNameStaticAttr("com.sun.tools.javac.tree.JCTree", "EQ");
                return plus == null ? 60 : (Integer)plus;
            } else if (BinaryTagEnum.NE.getCode().equals(code)) {
                plus = classForNameStaticAttr("com.sun.tools.javac.tree.JCTree", "NE");
                return plus == null ? 61 : (Integer)plus;
            } else {
                return BinaryTagEnum.BOT.getCode().equals(code) ? 17 : 0;
            }
        }
    }

    public static Object convertBinaryTag(BinaryTagEnum binaryTagEnum) {
        if (binaryTagEnum == null) {
            return null;
        } else {
            String code = binaryTagEnum.getCode();
            if (BinaryTagEnum.PLUS.getCode().equals(code)) {
                return classForNameEnume("com.sun.tools.javac.tree.JCTree$Tag", "PLUS");
            } else if (BinaryTagEnum.EQ.getCode().equals(code)) {
                return classForNameEnume("com.sun.tools.javac.tree.JCTree$Tag", "EQ");
            } else if (BinaryTagEnum.NE.getCode().equals(code)) {
                return classForNameEnume("com.sun.tools.javac.tree.JCTree$Tag", "NE");
            } else {
                return BinaryTagEnum.BOT.getCode().equals(code) ? classForNameEnume("com.sun.tools.javac.code.TypeTag", "BOT") : null;
            }
        }
    }

    public static SourceVersion convertSourceVersion(JavaVersionEnum javaVersionEnum) {
        if (javaVersionEnum == null) {
            return SourceVersion.RELEASE_6;
        } else {
            String code = javaVersionEnum.getCode();
            String version = "RELEASE_6";
            if (JavaVersionEnum.VERSION_6.getCode().equals(code)) {
                version = "RELEASE_6";
            } else if (JavaVersionEnum.VERSION_7.getCode().equals(code)) {
                version = "RELEASE_7";
            } else if (JavaVersionEnum.VERSION_8.getCode().equals(code)) {
                version = "RELEASE_8";
            }

            Enum enume = classForNameEnume("javax.lang.model.SourceVersion", version);
            return enume == null ? SourceVersion.RELEASE_6 : (SourceVersion)enume;
        }
    }

    public static Enum classForNameEnume(String className, String value) {
        try {
            Class c = Class.forName(className);
            return Enum.valueOf(c, value);
        } catch (Exception var3) {
            return null;
        }
    }

    public static Object classForNameStaticAttr(String className, String field) {
        try {
            Class c = Class.forName(className);
            return c.getField(field).get((Object)null);
        } catch (Exception var3) {
            return null;
        }
    }
}

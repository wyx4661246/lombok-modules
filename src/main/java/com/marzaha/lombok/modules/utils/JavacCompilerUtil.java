package com.marzaha.lombok.modules.utils;

import com.sun.tools.javac.code.Source;
import com.sun.tools.javac.main.JavaCompiler;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JavacCompilerUtil {
    private static final Pattern VERSION_PARSER = Pattern.compile("^(\\d{1,6})\\.(\\d{1,6}).*$");
    private static final Pattern SOURCE_PARSER = Pattern.compile("^JDK(\\d{1,6})_(\\d{1,6}).*$");
    private static final AtomicInteger compilerVersion = new AtomicInteger(-1);

    public JavacCompilerUtil() {
    }

    public static int getJavaCompilerVersion() {
        int cv = compilerVersion.get();
        if (cv != -1) {
            return cv;
        } else {
            Matcher m = VERSION_PARSER.matcher(JavaCompiler.version());
            int major;
            if (m.matches()) {
                //int major = Integer.parseInt(m.group(1)); todo
                major = Integer.parseInt(m.group(2));
                if (major == 1) {
                    compilerVersion.set(major);
                    return major;
                }
            }

            String name = Source.values()[Source.values().length - 1].name();
            //Matcher todo
            m = SOURCE_PARSER.matcher(name);
            if (m.matches()) {
                major = Integer.parseInt(m.group(1));
                int minor = Integer.parseInt(m.group(2));
                if (major == 1) {
                    compilerVersion.set(minor);
                    return minor;
                }
            }

            compilerVersion.set(6);
            return 6;
        }
    }
}

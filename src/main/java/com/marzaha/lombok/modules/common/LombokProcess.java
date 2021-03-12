package com.marzaha.lombok.modules.common;



import com.marzaha.lombok.modules.utils.PropertieUtil;

import javax.annotation.processing.Messager;
import javax.tools.Diagnostic.Kind;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class LombokProcess {
    private static boolean PRINTMSG = true;
    private static Kind LOGLEVEL;
    public static Messager MESSAGER;
    public static Map<String, String> MASK_FIELD_MAP;

    public LombokProcess() {
    }

    public static void printMessage(Kind kind, String message) {
        if (PRINTMSG) {
            if (kind == null) {
                kind = Kind.NOTE;
            }

            if (LOGLEVEL == Kind.WARNING) {
                if (kind != Kind.WARNING && kind != Kind.ERROR) {
                    return;
                }
            } else if (LOGLEVEL == Kind.ERROR && kind != Kind.ERROR) {
                return;
            }

            if (MESSAGER != null) {
                MESSAGER.printMessage(kind, message);
            }
        }

    }

    public static void loadMaskFields() {
        printMessage(Kind.NOTE, "load lombok-mask.properties");
        InputStream inputStream = null;
        Map pmaps = null;

        try {
            inputStream = LombokProcess.class.getClassLoader().getResourceAsStream("lombok-mask.properties");
            pmaps = PropertieUtil.getConfigProperties(inputStream);
        } catch (Exception var13) {
            printMessage(Kind.WARNING, "load lombok-mask.properties ERROR");
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Exception var12) {
                    printMessage(Kind.WARNING, "close lombok-mask.properties ERROR");
                }
            }

        }

        if (pmaps != null) {
            Iterator var3 = pmaps.entrySet().iterator();

            while(var3.hasNext()) {
                Entry<Object, Object> entry = (Entry)var3.next();
                Object key = entry.getKey();
                Object value = entry.getValue();
                String mask = String.valueOf(value);
                MASK_FIELD_MAP.put(String.valueOf(key), mask != null && !mask.isEmpty() ? mask : "");
            }

            printMessage(Kind.NOTE, "load lombok-mask.properties SUCESS");
        }

    }

    static {
        LOGLEVEL = Kind.WARNING;
        MASK_FIELD_MAP = new HashMap();
    }
}



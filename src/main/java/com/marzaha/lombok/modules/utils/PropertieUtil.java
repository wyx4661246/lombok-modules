package com.marzaha.lombok.modules.utils;


import com.marzaha.lombok.modules.common.LinkedProperties;
import com.marzaha.lombok.modules.common.LombokProcess;

import javax.tools.Diagnostic;
import java.io.InputStream;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;


public class PropertieUtil {
    public PropertieUtil() {
    }

    public static Map<Object, Object> getConfigProperties(InputStream input) {
        LinkedHashMap pmap = new LinkedHashMap();

        try {
            LinkedProperties props = new LinkedProperties();
            props.load(input);
            Iterator iterator = props.entrySet().iterator();

            while (iterator.hasNext()) {
                Map.Entry<Object, Object> entery = (Map.Entry) iterator.next();
                pmap.put(entery.getKey(), entery.getValue());
            }
        } catch (Exception var5) {
            LombokProcess.printMessage(Diagnostic.Kind.WARNING, "load lombok-mask.properties getConfigProperties ERROR");
        }

        return pmap;
    }
}

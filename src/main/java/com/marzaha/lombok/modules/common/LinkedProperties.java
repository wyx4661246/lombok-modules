package com.marzaha.lombok.modules.common;

import java.io.IOException;
import java.io.Writer;
import java.util.*;

public class LinkedProperties extends Properties {
    private Map<Object, Object> linkMap = new LinkedHashMap();

    public LinkedProperties() {
    }

    public synchronized Enumeration keys() {
        List<Object> keys = new ArrayList();
        Iterator iterator = this.linkMap.entrySet().iterator();

        while(iterator.hasNext()) {
            Map.Entry<Object, Object> entry = (Map.Entry)iterator.next();
            keys.add(entry.getKey());
        }

        return Collections.enumeration(keys);
    }

    public synchronized Object put(Object key, Object value) {
        super.put(key, value);
        return this.linkMap.put(key, value);
    }

    public synchronized boolean contains(Object value) {
        return this.linkMap.containsValue(value);
    }

    public boolean containsValue(Object value) {
        return this.linkMap.containsValue(value);
    }

    public Set<Map.Entry<Object, Object>> entrySet() {
        return this.linkMap.entrySet();
    }

    public synchronized void clear() {
        super.clear();
        this.linkMap.clear();
    }

    public synchronized boolean containsKey(Object key) {
        return this.linkMap.containsKey(key);
    }

    public void store(Writer writer, String comments) throws IOException {
        super.putAll(this.linkMap);
        super.store(writer, comments);
    }
}


package com.marzaha.lombok.modules.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.*;

public class ToStringUtil {
    public ToStringUtil() {
    }

    public static String mapToString(String attrName, Map<?, ?> map) {
        if (map == null) {
            return buildJsonKey(attrName) + "null";
        } else {
            return map.isEmpty() ? buildJsonKey(attrName) + "{}" : buildJsonKey(attrName) + toJson(map);
        }
    }

    public static String toJson(Object value) {
        if (value == null) {
            return null;
        } else if (value instanceof String) {
            return buildJsonString((String)value);
        } else if (value instanceof Date) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return "\"" + sdf.format(value) + "\"";
        } else if (value instanceof List) {
            return listToString((List)value);
        } else if (value instanceof Set) {
            return setToString((Set)value);
        } else if (value instanceof Map) {
            return mapToString((Map)value);
        } else {
            Class className = value.getClass();
            if (className.isArray()) {
                return arrayToString(value);
            } else {
                return !className.isPrimitive() && !className.equals(Integer.class) && !className.equals(Byte.class) && !className.equals(Long.class) && !className.equals(Double.class) && !className.equals(Float.class) && !className.equals(Character.class) && !className.equals(Short.class) && !className.equals(Boolean.class) ? value.toString() : value.toString();
            }
        }
    }

    private static String userDefinedClassToString(Object object) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        List<Field> fields = getAllField(object);

        for(int i = 0; i < fields.size(); ++i) {
            String fieldName = ((Field)fields.get(i)).getName();
            if (!"serialVersionUID".equals(fieldName)) {
                Object fieldValue = getFieldValueByName(fieldName, object);
                if (null == fieldValue) {
                    fieldValue = getBooleanFieldValueByName(fieldName, object);
                    if (null == fieldValue) {
                        continue;
                    }
                }

                sb.append(buildJsonKey(fieldName)).append(toJson(fieldValue));
                if (i == fields.size() - 1) {
                    sb.append('}');
                } else {
                    sb.append(',');
                }
            }
        }

        if (sb.length() > 1 && sb.charAt(sb.length() - 1) == ',') {
            sb.deleteCharAt(sb.length() - 1);
            sb.append("}");
        }

        return sb.toString();
    }

    private static List<Field> getAllField(Object object) {
        List<Field> fields = new ArrayList();

        for(Class cls = object.getClass(); cls != Object.class; cls = cls.getSuperclass()) {
            Collections.addAll(fields, cls.getDeclaredFields());
        }

        return fields;
    }

    private static Object getFieldValueByName(String fieldName, Object o) {
        try {
            String firstLetter = fieldName.substring(0, 1).toUpperCase();
            String getter = "get" + firstLetter + fieldName.substring(1);
            Method method = o.getClass().getMethod(getter);
            return method.invoke(o);
        } catch (Exception var5) {
            return null;
        }
    }

    private static Object getBooleanFieldValueByName(String fieldName, Object o) {
        try {
            String firstLetter = fieldName.substring(0, 1).toUpperCase();
            String getter = "is" + firstLetter + fieldName.substring(1);
            Method method = o.getClass().getMethod(getter);
            return method.invoke(o);
        } catch (Exception var5) {
            return null;
        }
    }

    private static String mapToString(Map<?, ?> map) {
        Iterator<? extends Map.Entry<?, ?>> i = map.entrySet().iterator();
        if (!i.hasNext()) {
            return "{}";
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append('{');

            while(true) {
                while(true) {
                    Map.Entry e = (Map.Entry)i.next();
                    Object value = e.getValue();
                    if (null != value) {
                        String key = e.getKey() instanceof String ? buildJsonKey(e.getKey().toString()) : toJson(e.getKey()) + ":";
                        sb.append(key).append(toJson(value));
                        if (!i.hasNext()) {
                            return sb.append('}').toString();
                        }

                        sb.append(',');
                    }
                }
            }
        }
    }

    private static String setToString(Set<?> set) {
        Iterator<?> i = set.iterator();
        return collectionToString(i);
    }

    private static String listToString(List<?> list) {
        Iterator<?> i = list.iterator();
        return collectionToString(i);
    }

    private static String collectionToString(Iterator<?> i) {
        if (!i.hasNext()) {
            return "[]";
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append('[');

            while(true) {
                while(true) {
                    Object value = i.next();
                    if (null != value) {
                        sb.append(toJson(value));
                        if (!i.hasNext()) {
                            return sb.append(']').toString();
                        }

                        sb.append(',');
                    }
                }
            }
        }
    }

    private static String arrayToString(Object value) {
        if (value instanceof int[]) {
            return Arrays.toString((int[])((int[])value));
        } else if (value instanceof Integer[]) {
            return Arrays.toString((Integer[])((Integer[])value));
        } else if (value instanceof char[]) {
            return Arrays.toString((char[])((char[])value));
        } else if (value instanceof Character[]) {
            return Arrays.toString((Character[])((Character[])value));
        } else if (value instanceof long[]) {
            return Arrays.toString((long[])((long[])value));
        } else if (value instanceof Long[]) {
            return Arrays.toString((Long[])((Long[])value));
        } else if (value instanceof double[]) {
            return Arrays.toString((double[])((double[])value));
        } else if (value instanceof Double[]) {
            return Arrays.toString((Double[])((Double[])value));
        } else if (value instanceof String[]) {
            return arrayToString((String[])((String[])value));
        } else {
            return value instanceof Object[] ? arrayToString((Object[])((Object[])value)) : value.toString();
        }
    }

    private static String arrayToString(String[] a) {
        if (a == null) {
            return "null";
        } else {
            int iMax = a.length - 1;
            if (iMax == -1) {
                return "[]";
            } else {
                StringBuilder b = new StringBuilder();
                b.append('[');
                int i = 0;

                while(true) {
                    b.append('"').append(a[i]).append('"');
                    if (i == iMax) {
                        return b.append(']').toString();
                    }

                    b.append(',');
                    ++i;
                }
            }
        }
    }

    private static String arrayToString(Object[] a) {
        if (a == null) {
            return "null";
        } else {
            int iMax = a.length - 1;
            if (iMax == -1) {
                return "[]";
            } else {
                StringBuilder b = new StringBuilder();
                b.append('[');
                int i = 0;

                while(true) {
                    b.append(toJson(a[i]));
                    if (i == iMax) {
                        return b.append(']').toString();
                    }

                    b.append(',');
                    ++i;
                }
            }
        }
    }

    private static String buildJsonKey(String key) {
        return "\"" + key + "\":";
    }

    private static String buildJsonString(String str) {
        return "\"" + str + "\"";
    }

    private static String buildJsonStringV2(String str) {
        return "\"" + str.replaceAll("\"", "\\\\\"") + "\"";
    }

    public static void main(String[] args) {
        buildJsonStringTest();
        A a = new A();
        a.a1 = 1;
        a.a2 = 2;
        a.a3 = 3L;
        a.a4 = 4L;
        a.a5 = 5.0D;
        a.a6 = 6.0D;
        a.a7 = "7";
        a.a8 = new int[]{8, 8, 8};
        a.a9 = Arrays.asList("9", "9", "9");
        a.a10 = new HashMap();
        a.a10.put("a10.key", "a10.\"value\"");
        a.a11 = new String[]{"11", "11", "11"};
        a.a12 = new HashSet();
        a.a12.add("a12.value");
        System.out.println(a);
        SubA subA = new SubA();
        subA.setSubA1("subA1");
        subA.setA1(1);
        subA.setA2(2);
        subA.setA3(3L);
        subA.setA4(4L);
        subA.setA5(5.0D);
        subA.setA6(6.0D);
        subA.setA7("7");
        subA.setA8(new int[]{8, 8, 8});
        subA.setA9(Arrays.asList("9", "9", "9"));
        subA.setA10(new HashMap());
        subA.getA10().put("a10.key", "a10.\"value\"");
        subA.getA10().put("null", null);
        subA.setA11(new String[]{"11", "11", "11"});
        System.out.println(toJson(subA));
        B b = new B();
        b.setB1("b1");
        b.setB2(new HashMap());
        b.getB2().put("a", a);
        b.setSubA(subA);
        System.out.println(toJson(b));
        Set<String> set = new HashSet();
        set.add("a");
        set.add("b");
        System.out.println(toJson(set));
        List<String> list = new ArrayList();
        list.add("a");
        list.add("b");
        System.out.println(toJson(list));
    }

    private static void buildJsonStringTest() {
        String str = "\"中文\"";
        System.out.println(buildJsonKey(str));
        System.out.println(buildJsonStringV2(str));
        long start = System.currentTimeMillis();

        int i;
        for(i = 0; i < 2000000; ++i) {
            buildJsonString(str);
        }

        System.out.println(System.currentTimeMillis() - start);
        start = System.currentTimeMillis();

        for(i = 0; i < 2000000; ++i) {
            buildJsonStringV2(str);
        }

        System.out.println(System.currentTimeMillis() - start);
    }

    static class B {
        private String b1;
        private Map<String, Object> b2;
        private SubA subA;

        B() {
        }

        public String toString() {
            return "{\"b1\":\"" + this.b1 + "\", \"b2\":" + ToStringUtil.toJson(this.b2) + ", \"subA\":" + this.subA + "}";
        }

        public String getB1() {
            return this.b1;
        }

        public void setB1(String b1) {
            this.b1 = b1;
        }

        public Map<String, Object> getB2() {
            return this.b2;
        }

        public void setB2(Map<String, Object> b2) {
            this.b2 = b2;
        }

        public SubA getSubA() {
            return this.subA;
        }

        public void setSubA(SubA subA) {
            this.subA = subA;
        }
    }

    static class SubA extends A {
        private String subA1;

        SubA() {
        }

        public String toString() {
            return "{\"subA1\":\"" + this.subA1 + "\"}" + super.toString();
        }

        public String getSubA1() {
            return this.subA1;
        }

        public void setSubA1(String subA1) {
            this.subA1 = subA1;
        }
    }

    static class A {
        private int a1;
        private Integer a2;
        private long a3;
        private Long a4;
        private double a5;
        private Double a6;
        private String a7;
        private int[] a8;
        private List<String> a9;
        private Map<String, String> a10;
        private String[] a11;
        private Set<String> a12;

        A() {
        }

        public String toString() {
            return "{\"a1\":" + this.a1 + ", \"a2\":" + this.a2 + ", \"a3\":" + this.a3 + ", \"a4\":" + this.a4 + ", \"a5\":" + this.a5 + ", \"a6\":" + this.a6 + ", \"a7\":\"" + this.a7 + "\", \"a8\":" + ToStringUtil.toJson(this.a8) + ", \"a9\":" + ToStringUtil.toJson(this.a9) + ", \"a10\":" + ToStringUtil.toJson(this.a10) + ", \"a11\":" + ToStringUtil.toJson(this.a11) + ", \"a12\":" + ToStringUtil.toJson(this.a12) + "}";
        }

        public int getA1() {
            return this.a1;
        }

        public void setA1(int a1) {
            this.a1 = a1;
        }

        public Integer getA2() {
            return this.a2;
        }

        public void setA2(Integer a2) {
            this.a2 = a2;
        }

        public long getA3() {
            return this.a3;
        }

        public void setA3(long a3) {
            this.a3 = a3;
        }

        public Long getA4() {
            return this.a4;
        }

        public void setA4(Long a4) {
            this.a4 = a4;
        }

        public double getA5() {
            return this.a5;
        }

        public void setA5(double a5) {
            this.a5 = a5;
        }

        public Double getA6() {
            return this.a6;
        }

        public void setA6(Double a6) {
            this.a6 = a6;
        }

        public String getA7() {
            return this.a7;
        }

        public void setA7(String a7) {
            this.a7 = a7;
        }

        public int[] getA8() {
            return this.a8;
        }

        public void setA8(int[] a8) {
            this.a8 = a8;
        }

        public List<String> getA9() {
            return this.a9;
        }

        public void setA9(List<String> a9) {
            this.a9 = a9;
        }

        public Map<String, String> getA10() {
            return this.a10;
        }

        public void setA10(Map<String, String> a10) {
            this.a10 = a10;
        }

        public String[] getA11() {
            return this.a11;
        }

        public void setA11(String[] a11) {
            this.a11 = a11;
        }

        public Set<String> getA12() {
            return this.a12;
        }

        public void setA12(Set<String> a12) {
            this.a12 = a12;
        }
    }
}

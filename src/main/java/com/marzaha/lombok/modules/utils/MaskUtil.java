package com.marzaha.lombok.modules.utils;

public class MaskUtil {
    private static String MODE1 = "\\.\\d+,[^(\\+,\\d,\\,,\\.)]{1}\\d+,\\.\\+{1}";
    private static String MODE2 = "[^(\\+,\\d,\\,,\\.)]{1}\\d+,\\.\\+{1}";
    private static String MODE3 = "\\.\\+{1},[^(\\+,\\d,\\,,\\.)]{1}\\d+,\\.\\d+";
    private static String MODE4 = "\\.\\+{1},[^(\\+,\\d,\\,,\\.)]{1}\\d+";
    private static String MODE5 = "[^(\\+,\\d,\\,,\\.)]{1}\\d+,\\.\\+{1},[^(\\+,\\d,\\,,\\.)]{1}\\d+";
    private static String MODE6 = "\\.\\d+,[^(\\+,\\d,\\,,\\.)]\\+{1},\\.\\d+";

    public MaskUtil() {
    }

    public static String mask(Object obj, String expression) {
        if (obj == null) {
            return null;
        } else if (!(obj instanceof String)) {
            return obj.toString();
        } else {
            String source = (String)obj;
            if (!isBlank(source) && !isBlank(expression)) {
                try {
                    if (expression.matches(MODE1)) {
                        return expressionMode1(source, expression);
                    }

                    if (expression.matches(MODE2)) {
                        return expressionMode2(source, expression);
                    }

                    if (expression.matches(MODE3)) {
                        return expressionMode3(source, expression);
                    }

                    if (expression.matches(MODE4)) {
                        return expressionMode4(source, expression);
                    }

                    if (expression.matches(MODE5)) {
                        return expressionMode5(source, expression);
                    }

                    if (expression.matches(MODE6)) {
                        return expressionMode6(source, expression);
                    }
                } catch (Exception var4) {
                    System.out.println("lombok plugin mask error" + var4.getMessage());
                }

                return source;
            } else {
                return source;
            }
        }
    }

    private static String expressionMode1(String source, String expression) {
        String[] split1 = expression.split(",");
        String s1 = split1[0].replaceAll("\\.", "");
        String maskNum = split1[1].replaceAll("\\D", "");
        String mask = split1[1].replaceAll("\\d", "");
        String patternstr = String.format("(.{%s}).{%s}(.*)", s1, maskNum);
        int index = Integer.valueOf(maskNum);
        StringBuffer maskBuffer = new StringBuffer();

        for(int i = 0; i < index; ++i) {
            maskBuffer.append(mask);
        }

        String replaceMent = "$1" + maskBuffer + "$2";
        return source.replaceAll(patternstr, replaceMent);
    }

    private static String expressionMode2(String source, String expression) {
        String[] split1 = expression.split(",");
        String maskNum = split1[0].replaceAll("\\D", "");
        String mask = split1[0].replaceAll("\\d", "");
        String patternstr = String.format(".{%s}(.*)", maskNum);
        int index = Integer.valueOf(maskNum);
        StringBuffer maskBuffer = new StringBuffer();

        for(int i = 0; i < index; ++i) {
            maskBuffer.append(mask);
        }

        String replaceMent = maskBuffer + "$1";
        return source.replaceAll(patternstr, replaceMent);
    }

    private static String expressionMode3(String source, String expression) {
        String[] split1 = expression.split(",");
        String s1 = split1[2].replaceAll("\\.", "");
        String maskNum = split1[1].replaceAll("\\D", "");
        String mask = split1[1].replaceAll("\\d", "");
        String patternstr = String.format("(.*).{%s}(.{%s})", maskNum, s1);
        int index = Integer.valueOf(maskNum);
        StringBuffer maskBuffer = new StringBuffer();

        for(int i = 0; i < index; ++i) {
            maskBuffer.append(mask);
        }

        String replaceMent = "$1" + maskBuffer + "$2";
        return source.replaceAll(patternstr, replaceMent);
    }

    private static String expressionMode4(String source, String expression) {
        String[] split1 = expression.split(",");
        String maskNum = split1[1].replaceAll("\\D", "");
        String mask = split1[1].replaceAll("\\d", "");
        String patternstr = String.format("(.*).{%s}", maskNum);
        int index = Integer.valueOf(maskNum);
        StringBuffer maskBuffer = new StringBuffer();

        for(int i = 0; i < index; ++i) {
            maskBuffer.append(mask);
        }

        String replaceMent = "$1" + maskBuffer;
        return source.replaceAll(patternstr, replaceMent);
    }

    private static String expressionMode5(String source, String expression) {
        String[] split1 = expression.split(",");
        String maskNum1 = split1[0].replaceAll("\\D", "");
        String mask1 = split1[0].replaceAll("\\d", "");
        String maskNum2 = split1[2].replaceAll("\\D", "");
        String mask2 = split1[2].replaceAll("\\d", "");
        String patternstr = String.format(".{%s}(.*).{%s}", maskNum1, maskNum2);
        int index1 = Integer.valueOf(maskNum1);
        StringBuffer maskBuffer1 = new StringBuffer();

        int index2;
        for(index2 = 0; index2 < index1; ++index2) {
            maskBuffer1.append(mask1);
        }

        index2 = Integer.valueOf(maskNum2);
        StringBuffer maskBuffer2 = new StringBuffer();

        for(int i = 0; i < index2; ++i) {
            maskBuffer2.append(mask2);
        }

        String replaceMent = maskBuffer1 + "$1" + maskBuffer2;
        return source.replaceAll(patternstr, replaceMent);
    }

    private static String expressionMode6(String source, String expression) {
        String[] split1 = expression.split(",");
        String noMaskNum1 = split1[0].replaceAll("\\D", "");
        String mask = split1[1].replaceAll("\\+", "");
        String noMaskNum2 = split1[2].replaceAll("\\D", "");
        String patternstr = String.format("(.{%s}).*(.{%s})", noMaskNum1, noMaskNum2);
        int index1 = Integer.valueOf(noMaskNum1);
        int index2 = Integer.valueOf(noMaskNum2);
        int index3 = source.length() - index1 - index2;
        StringBuffer maskBuffer = new StringBuffer();

        for(int i = 0; i < index3; ++i) {
            maskBuffer.append(mask);
        }

        String replaceMent = "$1" + maskBuffer + "$2";
        return source.replaceAll(patternstr, replaceMent);
    }

    public static boolean isBlank(CharSequence cs) {
        int strLen;
        if (cs != null && (strLen = cs.length()) != 0) {
            for(int i = 0; i < strLen; ++i) {
                if (!Character.isWhitespace(cs.charAt(i))) {
                    return false;
                }
            }

            return true;
        } else {
            return true;
        }
    }
}

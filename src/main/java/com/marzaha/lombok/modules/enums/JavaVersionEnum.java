package com.marzaha.lombok.modules.enums;

public enum JavaVersionEnum {
    VERSION_6("1.6"),
    VERSION_7("1.7"),
    VERSION_8("1.8");

    private final String code;

    private JavaVersionEnum(String code) {
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }
}

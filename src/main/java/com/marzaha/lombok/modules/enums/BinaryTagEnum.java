package com.marzaha.lombok.modules.enums;

public enum BinaryTagEnum {
    PLUS("1", "+"),
    EQ("60", "="),
    NE("61", "!="),
    BOT("17", "null");

    private final String code;
    private final String desc;

    private BinaryTagEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return this.code;
    }

    public String getDesc() {
        return this.desc;
    }
}

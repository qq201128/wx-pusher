package com.wang.enums;

/**
 * 状态（success or other）
 */
public enum WxStatusType {
    /**
     * 成功
     */
    SUCCESS("success"),

    FAIL("fail");
    private final String type;

    WxStatusType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}

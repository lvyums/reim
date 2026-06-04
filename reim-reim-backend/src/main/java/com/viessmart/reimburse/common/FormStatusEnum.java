package com.viessmart.reimburse.common;

public enum FormStatusEnum {

    DRAFT(1, "未提交"),
    SUBMITTED(2, "已提交"),
    DELETED(3, "已删除"),
    CANCELED(4, "已作废"),
    ;

    private final Integer code;
    private final String desc;

    FormStatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static FormStatusEnum getByCode(Integer code) {
        if (code == null) return null;
        for (FormStatusEnum status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        return null;
    }

    public static String getDescByCode(Integer code) {
        FormStatusEnum status = getByCode(code);
        return status == null ? "未知" : status.desc;
    }

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}

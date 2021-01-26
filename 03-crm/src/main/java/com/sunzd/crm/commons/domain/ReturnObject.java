package com.sunzd.crm.commons.domain;

/**
 * SZD
 * 2020/12/23
 * 16:33
 */
public class ReturnObject {
    private String code;

    private String message;

    private Object retData;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getRetData() {
        return retData;
    }

    public void setRetData(Object retData) {
        this.retData = retData;
    }
}

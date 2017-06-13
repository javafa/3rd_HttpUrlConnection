package com.veryworks.android.httpurlconnection.domain;

/**
 * Created by pc on 6/13/2017.
 */

public class RESULT {
    private String MESSAGE;

    private String CODE;

    public String getMESSAGE () {
        return MESSAGE;
    }

    public void setMESSAGE (String MESSAGE) {
        this.MESSAGE = MESSAGE;
    }

    public String getCODE () {
        return CODE;
    }

    public void setCODE (String CODE) {
        this.CODE = CODE;
    }

    @Override
    public String toString() {
        return "ClassPojo [MESSAGE = "+MESSAGE+", CODE = "+CODE+"]";
    }
}

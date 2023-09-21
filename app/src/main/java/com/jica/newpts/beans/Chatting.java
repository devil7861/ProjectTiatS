package com.jica.newpts.beans;

import com.google.firebase.Timestamp;

public class Chatting {
    private int c_idx;
    private String c_user;
    private Timestamp c_date;
    private String c_content;

    public int getC_idx() {
        return c_idx;
    }

    public void setC_idx(int c_idx) {
        this.c_idx = c_idx;
    }

    public String getC_user() {
        return c_user;
    }

    public void setC_user(String c_user) {
        this.c_user = c_user;
    }

    public Timestamp getC_date() {
        return c_date;
    }

    public void setC_date(Timestamp c_date) {
        this.c_date = c_date;
    }

    public String getC_content() {
        return c_content;
    }

    public void setC_content(String c_content) {
        this.c_content = c_content;
    }
}

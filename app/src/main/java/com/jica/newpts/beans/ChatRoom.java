package com.jica.newpts.beans;

import com.google.firebase.Timestamp;

public class ChatRoom {
    private int c_idx;
    private String c_send;
    private String c_receive;
    private String c_subject;
    private Timestamp c_date;
    private boolean c_delete;
    private Timestamp c_del_date;

    public int getC_idx() {
        return c_idx;
    }

    public void setC_idx(int c_idx) {
        this.c_idx = c_idx;
    }

    public String getC_send() {
        return c_send;
    }

    public void setC_send(String c_send) {
        this.c_send = c_send;
    }

    public String getC_receive() {
        return c_receive;
    }

    public void setC_receive(String c_receive) {
        this.c_receive = c_receive;
    }

    public String getC_subject() {
        return c_subject;
    }

    public void setC_subject(String c_subject) {
        this.c_subject = c_subject;
    }

    public Timestamp getC_date() {
        return c_date;
    }

    public void setC_date(Timestamp c_date) {
        this.c_date = c_date;
    }

    public boolean isC_delete() {
        return c_delete;
    }

    public void setC_delete(boolean c_delete) {
        this.c_delete = c_delete;
    }

    public Timestamp getC_del_date() {
        return c_del_date;
    }

    public void setC_del_date(Timestamp c_del_date) {
        this.c_del_date = c_del_date;
    }
}

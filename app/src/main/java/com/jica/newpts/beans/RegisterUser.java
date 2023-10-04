package com.jica.newpts.beans;

import com.google.firebase.Timestamp;

public class RegisterUser {
    private int u_idx;
    private String u_id;
    private String u_name;
    private Timestamp u_date;
    private String u_photo;
    private String u_phone;
    private String u_address1;
    private String u_address2;

    public int getU_idx() {
        return u_idx;
    }

    public void setU_idx(int u_idx) {
        this.u_idx = u_idx;
    }

    public String getU_id() {
        return u_id;
    }

    public void setU_id(String u_id) {
        this.u_id = u_id;
    }

    public String getU_name() {
        return u_name;
    }

    public void setU_name(String u_name) {
        this.u_name = u_name;
    }

    public Timestamp getU_date() {
        return u_date;
    }

    public void setU_date(Timestamp u_date) {
        this.u_date = u_date;
    }

    public String getU_photo() {
        return u_photo;
    }

    public void setU_photo(String u_photo) {
        this.u_photo = u_photo;
    }

    public String getU_phone() {
        return u_phone;
    }

    public void setU_phone(String u_phone) {
        this.u_phone = u_phone;
    }

    public String getU_address1() {
        return u_address1;
    }

    public void setU_address1(String u_address1) {
        this.u_address1 = u_address1;
    }

    public String getU_address2() {
        return u_address2;
    }

    public void setU_address2(String u_address2) {
        this.u_address2 = u_address2;
    }
}

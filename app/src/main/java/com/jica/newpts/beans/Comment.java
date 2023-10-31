package com.jica.newpts.beans;

import com.google.firebase.Timestamp;

public class Comment {
    private int r_idx;
    private int r_parent_idx;
    private int r_child_idx;
    private int r_level_idx;
    private String r_content;
    private String r_user;
    private Timestamp r_date;
    private String r_profile_photo;
    private String r_check_level;
    private String r_name;
    private Boolean r_delete;
    private int f_board_idx;

    public Comment() {
    }

    public Comment(int r_idx, int r_parent_idx, int r_child_idx, int r_level_idx, String r_content, String r_user, Timestamp r_date, String r_profile_photo, String r_check_level) {
        this.r_idx = r_idx;
        this.r_parent_idx = r_parent_idx;
        this.r_child_idx = r_child_idx;
        this.r_level_idx = r_level_idx;
        this.r_content = r_content;
        this.r_user = r_user;
        this.r_date = r_date;
        this.r_profile_photo = r_profile_photo;
        this.r_check_level = r_check_level;
    }

    public int getR_idx() {
        return r_idx;
    }

    public void setR_idx(int r_idx) {
        this.r_idx = r_idx;
    }

    public int getR_parent_idx() {
        return r_parent_idx;
    }

    public void setR_parent_idx(int r_parent_idx) {
        this.r_parent_idx = r_parent_idx;
    }

    public int getR_child_idx() {
        return r_child_idx;
    }

    public void setR_child_idx(int r_child_idx) {
        this.r_child_idx = r_child_idx;
    }

    public int getR_level_idx() {
        return r_level_idx;
    }

    public void setR_level_idx(int r_level_idx) {
        this.r_level_idx = r_level_idx;
    }

    public String getR_content() {
        return r_content;
    }

    public void setR_content(String r_content) {
        this.r_content = r_content;
    }

    public String getR_user() {
        return r_user;
    }

    public void setR_user(String r_user) {
        this.r_user = r_user;
    }

    public Timestamp getR_date() {
        return r_date;
    }

    public void setR_date(Timestamp r_date) {
        this.r_date = r_date;
    }

    public String getR_profile_photo() {
        return r_profile_photo;
    }

    public void setR_profile_photo(String r_profile_photo) {
        this.r_profile_photo = r_profile_photo;
    }

    public String getR_check_level() {
        return r_check_level;
    }

    public void setR_check_level(String r_check_level) {
        this.r_check_level = r_check_level;
    }

    public String getR_name() {
        return r_name;
    }

    public void setR_name(String r_name) {
        this.r_name = r_name;
    }

    public Boolean getR_delete() {
        return r_delete;
    }

    public void setR_delete(Boolean r_delete) {
        this.r_delete = r_delete;
    }

    public int getF_board_idx() {
        return f_board_idx;
    }

    public void setF_board_idx(int f_board_idx) {
        this.f_board_idx = f_board_idx;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "r_idx=" + r_idx +
                ", r_parent_idx=" + r_parent_idx +
                ", r_child_idx=" + r_child_idx +
                ", r_level_idx=" + r_level_idx +
                ", r_content='" + r_content + '\'' +
                ", r_user='" + r_user + '\'' +
                ", r_date=" + r_date +
                '}';
    }
}

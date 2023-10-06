package com.jica.newpts.beans;

import com.google.firebase.Timestamp;

public class Board {
    private String f_subject;
    private String f_user;
    private String f_context;
    private Timestamp f_date;
    private int f_thumbs_up;
    private String f_hashtag;
    private int f_board_info_idx;
    private int hits;
    private int f_board_idx;
    private int f_count_comment;
    private String f_photo;
    private String f_down_url;
    private boolean f_del;
    private Timestamp f_del_date;
    private Timestamp f_modify_date;
    private String f_writer_name;
    private String f_wirter_photo;


    public Board() {
    }

    public Board(String f_subject, String f_user, String f_context, Timestamp f_date, int f_thumbs_up, String f_hashtag, int f_board_info_idx, int hits, int f_board_idx, int f_count_comment, String f_photo) {
        this.f_subject = f_subject;
        this.f_user = f_user;
        this.f_context = f_context;
        this.f_date = f_date;
        this.f_thumbs_up = f_thumbs_up;
        this.f_hashtag = f_hashtag;
        this.f_board_info_idx = f_board_info_idx;
        this.hits = hits;
        this.f_board_idx = f_board_idx;
        this.f_count_comment = f_count_comment;
        this.f_photo = f_photo;
    }

    public String getF_down_url() {
        return f_down_url;
    }

    public void setF_down_url(String f_down_url) {
        this.f_down_url = f_down_url;
    }

    public String getF_subject() {
        return f_subject;
    }

    public void setF_subject(String f_subject) {
        this.f_subject = f_subject;
    }

    public String getF_user() {
        return f_user;
    }

    public void setF_user(String f_user) {
        this.f_user = f_user;
    }

    public String getF_context() {
        return f_context;
    }

    public void setF_context(String f_context) {
        this.f_context = f_context;
    }

    public Timestamp getF_date() {
        return f_date;
    }

    public void setF_date(Timestamp f_date) {
        this.f_date = f_date;
    }

    public int getF_thumbs_up() {
        return f_thumbs_up;
    }

    public void setF_thumbs_up(int f_thumbs_up) {
        this.f_thumbs_up = f_thumbs_up;
    }

    public String getF_hashtag() {
        return f_hashtag;
    }

    public void setF_hashtag(String f_hashtag) {
        this.f_hashtag = f_hashtag;
    }

    public int getF_board_info_idx() {
        return f_board_info_idx;
    }

    public void setF_board_info_idx(int f_board_info_idx) {
        this.f_board_info_idx = f_board_info_idx;
    }

    public int getHits() {
        return hits;
    }

    public void setHits(int hits) {
        this.hits = hits;
    }

    public int getF_board_idx() {
        return f_board_idx;
    }

    public void setF_board_idx(int f_board_idx) {
        this.f_board_idx = f_board_idx;
    }

    public int getF_count_comment() {
        return f_count_comment;
    }

    public void setF_count_comment(int f_count_comment) {
        this.f_count_comment = f_count_comment;
    }

    public String getF_photo() {
        return f_photo;
    }

    public void setF_photo(String f_photo) {
        this.f_photo = f_photo;
    }

    public boolean isF_del() {
        return f_del;
    }

    public void setF_del(boolean f_del) {
        this.f_del = f_del;
    }

    public String getF_writer_name() {
        return f_writer_name;
    }

    public void setF_writer_name(String f_writer_name) {
        this.f_writer_name = f_writer_name;
    }

    public String getF_wirter_photo() {
        return f_wirter_photo;
    }

    public void setF_wirter_photo(String f_wirter_photo) {
        this.f_wirter_photo = f_wirter_photo;
    }

    public Timestamp getF_del_date() {
        return f_del_date;
    }

    public void setF_del_date(Timestamp f_del_date) {
        this.f_del_date = f_del_date;
    }

    public Timestamp getF_modify_date() {
        return f_modify_date;
    }

    public void setF_modify_date(Timestamp f_modify_date) {
        this.f_modify_date = f_modify_date;
    }

    @Override
    public String toString() {
        return "Board{" +
                "f_subject='" + f_subject + '\'' +
                ", f_user='" + f_user + '\'' +
                ", f_context='" + f_context + '\'' +
                ", f_date=" + f_date +
                ", f_thumbs_up=" + f_thumbs_up +
                ", f_hashtag='" + f_hashtag + '\'' +
                ", f_board_info_idx=" + f_board_info_idx +
                ", hits=" + hits +
                ", f_board_idx=" + f_board_idx +
                ", f_count_comment=" + f_count_comment +
                ", f_profile_photo='" + f_photo + '\'' +
                '}';
    }
}

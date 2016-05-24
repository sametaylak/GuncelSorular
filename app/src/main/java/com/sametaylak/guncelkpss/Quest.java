package com.sametaylak.guncelkpss;

import java.io.Serializable;

public class Quest implements Serializable{

    private static final long serialVersionUID = 1L;
    private int id;
    private String sender;
    private String quest;
    private int confirm;

    public Quest() {
        super();
    }

    public Quest(int mId, String mSender, String mQuest, int mConfirm) {
        super();
        this.id = mId;
        this.sender = mSender;
        this.quest = mQuest;
        this.confirm = mConfirm;
    }

    public int getId() {
        return this.id;
    }

    public String getSender() {
        return this.sender;
    }

    public String getQuest() {
        return this.quest;
    }

    public int getConfirm() {
        return this.confirm;
    }

}

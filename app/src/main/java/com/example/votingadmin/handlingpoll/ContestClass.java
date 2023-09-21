package com.example.votingadmin.handlingpoll;

import java.io.Serializable;

public class ContestClass implements Serializable {
    private String conId;
    private String conName;

    public String getConId() {
        return conId;
    }

    public void setConId(String conId) {
        this.conId = conId;
    }

    public String getConName() {
        return conName;
    }

    public void setConName(String conName) {
        this.conName = conName;
    }
}

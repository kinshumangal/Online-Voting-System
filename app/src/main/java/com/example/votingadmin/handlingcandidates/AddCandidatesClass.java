package com.example.votingadmin.handlingcandidates;

import java.io.Serializable;

public class AddCandidatesClass implements Serializable {

    private String aFullname;
    private String aAadhaar;


    public void setaAadhaar(String uAadhaar) {
        this.aAadhaar = uAadhaar;
    }

    public void setaFullname(String uFullname) {
        this.aFullname = uFullname;
    }


    public String getaAadhaar() {
        return aAadhaar;
    }


    public String getaFullname() {
        return aFullname;
    }
}



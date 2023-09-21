package com.example.votingadmin;


import java.io.Serializable;

public class AdminAddedData implements Serializable {
    private String aFullname;
    private String aEmail;
    private  long aMobile;
    private String aPassword;
    private String aAddress;
    private String aAadhaar;


    public void setaAadhaar(String uAadhaar) {
        this.aAadhaar = uAadhaar;
    }

    public void setaAddress(String uAddress) {
        this.aAddress = uAddress;
    }

    public void setaEmail(String uEmail) {
        this.aEmail = uEmail;
    }

    public void setaFullname(String uFullname) {
        this.aFullname = uFullname;
    }

    public void setaMobile(long uMobile) {
        this.aMobile = uMobile;
    }

    public void setaPassword(String uPassword) {
        this.aPassword = uPassword;
    }

    public String getaFullname() {
        return aFullname;
    }

    public String getaEmail() {
        return aEmail;
    }

    public String getaAddress() {
        return aAddress;
    }

    public String getaPassword() {
        return aPassword;
    }

    public String getaAadhaar() {
        return aAadhaar;
    }

    public long getaMobile() {
        return aMobile;
    }

}


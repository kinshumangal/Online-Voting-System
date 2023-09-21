package com.example.votingadmin.handlingusers;

import java.io.Serializable;

public class AddUserData implements Serializable {
        private String auFullname;
        private String auAadhaar;


        public void setauFullname(String uFullname) {
            this.auFullname = uFullname;
        }


        public String getAuAadhaar() {
            return auAadhaar;
        }


        public String getAuFullname() {
            return auFullname;
        }

    public void setauAadhaar(String auAadhaar) {
        this.auAadhaar = auAadhaar;
    }
}

package com.example.ispyproject;

public class Invitation {

    private String inviterID;
    private String inviteeUsername;
    private String inviterUsername;

    public Invitation(String inviterID, String inviteeUsername, String inviterUsername) {
        this.inviteeUsername = inviteeUsername;
        this.inviterID = inviterID;
        this.inviterUsername = inviterUsername;
    }
    public Invitation() {
    }

    public String getInviterUsername() {
        return inviterUsername;
    }

    public String getInviteeUsername() {
        return inviteeUsername;
    }

    public String getInviterID() {
        return inviterID;
    }
}

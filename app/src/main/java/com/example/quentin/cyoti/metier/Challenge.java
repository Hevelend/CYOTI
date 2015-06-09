package com.example.quentin.cyoti.metier;

import android.util.Log;

import com.parse.ParseUser;

import java.util.Date;

/**
 * Created by Vincent on 15/05/2015.
 */
public class Challenge {
    private String challengeID;
    private String description;
    private Date createdDate;
    private Date finishedDate;
    private Friend friendChallenged;
    private Friend friendChallenger;

    private String username;
    private boolean isCurrentUserChallenged;
    private boolean isCurrentUserChallenger;

    public Challenge() {
//        this.description = "Sing in public";
//        this.createdDate = new Date(1432001045);
//        this.finishedDate = null;
//        this.friendChallenged = new Friend("John", "Smith","JS");
//        this.friendChallenger = new Friend("Jane", "Doe", "JD");
    }

    public Challenge(String id, String description, Date creation, Date finish, Friend challenged, Friend challenger, ParseUser user) {
        this.challengeID = id;
        this.description = description;
        this.createdDate = creation;
        this.finishedDate = finish;
        this.friendChallenged = challenged;
        this.friendChallenger = challenger;
        this.username = user.getUsername();
        this.isCurrentUserChallenged = friendChallenged != null && (friendChallenged.getFirstName().equals(username));
        this.isCurrentUserChallenger = friendChallenger != null && (friendChallenger.getFirstName().equals(username));
    }

    public String getChallengeID() {
        return challengeID;
    }

    public void setChallengeID(String id) {
        this.challengeID = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getFinishedDate() {
        return finishedDate;
    }

    public void setFinishedDate(Date finishedDate) {
        this.finishedDate = finishedDate;
    }

    public Friend getUserChallenged() {
        return friendChallenged;
    }

    public void setUserChallenged(Friend currentUserChallenged) {
        this.friendChallenged = currentUserChallenged;
        this.isCurrentUserChallenged = friendChallenged != null && (friendChallenged.getFirstName().equals(username));
    }

    public Friend getUserChallenger() {
        return friendChallenger;
    }

    public void setUserChallenger(Friend currentUserChallenger) {
        this.friendChallenger = currentUserChallenger;
        this.isCurrentUserChallenger = friendChallenger != null && (friendChallenger.getFirstName().equals(username));
    }

    public boolean isCurrentUserChallenged() {
        return isCurrentUserChallenged;
    }

    public boolean isCurrentUserChallenger() {
        return isCurrentUserChallenger;
    }
}
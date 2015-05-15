package com.example.quentin.cyoti.metier;

import java.util.Date;

/**
 * Created by Vincent on 15/05/2015.
 */
public class Challenge {
    private String description;
    private Date createdDate;
    private Date finishedDate;
    private Friend friendChallenged;
    private Friend friendChallenger;

    public Challenge() {}

    public Challenge(String description, Date creation, Date finish, Friend challenged, Friend challenger) {
        this.description = description;
        this.createdDate = creation;
        this.finishedDate = finish;
        this.friendChallenged = challenged;
        this.friendChallenger = challenger;
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

    public void setUserChallenged(Friend userChallenged) {
        this.friendChallenged = userChallenged;
    }

    public Friend getUserChallenger() {
        return friendChallenger;
    }

    public void setUserChallenger(Friend userChallenger) {
        this.friendChallenger = userChallenger;
    }
}
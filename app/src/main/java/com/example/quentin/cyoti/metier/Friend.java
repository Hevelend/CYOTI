package com.example.quentin.cyoti.metier;

/**
 * Created by Vincent on 13/05/2015.
 */
public class Friend {
    private String firstName;
    private String lastName;
    private String nickName;
    private String imgPath;

    public Friend() {
        firstName = "Mon";
        lastName = "Ami";
        nickName = "Nick123";
        imgPath = "mipmap/ic_app";
    }

    public Friend(String first, String last, String nick, String path) {
        firstName = first;
        lastName = last;
        nickName = nick;
        imgPath = path;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }
}

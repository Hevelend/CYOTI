package com.example.quentin.cyoti.metier;

import java.util.ArrayList;

/**
 * Created by Vincent on 18/05/2015.
 */
public class User {
    private String firstName;
    private String nickName;
    private String imgPath;
    private ArrayList<Friend> friends = new ArrayList<Friend>();


    public User() {
        firstName = "Mon";
        nickName = "Nick123";
        imgPath = "mipmap/ic_app";
    }

    public User(String first, String last, String nick, String path) {
        firstName = first;
        nickName = nick;
        imgPath = path;
    }

    public User(String first, String last, String nick) {
        firstName = first;
        nickName = nick;
    }

    public User(String name) {
        this.firstName = name;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
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

    public ArrayList<Friend> getFriends() {
        return friends;
    }

    public void addFriend(String friend) {
        this.friends.add(new Friend(friend));
    }
}

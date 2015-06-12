package com.example.quentin.cyoti.metier;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.quentin.cyoti.R;

/**
 * Created by Vincent on 13/05/2015.
 */
public class Friend {
    private String firstName;
    private String lastName;
    private String nickName;
    private Bitmap imgFriend;
    private String userID;
    private boolean isSelected = false;

    // TODO ne plus utiliser ce constructeur
    public Friend() {
        firstName = "Mon";
        lastName = "Ami";
        nickName = "Nick123";
    }

    // TODO ne plus utiliser ce constructeur
    public Friend(String first, String last, String nick) {
        firstName = first;
        lastName = last;
        nickName = nick;
    }

    public Friend(Context context, String name, String id, Bitmap icon) {
        this.firstName = name;
        this.userID = id;
        this.imgFriend = (icon != null) ? icon : BitmapFactory.decodeResource(context.getResources(), R.drawable.default_avatar);
    }

    // TODO ne plus utiliser ce constructeur
    public Friend(String name) {
        this.firstName = name;
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

    public Bitmap getImgBmp() {
        return imgFriend;
    }

    public void setImgBmp(Bitmap imgFriend) {
        this.imgFriend = imgFriend;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public boolean isSelected() {
        return this.isSelected;
    }

    public void setSelected(boolean select) {
        this.isSelected = select;
    }

    @Override
    public String toString() {
        return firstName + " " + userID;
    }
}

package com.example.quentin.cyoti.utilities;

import android.widget.CheckBox;
import android.widget.TextView;

/**
 * Created by Vincent on 20/05/2015.
 */
public class FriendViewHolder {
    private static CheckBox checkBox;
    private static TextView textView;

    public FriendViewHolder() {}

    public FriendViewHolder(CheckBox cb, TextView text) {
        this.checkBox = cb;
        this.textView = text;
    }

    public static CheckBox getCheckBox() {
        return checkBox;
    }

    public static void setCheckBox(CheckBox checkBox) {
        FriendViewHolder.checkBox = checkBox;
    }

    public static TextView getTextView() {
        return textView;
    }

    public static void setTextView(TextView textView) {
        FriendViewHolder.textView = textView;
    }
}

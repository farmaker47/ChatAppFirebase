package com.google.firebase.udacity.friendlychat;

/**
 * Created by farmaker1 on 10/06/2017.
 */

public class UserNameMessage {

    private String mText;

    public UserNameMessage(){

    }

    public UserNameMessage (String text){
        mText=text;
    }

    public String getText() {
        return mText;
    }


}

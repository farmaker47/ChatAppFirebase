package com.google.firebase.udacity.friendlychat;

/**
 * Created by farmaker1 on 10/06/2017.
 */

public class UserNameMessage {

    private String nameToName;

    public UserNameMessage(){
    }

    public UserNameMessage (String nameToName){
        this.nameToName=nameToName;
    }

    public String getnameToName() {
        return nameToName;
    }

    public void setNameToName (String nameToName){
        this.nameToName = nameToName;
    }


}

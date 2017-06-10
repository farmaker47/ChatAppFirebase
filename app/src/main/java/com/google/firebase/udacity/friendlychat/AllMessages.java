package com.google.firebase.udacity.friendlychat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class AllMessages extends AppCompatActivity {

    private AllMessagesAdapter mMessageAdapter;
    private ListView mMessageListView;

    private String mUsername;

    private FirebaseDatabase mFirebaseDatabase;

    //A class that reference to spesific part of database
    private DatabaseReference mMessagesDatabaseReference;

    //Child event listener to understand that has new messages
    private ChildEventListener mChildEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_messages);

        mMessageListView = (ListView)findViewById(R.id.allMessageListView);

        Intent intent = getIntent();
        String str = intent.getStringExtra("myUsername");
        Log.e("AllMessages",str);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mMessagesDatabaseReference = mFirebaseDatabase.getReference().child(str);
        /*mMessagesDatabaseReference = mFirebaseDatabase.getReference();*/
        Log.e("referenAllMessages",mMessagesDatabaseReference.toString());

        // Initialize message ListView and its adapter
        List<UserNameMessage> userNameMessages = new ArrayList<>();
        mMessageAdapter = new AllMessagesAdapter(this, R.layout.item_all_message, userNameMessages);
        mMessageListView.setAdapter(mMessageAdapter);

        /*onInitialize(str + " -> " + str);*/
        attachDatabaseReadListener();

    }

    private void onInitialize(String username) {
        /*mUsername = username;
        //after log in then we take the name and we create a new node for the messages based on the username
        mMessagesDatabaseReference = mFirebaseDatabase.getReference().child(mUsername + " -> " + mUsername);
        Log.e("referenCE",mMessagesDatabaseReference.toString());*/
        attachDatabaseReadListener();
    }

    private void attachDatabaseReadListener() {
        if (mChildEventListener == null) {
            // Child event listener
            mChildEventListener = new ChildEventListener() {

                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    UserNameMessage usersMessage = dataSnapshot.getValue(UserNameMessage.class);
                    mMessageAdapter.add(usersMessage);
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };

            mMessagesDatabaseReference.addChildEventListener(mChildEventListener);
        }

    }


}

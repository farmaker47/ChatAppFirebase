package com.google.firebase.udacity.friendlychat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class StringUsernameMessages extends AppCompatActivity {


    private StringUsernameMessagesAdapter mMessageAdapter;
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
        setContentView(R.layout.activity_string_username_messages);

        mMessageListView = (ListView)findViewById(R.id.allMessageListView);

        Intent intent = getIntent();
        String strUsername = intent.getStringExtra("myUsername");
        String strPersonal = intent.getStringExtra("myPersonalMessages");
        Log.e("AllMessages",strUsername);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mMessagesDatabaseReference = mFirebaseDatabase.getReference().child(strUsername);

        // Initialize message ListView and its adapter
        List<String> userNameMessages = new ArrayList<>();
        mMessageAdapter = new StringUsernameMessagesAdapter(this, R.layout.item_all_message, userNameMessages);
        mMessageListView.setAdapter(mMessageAdapter);

        attachDatabaseReadListener();
        mMessageListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                TextView textView = (TextView) view.findViewById(R.id.allMessageTextView);
                String text = textView.getText().toString();

                Intent a = new Intent(StringUsernameMessages.this, AfterPickingMessages.class);
                a.putExtra("myUsername",mUsername);
                a.putExtra("myPersonalMessages",text);
                startActivity(a);
            }
        });
    }

    private void attachDatabaseReadListener() {
        if (mChildEventListener == null) {
            // Child event listener
            mChildEventListener = new ChildEventListener() {

                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                   /* UserNameMessage usersMessage = dataSnapshot.getValue(UserNameMessage.class);
                    String datasnapshoti = dataSnapshot.getKey();
                    Log.e("datasnapsotAllMessages",datasnapshoti);
                    mMessageAdapter.add(usersMessage);*/



                    String datasnapshoti = dataSnapshot.getKey();
                    Log.e("datasnapsotAllMessages",datasnapshoti);
                    mMessageAdapter.add(datasnapshoti);
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

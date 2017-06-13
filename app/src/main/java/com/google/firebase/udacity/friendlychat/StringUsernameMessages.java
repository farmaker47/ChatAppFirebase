package com.google.firebase.udacity.friendlychat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class StringUsernameMessages extends AppCompatActivity {


    private StringUsernameMessagesAdapter mMessageAdapter;
    private ListView mMessageListView;

    private String mUsername;
    private String string2;


    private FirebaseDatabase mFirebaseDatabase;

    //A class that reference to spesific part of database
    private DatabaseReference mMessagesDatabaseReference;
    private DatabaseReference mMessagesDatabaseReference2;
    private DataSnapshot dSnapshot;

    //Child event listener to understand that has new messages
    private ChildEventListener mChildEventListener;
    private ChildEventListener mChildEventListener2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_string_username_messages);

        string2 = "222";

        mMessageListView = (ListView) findViewById(R.id.allMessageListView);

        Intent intent = getIntent();
        final String strUsername = intent.getStringExtra("myUsername");
        String strPersonal = intent.getStringExtra("myPersonalMessages");
        final String secondName = intent.getStringExtra("secondName");
        Log.e("AllMessages", strUsername+secondName);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mMessagesDatabaseReference = mFirebaseDatabase.getReference().child(strUsername);


        // Initialize message ListView and its adapter
        List<String> userNameMessages = new ArrayList<>();
        mMessageAdapter = new StringUsernameMessagesAdapter(this, R.layout.item_all_message, userNameMessages);
        mMessageListView.setAdapter(mMessageAdapter);

        attachDatabaseReadListener();

        //adding listener to listview
        mMessageListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                TextView textView = (TextView) view.findViewById(R.id.allMessageTextView);
                String text = textView.getText().toString();

                mMessagesDatabaseReference2 = mFirebaseDatabase.getReference().child(strUsername).child(text);


                /////
                ////////
               if (mChildEventListener2 == null) {
                    // Child event listener
                    mChildEventListener2 = new ChildEventListener() {

                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                   /* UserNameMessage usersMessage = dataSnapshot.getValue(UserNameMessage.class);
                    String datasnapshoti = dataSnapshot.getKey();
                    Log.e("datasnapsotAllMessages",datasnapshoti);
                    mMessageAdapter.add(usersMessage);*/


                            string2 = dataSnapshot.getKey();
                            Log.e("454545", string2);

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

                    mMessagesDatabaseReference2.addChildEventListener(mChildEventListener2);


                }

                ////////
                ///////
                //////

                Intent a = new Intent(StringUsernameMessages.this, AfterPickingMessages.class);
                a.putExtra("4444", string2);
                a.putExtra("myUsername", strUsername);
                a.putExtra("myPersonalMessages", text);
                a.putExtra("secondName", secondName);
                startActivity(a);


                mChildEventListener2 =null;
                /*mMessagesDatabaseReference2.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        string2 = dataSnapshot.getKey();
                        Log.e("454545", string2);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });*/

               /* mMessagesDatabaseReference2.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        string2 = dataSnapshot.getKey();
                        Log.e("454545", string2);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });*/





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
                    Log.e("datasnapsotAllMessages", datasnapshoti);
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

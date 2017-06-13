package com.google.firebase.udacity.friendlychat;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

public class AfterPickingMessages extends AppCompatActivity {

    private static final int RC_PHOTO_PICKER = 2;
    public static final int DEFAULT_MSG_LENGTH_LIMIT = 1000;

    private String strUsername;
    private String strPersonal;
    private String strSecondName;
    private String strSecondNameAfter;
    private String str4444;

    private ListView mMessageListView;
    private MessageAdapter mMessageAdapter;
    private ProgressBar mProgressBar;
    private ImageButton mPhotoPickerButton;
    private EditText mMessageEditText;
    private Button mSendButton,mButtonToMessages;



    private FirebaseDatabase mFirebaseDatabase;

    //A class that reference to spesific part of database
    private DatabaseReference mMessagesDatabaseReference;
    private DatabaseReference mMessagesDatabaseReference2;


    //Child event listener to understand that has new messages
    private ChildEventListener mChildEventListener;

    //instance of firebase storage
    private FirebaseStorage mFirebaseStorage;
    private StorageReference mChatPhotosStorageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        strUsername = intent.getStringExtra("myUsername");
        strPersonal = intent.getStringExtra("myPersonalMessages");
        strSecondName = intent.getStringExtra("secondName");
        str4444 = intent.getStringExtra("4444");
        Log.e("AllMessages", strUsername+"--"+strPersonal+"--"+str4444+"333"+strSecondName);

        if(strUsername.equals(strSecondName)){
            strSecondNameAfter=str4444;
        }

        Log.e("AfterChange", strUsername+"--"+strPersonal+"--"+str4444+"333"+strSecondNameAfter);

        //Instantiating the database..access point of the database reference
        mFirebaseDatabase = FirebaseDatabase.getInstance();

        //initializing the storage
        mFirebaseStorage = FirebaseStorage.getInstance();

        mMessagesDatabaseReference = mFirebaseDatabase.getReference().child(strUsername);
        mMessagesDatabaseReference2 = mFirebaseDatabase.getReference().child(strSecondName);

        //getting a reference of the messages node

        mChatPhotosStorageReference = mFirebaseStorage.getReference().child("chat_photos");

        // Initialize references to views
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mMessageListView = (ListView) findViewById(R.id.messageListView);
        mPhotoPickerButton = (ImageButton) findViewById(R.id.photoPickerButton);
        mMessageEditText = (EditText) findViewById(R.id.messageEditText);
        mSendButton = (Button) findViewById(R.id.sendButton);
        mButtonToMessages = (Button) findViewById(R.id.buttonToMessages);

        // Initialize message ListView and its adapter
        List<FriendlyMessage> friendlyMessages = new ArrayList<>();
        mMessageAdapter = new MessageAdapter(this, R.layout.item_message, friendlyMessages);
        mMessageListView.setAdapter(mMessageAdapter);

        mButtonToMessages.setVisibility(View.GONE);


        // Initialize progress bar
        mProgressBar.setVisibility(ProgressBar.INVISIBLE);

        // ImagePickerButton shows an image picker to upload a image for a message
        mPhotoPickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(intent, "Complete action using"), RC_PHOTO_PICKER);
            }
        });

        // Enable Send button when there's text to send
        mMessageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    mSendButton.setEnabled(true);
                } else {
                    mSendButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        mMessageEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(DEFAULT_MSG_LENGTH_LIMIT)});

        // Send button sends a message and clears the EditText
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Creating a message
                FriendlyMessage friendlyMessage = new FriendlyMessage(mMessageEditText.getText().toString(), strUsername, strPersonal, null);
                //The push method is exactly what you want to be using in this case because you need a new id generated for each message
                mMessagesDatabaseReference.child(strUsername + " -> " + strSecondName).child(strUsername).push().setValue(friendlyMessage);


                FriendlyMessage friendlyMessage2 = new FriendlyMessage(mMessageEditText.getText().toString(), strUsername, strUsername + " -> " + strSecondName, null);
                //The push method is exactly what you want to be using in this case because you need a new id generated for each message
                mMessagesDatabaseReference2.child(strUsername + " -> " + strSecondName).child(strUsername).push().setValue(friendlyMessage2);



                // Clear input box
                mMessageEditText.setText("");

            }
        });

        attachDatabaseReadListener();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_PHOTO_PICKER && resultCode == RESULT_OK) {
            Uri selectedImageUri = data.getData();
            //to check in the log
            String image = selectedImageUri.toString();
            Log.e("PhotoUri:", image);

            Toast.makeText(AfterPickingMessages.this, "imageloaded", Toast.LENGTH_SHORT).show();

            //reference of the last segment of the uri
            StorageReference photoRef = mChatPhotosStorageReference.child(selectedImageUri.getLastPathSegment());

            //upload with putfile method
            photoRef.putFile(selectedImageUri).addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    FriendlyMessage friendlyMessage = new FriendlyMessage(null, strUsername, strPersonal, downloadUrl.toString());
                    mMessagesDatabaseReference.child(strUsername + " -> " + strSecondName).push().setValue(friendlyMessage);

                    FriendlyMessage friendlyMessage2 = new FriendlyMessage(null, strUsername, strUsername + " -> " + strSecondName, downloadUrl.toString());
                    //The push method is exactly what you want to be using in this case because you need a new id generated for each message
                    mMessagesDatabaseReference2.child(strUsername + " -> " + strSecondName).push().setValue(friendlyMessage2);
                }
            });
        }


    }

    private void attachDatabaseReadListener() {
        if (mChildEventListener == null) {
            // Child event listener
            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    FriendlyMessage friendlyMessage = dataSnapshot.getValue(FriendlyMessage.class);
                    String datasnapshoti = dataSnapshot.getKey();
                    Log.e("datasnapsot",datasnapshoti);
                    mMessageAdapter.add(friendlyMessage);
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

            mMessagesDatabaseReference.child(strUsername + " -> " + strSecondName).child(strUsername).addChildEventListener(mChildEventListener);
        }

    }
}

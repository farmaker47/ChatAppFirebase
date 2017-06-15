package com.google.firebase.udacity.friendlychat;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AfterPickingMessages extends AppCompatActivity {

    private Context mContext;

    private static final int RC_PHOTO_PICKER = 2;
    public static final int DEFAULT_MSG_LENGTH_LIMIT = 1000;

    private String strUsername = null;
    private String strPersonal;
    private String strSecondName;
    private String strSecondNameAfter;
    private String str4444;
    private boolean isReaded;

    private ListView mMessageListView;
    private MessageAdapter mMessageAdapter;
    private ProgressBar mProgressBar;
    private ImageButton mPhotoPickerButton;
    private EditText mMessageEditText;
    private Button mSendButton, mButtonToMessages;


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

        isReaded = false;

        /*Intent intent = getIntent();
        strUsername = intent.getStringExtra("myUsername");
        strPersonal = intent.getStringExtra("myPersonalMessages");
        strSecondName = intent.getStringExtra("secondName");
        str4444 = intent.getStringExtra("4444");*/

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);;
        strUsername = sharedPref.getString("myUsername",null);
        strPersonal = sharedPref.getString("myPersonalMessages",null);
        strSecondName = sharedPref.getString("secondName",null);
        str4444 = sharedPref.getString("4444",null);

        Log.e("AllMessages", strUsername + "--" + strPersonal + "--" + str4444 + "--333--" + strSecondName);

        if (strUsername.equals(strSecondName)) {
            strSecondName = str4444;
        }

        Log.e("AfterChange", strUsername + "--" + strPersonal + "--" + str4444 + "333" + strSecondNameAfter);

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
                FriendlyMessage friendlyMessage = new FriendlyMessage(mMessageEditText.getText().toString(), strUsername, strPersonal, null, getTheDateTime(),isReaded);
                //The push method is exactly what you want to be using in this case because you need a new id generated for each message
                mMessagesDatabaseReference.child(strPersonal).child(str4444).push().setValue(friendlyMessage);


                FriendlyMessage friendlyMessage2 = new FriendlyMessage(mMessageEditText.getText().toString(), strUsername, strPersonal, null, getTheDateTime(),isReaded);
                //The push method is exactly what you want to be using in this case because you need a new id generated for each message
                mMessagesDatabaseReference2.child(strPersonal).child(str4444).push().setValue(friendlyMessage2);


                // Clear input box
                mMessageEditText.setText("");

                InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                in.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
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

                    FriendlyMessage friendlyMessage = new FriendlyMessage(null, strUsername, strPersonal, downloadUrl.toString(), getTheDateTime(),isReaded);

                    mMessagesDatabaseReference.child(strPersonal).child(str4444).push().setValue(friendlyMessage);


                    FriendlyMessage friendlyMessage2 = new FriendlyMessage(null, strUsername, strPersonal, downloadUrl.toString(), getTheDateTime(),isReaded);
                    //The push method is exactly what you want to be using in this case because you need a new id generated for each message
                    mMessagesDatabaseReference2.child(strPersonal).child(str4444).push().setValue(friendlyMessage2);


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

                    //to use it for notifications
                    String valueOfRead = dataSnapshot.child("/isReaded").getKey();
                    /////
                    String datasnapshoti = dataSnapshot.getKey();
                    Log.e("datasnapsot", datasnapshoti);
                    ////
                    friendlyMessage.setIsReaded(true);
                    mMessageAdapter.add(friendlyMessage);


                    /////////
                    ////////we give strPersonal new value so the notification transfer us to the correct messages
                    SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(AfterPickingMessages.this);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("myPersonalMessages", strPersonal);
                    editor.commit();


                    if (!friendlyMessage.getName().equals(strUsername) && valueOfRead.equals("false")) {

                        int notifyID = 1;

                        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(AfterPickingMessages.this)
                                .setSmallIcon(R.drawable.ic_launcher)
                                .setContentTitle("Message from:\n " + strSecondName)
                                .setContentText(friendlyMessage.getText())
                                .setOnlyAlertOnce(true)
                                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
                        mBuilder.setAutoCancel(true);
                        mBuilder.setLocalOnly(false);

                        Intent resultIntent = new Intent(AfterPickingMessages.this, AfterPickingMessages.class);


                        resultIntent.setAction("android.intent.action.MAIN");
                        resultIntent.addCategory("android.intent.category.LAUNCHER");

                        PendingIntent resultPendingIntent = PendingIntent.getActivity(AfterPickingMessages.this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                        //building the notification
                        mBuilder.setContentIntent(resultPendingIntent);






                        /*TaskStackBuilder stackBuilder = TaskStackBuilder.create(AfterPickingMessages.this);
                        // Adds the back stack for the Intent (but not the Intent itself)
                        stackBuilder.addParentStack(AfterPickingMessages.class);
                        // Adds the Intent that starts the Activity to the top of the stack
                        stackBuilder.addNextIntent(resultIntent);
                        PendingIntent resultPendingIntent =
                                stackBuilder.getPendingIntent(
                                        0,
                                        PendingIntent.FLAG_UPDATE_CURRENT
                                );
                        mBuilder.setContentIntent(resultPendingIntent);*/


                        mNotificationManager.notify(notifyID, mBuilder.build());
                    }
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

            mMessagesDatabaseReference.child(strPersonal).child(str4444).addChildEventListener(mChildEventListener);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        //solution if you want to immediatelly go back to SimpleUsernameMessages
        /*Intent backIntent = new Intent(AfterPickingMessages.this,StringUsernameMessages.class);
        backIntent.putExtra("myUsername", strUsername);
        backIntent.putExtra("secondName", strSecondName);
        startActivity(backIntent);*/

    }

    private String getTheDateTime() {
        DateFormat df = new SimpleDateFormat("EEE, d MMM, HH:mm");
        String date = df.format(Calendar.getInstance().getTime());
        return date;
    }
}

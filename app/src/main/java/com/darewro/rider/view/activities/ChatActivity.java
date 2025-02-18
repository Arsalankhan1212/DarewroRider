package com.darewro.rider.view.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.darewro.rider.BuildConfig;
import com.darewro.rider.data.api.requests.VolleyMultipartRequest;
import com.darewro.rider.data.api.requests.VolleySingleton;
import com.darewro.rider.view.xmpp.XmppService;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.darewro.rider.App;
import com.darewro.rider.R;
import com.darewro.rider.data.api.ApiCalls;
import com.darewro.rider.data.api.models.FileUploadResponseModel;
import com.darewro.rider.data.db.model.ChatMessage;
import com.darewro.rider.presenter.ResponseListenerFile;
import com.darewro.rider.view.adapters.CustomChatIncomingAdapterViewHolder;
import com.darewro.rider.view.adapters.CustomChatOutcomingAdapterViewHolder;
import com.darewro.rider.view.chatLibViewModel.Author;
import com.darewro.rider.view.chatLibViewModel.IMessageModel;
import com.darewro.rider.view.utils.AlertDialogUtils;
import com.darewro.rider.view.utils.AppUtils;
import com.darewro.rider.view.utils.AudioRecordingUtils;
import com.darewro.rider.view.utils.DbUtils;
import com.darewro.rider.view.utils.SharedPreferenceHelper;
import com.darewro.rider.view.xmpp.XMPPUser;
import com.darewro.rider.view.xmpp.XmppClient;
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.messages.MessageInput;
import com.stfalcon.chatkit.messages.MessagesList;
import com.stfalcon.chatkit.messages.MessagesListAdapter;
import com.stfalcon.chatkit.utils.DateFormatter;
import com.stfalcon.imageviewer.StfalconImageViewer;

import org.jivesoftware.smackx.chatstates.ChatState;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class ChatActivity extends BaseChatActivity implements ResponseListenerFile {

    private static final String TAG = ChatActivity.class.getName();
    private static final int MY_PERMISSIONS_RECORD_AUDIO = 1;
    private static final int REQUEST_CODE_CAMERA = 2;
    private static final int REQUEST_IMAGE_CAPTURE = 3;
    private static final int PICK_IMAGE_GALLERY = 4;
    String imageFilePath;
    private String contactJid, contactName;
    //    private ChatView mChatView;
    private MessagesList messagesList;
    private MessageInput inputView;
    private TextView userStatus,recordingDuration;
    private MessagesListAdapter adapter;
    private ArrayList<Object> iMessages = new ArrayList<>();
    private String orderId;
    private List<String> images = new ArrayList<>();
    private ImageButton recording;

    private boolean isPaused = false;

    public String getOrderId() {
        return orderId;
    }

    public String getContactJid() {
        return contactJid;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        XmppClient.chatActivity = this;

        handleIntent();
        initializeViews();
        setListeners();

        loadData();

    }

    @Override
    public void initializeViews() {

        ((TextView) findViewById(R.id.title)).setText(contactName);


        userStatus = findViewById(R.id.composing);

        recording = findViewById(R.id.recording);
        recordingDuration = findViewById(R.id.recording_duration);
        inputView = findViewById(R.id.input);
        messagesList = findViewById(R.id.messagesList);

        MessagesListAdapter.HoldersConfig holdersConfig = new MessagesListAdapter.HoldersConfig();
        holdersConfig.setIncomingHolder(CustomChatIncomingAdapterViewHolder.class);
        holdersConfig.setIncomingLayout(R.layout.item_incoming_message);
        holdersConfig.setOutcomingHolder(CustomChatOutcomingAdapterViewHolder.class);
        holdersConfig.setOutcomingLayout(R.layout.item_outcoming_message);

        final ImageLoader imageLoader = new ImageLoader() {
            @Override
            public void loadImage(ImageView imageView, @Nullable final String url, @Nullable Object payload) {
                AppUtils.loadPicture(ChatActivity.this, imageView, url);

                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        com.stfalcon.imageviewer.loader.ImageLoader iml = new com.stfalcon.imageviewer.loader.ImageLoader() {
                            @Override
                            public void loadImage(ImageView imageView, Object image) {
                                AppUtils.loadPicture(ChatActivity.this, imageView, (String) image);
                            }
                        };
                        int index = 0;
                        Log.i("urlindex",url);
                        Log.i("urlindex",index+"");

                        for (int i = 0; i < images.size(); i++) {
                            if (url != null && images.get(i).equalsIgnoreCase(url)) {
                                index = i;
                            }
                        }
                        new StfalconImageViewer.Builder(ChatActivity.this, images, iml).withStartPosition(index).show();
                    }
                });
            }

        };

        adapter = new MessagesListAdapter<>(SharedPreferenceHelper.getString(XMPPUser.XMPP_ID, ChatActivity.this), holdersConfig, imageLoader);
        adapter.setDateHeadersFormatter(new DateFormatter.Formatter() {
            @Override
            public String format(Date date) {
                String dateString = AppUtils.getStringFromDate(date, AppUtils.FORMAT14);

                String formattedDate = AppUtils.getDay(dateString);
                if (formattedDate.equalsIgnoreCase("today")) {
                    return formattedDate;
                }

                formattedDate += "\n" + AppUtils.getDate(dateString);
                return formattedDate;
            }

/*
            @Override
            public String format(Date date) {
                if (DateFormatter.isToday(date)) {
                    return DateFormatter.format(date, DateFormatter.Template.TIME);
                } else if (DateFormatter.isYesterday(date)) {
                    return getString(R.string.date_header_yesterday);
                } else {
                    return DateFormatter.format(date, DateFormatter.Template.STRING_DAY_MONTH_YEAR);
                }
            }
*/
        });
        messagesList.setAdapter(adapter);
    }

    @Override
    public void setListeners() {

        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        inputView.setTypingListener(new MessageInput.TypingListener() {
            @Override
            public void onStartTyping() {
                if (XmppClient.getInstance() != null && XmppClient.getInstance().isConnected()) {
                    XmppClient.getInstance().setCurrentChatState(ChatState.composing, contactJid, orderId);
                } else {
//                    Toast.makeText(getApplicationContext(),"Client not connected to server ,Message not sent!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onStopTyping() {
                if (XmppClient.getInstance() != null && XmppClient.getInstance().isConnected()) {
                    XmppClient.getInstance().setCurrentChatState(ChatState.paused, contactJid, orderId);
                } else {
//                    Toast.makeText(getApplicationContext(),"Client not connected to server ,Message not sent!", Toast.LENGTH_LONG).show();
                }

            }
        });

        inputView.setInputListener(new MessageInput.InputListener() {
            @Override
            public boolean onSubmit(CharSequence input) {

                String timestamp = String.valueOf(AppUtils.getCurrentTimeStamp());

                ChatMessage chatMessage = new ChatMessage(AppUtils.generateNewMessageId(), input.toString(), orderId, contactJid, timestamp);

                chatMessage.setType(ChatMessage.Type.SENT);
                chatMessage.setMediaType(ChatMessage.MediaType.TEXT);
                chatMessage.setDeliveryStatus(ChatMessage.DeliveryStatus.UNSENT);

                chatMessage.save();

                IMessageModel iMessage = new IMessageModel();
                iMessage.setId(chatMessage.getMessageId());
                iMessage.setCreatedAt(AppUtils.getDateFromString(chatMessage.getTimestamp(), AppUtils.FORMAT14));
                iMessage.setText(chatMessage.getBody());
                iMessage.setChatMessage(chatMessage);
                Author author = new Author();
                author.setId(SharedPreferenceHelper.getString(XMPPUser.XMPP_ID, ChatActivity.this));
                author.setName(SharedPreferenceHelper.getString(XMPPUser.XMPP_ID, ChatActivity.this));
                iMessage.setAuthor(author);
                adapter.addToStart(iMessage, true);

                if (XmppClient.getInstance() != null && XmppClient.getInstance().isConnected()) {
                    XmppClient.getInstance().sendMessage(input.toString(), contactJid, chatMessage.getMessageId(), orderId, chatMessage.getMediaType());
                } else {
                    Toast.makeText(getApplicationContext(), "Client not connected to server ,Message not sent!", Toast.LENGTH_LONG).show();
                }


                return true;
            }
        });


        inputView.setAttachmentsListener(new MessageInput.AttachmentsListener() {
            @Override
            public void onAddAttachments() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkCameraPermission()) {
                        openCameraIntent();
                    } else {
                        ActivityCompat.requestPermissions(ChatActivity.this, new String[]{WRITE_EXTERNAL_STORAGE,READ_EXTERNAL_STORAGE, CAMERA}, REQUEST_CODE_CAMERA);
                    }
                } else {
                    openCameraIntent();
                }
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                    if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//                        requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, REQUEST_CODE_CAMERA);
//                    } else {
//                        openCameraIntent();
//                    }
//                } else {
//                    openCameraIntent();
//                }
            }
        });

        recording.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (checkPermission()) {

                            AudioRecordingUtils.getINSTANCE().setFileName(AppUtils.getFileName(AppUtils.AUDIO));
                            recordingDuration.setVisibility(View.VISIBLE);
                            AudioRecordingUtils.getINSTANCE().startRecording(recordingDuration);
                            recording.setBackground(getResources().getDrawable(R.drawable.mic_background_pressed));
                            return true;
                        } else {
                            requestPermission();

                            return false;
                        }
                    case MotionEvent.ACTION_UP:

                        if (checkPermission()) {

                            recording.setBackground(getResources().getDrawable(R.drawable.mic_background));
                            recordingDuration.setVisibility(View.GONE);
                            recordingDuration.setText("");

                            AudioRecordingUtils.getINSTANCE().stopRecording();

                            String timestamp = String.valueOf(AppUtils.getCurrentTimeStamp());

                            ChatMessage chatMessage = new ChatMessage(AppUtils.generateNewMessageId(), "", orderId, ChatMessage.MediaType.AUDIO, AudioRecordingUtils.getINSTANCE().getFileName(), contactJid, timestamp);

                            chatMessage.setType(ChatMessage.Type.SENT);
                            chatMessage.setMediaType(ChatMessage.MediaType.AUDIO);
                            chatMessage.setDeliveryStatus(ChatMessage.DeliveryStatus.UNSENT);

                            chatMessage.save();

                            IMessageModel iMessage = new IMessageModel();
                            iMessage.setId(chatMessage.getMessageId());
                            iMessage.setCreatedAt(AppUtils.getDateFromString(chatMessage.getTimestamp(), AppUtils.FORMAT14));
                            iMessage.setText(chatMessage.getBody());
                            iMessage.setChatMessage(chatMessage);
                            Author author = new Author();
                            author.setId(SharedPreferenceHelper.getString(XMPPUser.XMPP_ID, ChatActivity.this));
                            author.setName(SharedPreferenceHelper.getString(XMPPUser.XMPP_ID, ChatActivity.this));
                            iMessage.setAuthor(author);
                            adapter.addToStart(iMessage, true);

                            uploadfile(AudioRecordingUtils.getINSTANCE().getFileName(), chatMessage.getMessageId());


                            return true;
                        } else {
                            requestPermission();

                            return false;
                        }
                }


                return false;
            }
        });

    }

    @Override
    public void handleIntent() {

        Intent intent = getIntent();
        contactJid = intent.getStringExtra("EXTRA_CONTACT_JID");
        contactName = intent.getStringExtra("EXTRA_CONTACT_NAME");
        orderId = intent.getStringExtra("EXTRA_ORDER_ID");
    }

    @Override
    public void internetConnectionChangeListener(boolean isConnected) {

    }

    private void loadData() {
        iMessages.clear();
        List<ChatMessage> chatMessages = DbUtils.getAllMessagesForOrder(orderId);
        images.clear();
        for (ChatMessage chatMessage : chatMessages) {
            IMessageModel iMessage = new IMessageModel();
            iMessage.setId(chatMessage.getMessageId());
            iMessage.setCreatedAt(AppUtils.getDateFromString(chatMessage.getTimestamp(), AppUtils.FORMAT14));
            iMessage.setText(chatMessage.getBody());
            iMessage.setChatMessage(chatMessage);
            Author author = new Author();
            if (chatMessage.getType() == ChatMessage.Type.RECEIVED) {
                author.setId(contactJid);
                author.setName(contactJid);
                chatMessage.setDeliveryStatus(ChatMessage.DeliveryStatus.READ);
                chatMessage.save();
            } else {
                author.setId(SharedPreferenceHelper.getString(XMPPUser.XMPP_ID, ChatActivity.this));
                author.setName(SharedPreferenceHelper.getString(XMPPUser.XMPP_ID, ChatActivity.this));
            }
            iMessage.setAuthor(author);
            iMessages.add(iMessage);

            if (chatMessage.getMediaType() == ChatMessage.MediaType.IMAGE) {
                if (chatMessage.getLocalPath() != null && !TextUtils.isEmpty(chatMessage.getLocalPath())) {
                    images.add(chatMessage.getLocalPath());
                }else{
                    if (chatMessage.getBody() != null && !TextUtils.isEmpty(chatMessage.getBody())) {
                        images.add(chatMessage.getBody());
                    }
                }
            }
        }
        adapter.clear();
        adapter.addToEnd(iMessages, false);

    }

    @Override
    protected void onDestroy() {
        XmppClient.chatActivity = null;
        super.onDestroy();
    }

    public void reloadData() {
        loadData();
    }

    public void read(String id) {
        loadData();
    }

    public void newMessage(ChatMessage chatMessage) {

        AppUtils.playsound(ChatActivity.this);


        IMessageModel iMessage = new IMessageModel();
        iMessage.setId(chatMessage.getMessageId());
        iMessage.setCreatedAt(AppUtils.getDateFromString(chatMessage.getTimestamp(), AppUtils.FORMAT14));
        iMessage.setText(chatMessage.getBody());
        iMessage.setChatMessage(chatMessage);
        Author author = new Author();
        if (chatMessage.getType() == ChatMessage.Type.RECEIVED) {
            author.setId(contactJid);
            author.setName(contactJid);
        } else {
            author.setId(SharedPreferenceHelper.getString(XMPPUser.XMPP_ID, ChatActivity.this));
            author.setName(SharedPreferenceHelper.getString(XMPPUser.XMPP_ID, ChatActivity.this));
        }
        iMessage.setAuthor(author);
        iMessages.add(iMessage);
        adapter.addToEnd(iMessages, false);


        if (!isPaused) {
            if (XmppClient.getInstance() != null && XmppClient.getInstance().isConnected()) {
                XmppClient.getInstance().sendReadStatus(contactJid, orderId);
            } else {
//                Toast.makeText(getApplicationContext(),"Client not connected to server ,Message not sent!", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onPause() {
        isPaused = true;
        super.onPause();
    }

    protected void onResume() {
        Log.i("on Resume", "RESUMED");
        if (XmppClient.getInstance() != null && XmppClient.getInstance().isConnected()) {
            XmppClient.getInstance().sendReadAllStatus(contactJid, orderId);
        } else {
            if(XmppClient.getInstance()!=null){
                if(!XmppClient.getInstance().isConnected())
                    XmppClient.getInstance().connect("retry");
                else if(!XmppClient.getInstance().isAuthenticated())
                    XmppClient.getInstance().login();
            }else
            {
                Intent serviceIntent = new Intent(ChatActivity.this, XmppService.class);
//                bindService(serviceIntent, mConnection, Context.BIND_AUTO_CREATE);
                startService(serviceIntent);
            }
//            Toast.makeText(getApplicationContext(),"Client not connected to server ,Message not sent!", Toast.LENGTH_LONG).show();
        }
        isPaused = false;
        super.onResume();
    }

    public void setChatStatus(String status, String participant) {
        if (participant.equals(contactJid)) {
            if (TextUtils.isEmpty(status)) {
                userStatus.setVisibility(View.GONE);
            } else {
                userStatus.setText(status);
                userStatus.setVisibility(View.VISIBLE);
            }
        } else {
            userStatus.setVisibility(View.GONE);
            Log.d(TAG, "Got a message from jid :" + participant);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE) {

            if (resultCode == Activity.RESULT_OK) {
                String timestamp = String.valueOf(AppUtils.getCurrentTimeStamp());

                ChatMessage chatMessage = new ChatMessage(AppUtils.generateNewMessageId(), "", orderId, ChatMessage.MediaType.IMAGE, imageFilePath, contactJid, timestamp);

                chatMessage.setType(ChatMessage.Type.SENT);
                chatMessage.setMediaType(ChatMessage.MediaType.IMAGE);
                chatMessage.setDeliveryStatus(ChatMessage.DeliveryStatus.UNSENT);

                chatMessage.save();


                IMessageModel iMessage = new IMessageModel();
                iMessage.setId(chatMessage.getMessageId());
                iMessage.setCreatedAt(AppUtils.getDateFromString(chatMessage.getTimestamp(), AppUtils.FORMAT14));
                iMessage.setText(chatMessage.getBody());
                iMessage.setChatMessage(chatMessage);
                Author author = new Author();
                author.setId(SharedPreferenceHelper.getString(XMPPUser.XMPP_ID, ChatActivity.this));
                author.setName(SharedPreferenceHelper.getString(XMPPUser.XMPP_ID, ChatActivity.this));
                iMessage.setAuthor(author);
                adapter.addToStart(iMessage, true);

                uploadfile(imageFilePath, chatMessage.getMessageId());

//                Bundle extras = data.getExtras();
//                Bitmap imageBitmap = (Bitmap) extras.get("data");
//                String base64Image = AppUtils.getBase64(imageBitmap);
            } else if (resultCode == Activity.RESULT_CANCELED) {
                // User Cancelled the action
            }
        }
        else if (requestCode == PICK_IMAGE_GALLERY) {

            if (resultCode == Activity.RESULT_OK) {
                String timestamp = String.valueOf(AppUtils.getCurrentTimeStamp());
                Uri selectedImage = data.getData();
                String path = getRealPathFromURI(selectedImage);
                ChatMessage chatMessage = new ChatMessage(AppUtils.generateNewMessageId(), "", orderId, ChatMessage.MediaType.IMAGE, path, contactJid, timestamp);

                chatMessage.setType(ChatMessage.Type.SENT);
                chatMessage.setMediaType(ChatMessage.MediaType.IMAGE);
                chatMessage.setDeliveryStatus(ChatMessage.DeliveryStatus.UNSENT);

                chatMessage.save();


                IMessageModel iMessage = new IMessageModel();
                iMessage.setId(chatMessage.getMessageId());
                iMessage.setCreatedAt(AppUtils.getDateFromString(chatMessage.getTimestamp(), AppUtils.FORMAT14));
                iMessage.setText(chatMessage.getBody());
                iMessage.setChatMessage(chatMessage);
                Author author = new Author();
                author.setId(SharedPreferenceHelper.getString(XMPPUser.XMPP_ID, ChatActivity.this));
                author.setName(SharedPreferenceHelper.getString(XMPPUser.XMPP_ID, ChatActivity.this));
                iMessage.setAuthor(author);
                adapter.addToStart(iMessage, true);

                uploadfile(path, chatMessage.getMessageId());


//                Bundle extras = data.getExtras();
//                Bitmap imageBitmap = (Bitmap) extras.get("data");
//                String base64Image = AppUtils.getBase64(imageBitmap);
            } else if (resultCode == Activity.RESULT_CANCELED) {
                // User Cancelled the action
            }
//            try {
//                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
//                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
//                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, bytes);
//                Log.e("Activity", "Pick from Gallery::>>> ");
//
//                imgPath = getRealPathFromURI(selectedImage);
//                destination = new File(imgPath.toString());
//                imageview.setImageBitmap(bitmap);
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
        }
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(ChatActivity.this, new
                String[]{WRITE_EXTERNAL_STORAGE, RECORD_AUDIO}, MY_PERMISSIONS_RECORD_AUDIO);
    }

    //Handling callback
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_RECORD_AUDIO:
                if (grantResults.length > 0) {
                    boolean StoragePermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean RecordPermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (StoragePermission && RecordPermission) {
//                        Toast.makeText(ChatActivity.this, "Permission Granted",Toast.LENGTH_LONG).show();
                    } else {
//                        Toast.makeText(ChatActivity.this, "Permission Denied", Toast.LENGTH_LONG).show();
                    }
                }
                break;
            case REQUEST_CODE_CAMERA:
                if (grantResults.length > 0) {

                    boolean StoragePermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean RecordPermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (StoragePermission && RecordPermission) {
//                        Toast.makeText(ChatActivity.this, "Permission Granted",Toast.LENGTH_LONG).show();
                        openCameraIntent();
                    } else {
//                        Toast.makeText(ChatActivity.this, "Permission Denied", Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }

    }

    private File createImageFile() throws IOException {

        String audioSavePathInDevice = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + App.getInstance().getResources().getString(R.string.app_name);
        File sd = new File(audioSavePathInDevice);

        if (!sd.exists()) // if there was no folder at this path , then create it .
        {
            sd.mkdirs();
        }

        String fileName = "image_" + AppUtils.getCurrentTimeStampInMilliSeconds();

        File image = File.createTempFile(
                fileName,  /* prefix */
                ".jpg",         /* suffix */
                sd      /* directory */
        );

        imageFilePath = image.getAbsolutePath();
        return image;
    }

    private void openCameraIntent() {
        final CharSequence[] options = {"Take Photo", "Choose From Gallery","Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
        builder.setTitle("Select Option");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take Photo")) {
                    dialog.dismiss();
//                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                    startActivityForResult(intent, PICK_IMAGE_CAMERA);
                    Intent pictureIntent = new Intent(
                            MediaStore.ACTION_IMAGE_CAPTURE);
                    if (pictureIntent.resolveActivity(getPackageManager()) != null) {
                        //Create a file to store the image
                        File photoFile = null;
                        try {
                            photoFile = createImageFile();
                        } catch (IOException ex) {
                            // Error occurred while creating the File
                        }
                        if (photoFile != null) {

                            Uri photoURI = FileProvider.getUriForFile(
                                    ChatActivity.this,
                                    getApplicationContext().getPackageName() + ".provider", photoFile);
                            pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                            startActivityForResult(pictureIntent, REQUEST_IMAGE_CAPTURE);
                        }
                    }
                } else if (options[item].equals("Choose From Gallery")) {
                    dialog.dismiss();
                    Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pickPhoto, PICK_IMAGE_GALLERY);
                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
//        Intent pictureIntent = new Intent(
//                MediaStore.ACTION_IMAGE_CAPTURE);
//        if (pictureIntent.resolveActivity(getPackageManager()) != null) {
//            //Create a file to store the image
//            File photoFile = null;
//            try {
//                photoFile = createImageFile();
//            } catch (IOException ex) {
//                // Error occurred while creating the File
//            }
//            if (photoFile != null) {
//
//                Uri photoURI = FileProvider.getUriForFile(
//                        this,
//                        getApplicationContext().getPackageName() + ".provider", photoFile);
//                pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
//                startActivityForResult(pictureIntent, REQUEST_IMAGE_CAPTURE);
//            }
//        }
    }


    public boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED &&
                result1 == PackageManager.PERMISSION_GRANTED;
    }
 public boolean checkCameraPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA);
        int result2 = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);

        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED && result2 == PackageManager.PERMISSION_GRANTED;
    }


    @Override
    public void onSuccess(String calledApi, String response) {
        Log.i("FILEUPLOAD SUCCESS", response);
//        {"id":"94669-3319-2353-710053137708","pathUrl":"http:\/\/localhost:19005\/Upload\/19f06fe7-062d-4c17-917d-d7c6e0d603c2_image_1593420935803676058753.jpg"}
//{"returnCode":200,"message":"Image successfully Uploaded.","response":{"id":"41710-2168-2728-483476067661","pathUrl":"http:\/\/localhost:19005\/Upload\/e336f943-df0f-448a-92b7-c753cd9e10ba_image_159342289654238135210.jpg"}}
        ExclusionStrategy exclusionStrategy = new ExclusionStrategy() {

            @Override
            public boolean shouldSkipField(FieldAttributes fieldAttributes) {
                return false;
            }

            @Override
            public boolean shouldSkipClass(Class<?> clazz) {
                return clazz == Field.class || clazz == Method.class;
            }
        };

        Gson gson = new GsonBuilder()
                .addSerializationExclusionStrategy(exclusionStrategy)
                .addDeserializationExclusionStrategy(exclusionStrategy)
                .create();

        try {
            FileUploadResponseModel fileUploadResponseModel = gson.fromJson(response, new TypeToken<FileUploadResponseModel>() {
            }.getType());
            List<ChatMessage> chatMessages = DbUtils.getMessageById(fileUploadResponseModel.getId());

            for (ChatMessage chatMessage : chatMessages) {
                chatMessage.setBody(fileUploadResponseModel.getPathUrl());
                chatMessage.save();
                XmppClient.getInstance().sendMessage(fileUploadResponseModel.getPathUrl(), contactJid, chatMessage.getMessageId(), orderId, chatMessage.getMediaType());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        loadData();

    }

    @Override
    public void onError(String calledApi, String errorMessage) {
        if (errorMessage != null && !TextUtils.isEmpty(errorMessage))
            Log.i("FILEUPLOAD ERROR", errorMessage);
        loadData();
    }

    @Override
    public void onSuccess(String calledApi) {
    }

    @Override
    public void onError(String calledApi) {

    }

    @Override
    public void onError(String calledApi, int errorCode) {
        if(errorCode == 403)
            AlertDialogUtils.showAlertDialog(ChatActivity.this, AlertDialogUtils.ALERT_DIALOG_WARNING, getString(R.string.alert), getString(R.string.app_update), getString(R.string.ok), null, true);

    }

    @Override
    public void onError(String calledApi, String errorMessage, int errorCode) {
        if(errorCode == 403)
            AlertDialogUtils.showAlertDialog(ChatActivity.this, AlertDialogUtils.ALERT_DIALOG_WARNING, getString(R.string.alert), errorMessage, getString(R.string.ok), null, true);
    }

    void sendChatMessage(String path, String chatId){
        try {

            List<ChatMessage> chatMessages = DbUtils.getMessageById(chatId);

            for (ChatMessage chatMessage : chatMessages) {
                chatMessage.setBody(path);
                chatMessage.save();
                XmppClient.getInstance().sendMessage(path, contactJid, chatMessage.getMessageId(), orderId, chatMessage.getMediaType());
            }
            loadData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void uploadfile(final String filePath, final String chatMessageId) {

        File file = new File(filePath);
        byte[] arr = new byte[0];
        try {
            arr = org.apache.commons.io.FileUtils.readFileToByteArray(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.i("arr",String.valueOf(arr));
        final byte[] finalArr = arr;

        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, ApiCalls.getFileUpload(), new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                String resultResponse = new String(response.data);
                try {
                    JSONObject result = new JSONObject(resultResponse);
                    String status = result.getString("returnCode");
                    String message = result.getString("message");

                    if (status.equals("200")) {
                        Log.i("Messsage", message);
                        JSONObject resp = result.getJSONObject("response");
                        String path = resp.getJSONArray("filePaths").optString(0);

                        sendChatMessage(path, chatMessageId);

                    } else {
                        Log.i("Unexpected", message);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                String errorMessage = "Unknown error";
                if (networkResponse == null) {
                    if (error.getClass().equals(TimeoutError.class)) {
                        errorMessage = "Request timeout";
                    } else if (error.getClass().equals(NoConnectionError.class)) {
                        errorMessage = "Failed to connect server";
                    }
                } else {
                    String result = new String(networkResponse.data);
                    try {
                        JSONObject response = new JSONObject(result);
                        //String status = response.getString("status");
                        String message = response.getString("message");

                        //Log.e("Error Status", status);
                        Log.e("Error Message", message);

                        if (networkResponse.statusCode == 404) {
                            errorMessage = "Resource not found";
                        } else if (networkResponse.statusCode == 401) {
                            errorMessage = message+" Please login again";
                        } else if (networkResponse.statusCode == 400) {
                            errorMessage = message+ " Check your inputs";
                        } else if (networkResponse.statusCode == 500) {
                            errorMessage = message+" Something is getting wrong";
                        } else if(networkResponse.statusCode == 403) {
                            AlertDialogUtils.showAlertDialog(ChatActivity.this, AlertDialogUtils.ALERT_DIALOG_WARNING, getString(R.string.alert), message, getString(R.string.ok), null, true);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                Log.i("Error", errorMessage);
                error.printStackTrace();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                //params.put("api_token", "gh659gjhvdyudo973823tt9gvjf7i6ric75r76");
                //params.put("name", mNameInput.getText().toString());
                //params.put("location", mLocationInput.getText().toString());
                //params.put("about", mAboutInput.getText().toString());
                //params.put("contact", mContactInput.getText().toString());
                return null;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                // file name could found file base or direct access from real path
                // for now just get bitmap data from ImageView
                params.put("ChatFiles", new DataPart(filePath.substring(filePath.lastIndexOf("/")+1), finalArr, filePath.endsWith("aac")? "audio/aac":"image/jpeg"));
//                params.put("cover", new DataPart("file_cover.jpg", AppHelper.getFileDataFromDrawable(getBaseContext(), mCoverImage.getDrawable()), "image/jpeg"));

                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> header = AppUtils.getFileStandardHeaders(ChatActivity.this);
                header.put("x-api-version", "R-"+ BuildConfig.VERSION_NAME);
                return header;
            }
        };

        VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(multipartRequest);
    }

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }
}

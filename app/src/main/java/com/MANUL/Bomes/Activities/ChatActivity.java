package com.MANUL.Bomes.Activities;

import static com.MANUL.Bomes.Utils.ServerUtilsKt.NowRequest;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.MANUL.Bomes.Adapters.MessagesAdapter;
import com.MANUL.Bomes.Adapters.StickersAdapter;
import com.MANUL.Bomes.ExplosionField.ExplosionField;
import com.MANUL.Bomes.ImportantClasses.FileUploadService;
import com.MANUL.Bomes.ImportantClasses.ServiceGenerator;
import com.MANUL.Bomes.R;
import com.MANUL.Bomes.SimpleObjects.Message;
import com.MANUL.Bomes.SimpleObjects.Sticker;
import com.MANUL.Bomes.SimpleObjects.UniversalJSONObject;
import com.MANUL.Bomes.SimpleObjects.User;
import com.MANUL.Bomes.SimpleObjects.UserData;
import com.MANUL.Bomes.Utils.FileUtils;
import com.MANUL.Bomes.Utils.PermissionUtils;
import com.MANUL.Bomes.Utils.RequestCreationFactory;
import com.bumptech.glide.Glide;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.cfg.CoercionAction;
import com.fasterxml.jackson.databind.cfg.CoercionInputShape;
import com.fasterxml.jackson.databind.type.LogicalType;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import retrofit2.Call;
import retrofit2.Callback;

public class ChatActivity extends AppCompatActivity {
    ObjectMapper objectMapper = new ObjectMapper();
    WebSocket webSocket;
    private final int GALLERY_REQUEST = 100;
    private static final int MICROPHONE_PERMISSION_CODE = 200;
    private static final int PERMISSION_STORAGE = 101;

    User openedUser;


    ExplosionField explosionField;
    MediaRecorder mediaRecorder;



    CardView backBtn, sendBtn, stickersHolder, openStickersBtn, sendMediaBtn, replyHolder, closeReplyHolder, recordAudio, userInfoCard;
    ImageView inChatAvatar, recordingAudioImage;
    TextView username, onlineText, typingMsg, recordTimeText;
    public RecyclerView messagesRecycler;
    RecyclerView stickersRecycler;
    MessagesAdapter adapter;
    StickersAdapter stickersAdapter;
    EditText messageText;
    ConstraintLayout chatLayout;
    long lastOnline = 0;
    long loadedMessages = 0;

    boolean loadingMessagesNow = false;
    boolean isStop = false;
    Animation alpha_in, alpha_out;
    boolean typing = false;
    boolean recording = false;
    int recordTime = 0;
    String dirAudio, audioFileName;

    Handler handler = new Handler();
    InputMethodManager imm;

    ArrayList<Message> messages = new ArrayList<>();
    ArrayList<Sticker> stickers = new ArrayList<>();
    ArrayList<Sticker> allStickers = new ArrayList<>();
    ArrayList<Message> waitingMessages = new ArrayList<>();
    ArrayList<UniversalJSONObject> users = new ArrayList<>();
    public ArrayList<String> reactions = new ArrayList<>();
    ArrayList<ArrayList<String>> hints = new ArrayList<>();
    Message replyingMessage;
    Message editingMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.blue));

        explosionField = ExplosionField.attach2Window(this);

        dirAudio = this.getExternalFilesDir(null).getAbsolutePath() + "/Audios/";
        File boMesDirectoryAudio = new File(dirAudio);
        boMesDirectoryAudio.mkdirs();
        audioFileName = "record.mp3";

        init();
        connectToServer();
    }

    private void connectToServer(){
        OkHttpClient client = new OkHttpClient.Builder().build();
        Request request = NowRequest;

        webSocket = client.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onFailure(@NonNull WebSocket ws, @NonNull Throwable t, @Nullable Response response) {
                super.onFailure(ws, t, response);
                WebSocketListener listener = this;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ChatActivity.this, "Переподключение...", Toast.LENGTH_LONG).show();
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                webSocket = client.newWebSocket(request, listener);
                            }
                        }, 1000);
                    }
                });
            }
            @Override
            public void onMessage(@NonNull WebSocket ws, @NonNull String text)  {
                super.onMessage(ws, text);
                runOnUiThread(new Runnable() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void run() {
                        try {
                            UniversalJSONObject obj = objectMapper.readValue(text, UniversalJSONObject.class);
                            if (obj.event.equals("WrongAuthInIdentifier")){
                                Toast.makeText(ChatActivity.this, "Данные авторизации устарели!", Toast.LENGTH_LONG).show();
                                UserData.avatar = null;
                                UserData.identifier = null;
                                UserData.email = null;
                                UserData.description = null;
                                UserData.username = null;
                                UserData.table_name = null;
                                UserData.chatId = null;
                                UserData.chatAvatar = null;
                                UserData.isLocalChat = 0;
                                Intent intent = new Intent(ChatActivity.this, MainActivity.class);
                                startActivity(intent);
                                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                finish();
                                webSocket.close(1000, null);
                            }
                            if (obj.event.equals("ReturnUser")){
                                if (obj.user.identifier.equals(UserData.identifier)){
                                    UserData.username = obj.user.username;
                                    UserData.email = obj.user.email;
                                    UserData.description = obj.user.description;
                                    UserData.avatar = obj.user.avatar;
                                }
                                if (obj.user.identifier.equals(UserData.chatId)){
                                    if (!obj.user.avatar.isEmpty())
                                        Glide.with(ChatActivity.this).load("https://bomes.ru/" + obj.user.avatar).into(inChatAvatar);
                                    else
                                        Glide.with(ChatActivity.this).load("https://bomes.ru/media/icon.png").into(inChatAvatar);
                                    openedUser = new User(obj.user.username, obj.user.avatar, obj.user.identifier, obj.user.friendsCount);
                                    username.setText(obj.user.username);
                                    lastOnline = obj.user.lastOnline;
                                    UniversalJSONObject isUserOnline = RequestCreationFactory.create("IsUserOnline", obj.user.identifier);
                                    webSocket.send(objectMapper.writeValueAsString(isUserOnline));
                                }
                            }
                            else if (obj.event.equals("ReturnOnline")){
                                if (!obj.isOnline){
                                    setLastOnlineText();
                                    onlineText.setTextColor(getResources().getColor(R.color.white,  null));
                                }
                                else{
                                    onlineText.setText("Онлайн");
                                    onlineText.setTextColor(getResources().getColor(R.color.green,  null));
                                }
                            }
                            else if (obj.event.equals("ReturnChatMessages")){
                                for (UniversalJSONObject msg : obj.messages) {
                                    messages.add(0, new Message(msg.username, msg.dataType, msg.value, msg.reply, msg.isRead, msg.time, msg.id, msg.sender, msg.reaction, msg.avatar));
                                    adapter.notifyItemInserted(0);
                                    if (!msg.sender.equals(UserData.identifier)){
                                        UniversalJSONObject readMsg = RequestCreationFactory.create("ReadMessage", Long.toString(msg.id));
                                        webSocket.send(objectMapper.writeValueAsString(readMsg));
                                    }
                                }
                                if (loadedMessages == 0){
                                    messagesRecycler.scrollToPosition(messages.size()-1);
                                }
                                loadedMessages += obj.messages.length;
                                loadingMessagesNow = false;
                                findViewById(R.id.loadingBar).setVisibility(View.GONE);
                            }
                            else if (obj.event.equals("SendMessage")){
                                Message message = new Message(obj.username, obj.dataType, obj.value, obj.reply, obj.isRead, obj.time, obj.id, obj.sender, new UniversalJSONObject[0], obj.avatar);
                                messages.add(message);
                                loadedMessages++;
                                if (!obj.sender.equals(UserData.identifier) && obj.isRead == 0 && !isStop){
                                    UniversalJSONObject readMsg = RequestCreationFactory.create("ReadMessage", Long.toString(obj.id));
                                    webSocket.send(objectMapper.writeValueAsString(readMsg));
                                }
                                else if (isStop){
                                    waitingMessages.add(message);
                                }
                                messagesRecycler.scrollToPosition(messages.size()-1);
                                adapter.notifyItemInserted(messages.size());
                            }
                            else if (obj.event.equals("MessageIsRead")){
                                long id = obj.id;
                                for (int i = messages.size()-1; i > 0; i--){
                                    if (messages.get(i).id == id){
                                        messages.get(i).isRead = 1;
                                        adapter.notifyItemChanged(i);
                                        break;
                                    }
                                }
                            }
                            else if (obj.event.equals("Typing")){
                                if (!obj.identifier.equals(UserData.identifier) && !typing) {
                                    typing = true;
                                    typingMsg.setVisibility(View.VISIBLE);
                                    onlineText.setVisibility(View.GONE);
                                    if (UserData.isLocalChat == 1){
                                        if (obj.typingType.equals("text"))
                                            typingMsg.setText("Печатает...");
                                        else if (obj.typingType.equals("audio"))
                                            typingMsg.setText("Записывает голосовое сообщение...");
                                    }
                                    else{
                                        int index = getUserIndex(obj.identifier);
                                        if (index != -1){
                                            if (obj.typingType.equals("text"))
                                                typingMsg.setText(users.get(index).username + " печатает...");
                                            else if (obj.typingType.equals("audio"))
                                                typingMsg.setText(users.get(index).username + " записывает голосовое...");
                                        }
                                    }
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            typing = false;
                                            typingMsg.setVisibility(View.GONE);
                                            onlineText.setVisibility(View.VISIBLE);
                                        }
                                    }, 2000);
                                }
                            }
                            else if (obj.event.equals("ReturnStickers")){
                                for (String l : obj.stickers) {
                                    allStickers.add(new Sticker(l));
                                }
                                SharedPreferences preferences = getSharedPreferences("StickersHints", MODE_PRIVATE);
                                for (int i = 0; i < obj.hints.length; i++) {
                                    hints.add(new ArrayList<>());
                                    for (int j = 0; j < obj.hints[i].length; j++) {
                                        hints.get(i).add(obj.hints[i][j]);
                                    }
                                    String data = preferences.getString(String.valueOf(i), "");
                                    String[] values = data.split(CustomizeStickerHints.separator);
                                    for (int j = 0; j < values.length; j++) {
                                        if (!values[j].isEmpty())
                                            hints.get(i).add(values[j]);
                                    }
                                }
                                stickersAdapter.notifyDataSetChanged();
                            }
                            else if (obj.event.equals("ReturnReactions")){
                                reactions.addAll(Arrays.asList(obj.reactionsURLs));
                            }
                            else if (obj.event.equals("ReturnChatUsers")){
                                users.clear();
                                users.addAll(Arrays.asList(obj.members));
                            }
                            else if (obj.event.equals("EditMessageForUsers")){
                                int index = getMessageIndex(obj.messageId);
                                if (index != -1) {
                                    messages.get(index).value = obj.value;
                                    adapter.notifyItemChanged(index);
                                }
                                else{
                                    Toast.makeText(ChatActivity.this, "Сообщение не прогружено", Toast.LENGTH_SHORT).show();
                                }
                            }
                            else if (obj.event.equals("DeleteMessageForUsers")){
                                long id = obj.messageId;
                                int index = getMessageIndex(id);
                                if (index != -1){
                                    Message msg = messages.get(index);
                                    View explode = msg.holder;
                                    if (explode != null && msg.viewHolder.message.equals(msg))
                                        explosionField.explode(explode);

                                    messages.remove(index);
                                    adapter.notifyItemRemoved(index);
                                }
                                else{
                                    Toast.makeText(ChatActivity.this, "Непрогруженное сообщение удалено", Toast.LENGTH_SHORT).show();
                                }
                            }
                            else if (obj.event.equals("ReactionForUsers")){
                                int index = getMessageIndex(obj.messageId);
                                if (index != -1) {
                                    messages.get(index).reactions = obj.reactions;
                                    adapter.notifyItemChanged(index);
                                }
                            }
                        } catch (JsonProcessingException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
            }
            @Override
            public void onOpen(@NonNull WebSocket ws, @NonNull Response response) {
                super.onOpen(ws, response);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                UniversalJSONObject obj = RequestCreationFactory.create("setIdentifier");
                                webSocket.send(objectMapper.writeValueAsString(obj));

                                UniversalJSONObject loadMe = RequestCreationFactory.create("GetUser");
                                webSocket.send(objectMapper.writeValueAsString(loadMe));

                                UniversalJSONObject setChat = RequestCreationFactory.create("setChat");
                                webSocket.send(objectMapper.writeValueAsString(setChat));

                                UniversalJSONObject getStickers = RequestCreationFactory.create("GetStickers");
                                webSocket.send(objectMapper.writeValueAsString(getStickers));

                                UniversalJSONObject getReactions = RequestCreationFactory.create("GetReactions");
                                webSocket.send(objectMapper.writeValueAsString(getReactions));

                                if (UserData.isLocalChat == 0) {
                                    UniversalJSONObject getChatUsers = RequestCreationFactory.create("GetChatUsers");
                                    webSocket.send(objectMapper.writeValueAsString(getChatUsers));
                                }


                                if (UserData.chatId != null && UserData.isLocalChat == 1) {
                                    UniversalJSONObject loadOther = RequestCreationFactory.create("GetPartner", UserData.chatId);
                                    webSocket.send(objectMapper.writeValueAsString(loadOther));
                                } else if (UserData.isLocalChat == 0) {
                                    if (!UserData.chatAvatar.isEmpty())
                                        Glide.with(ChatActivity.this).load("https://bomes.ru/" + UserData.chatAvatar).into(inChatAvatar);
                                    else
                                        Glide.with(ChatActivity.this).load("https://bomes.ru/media/icon.png").into(inChatAvatar);
                                    username.setText(UserData.chatName);
                                    lastOnline = 0;
                                    onlineText.setText("");
                                }
                                loadMessages();
                            }
                            catch (JsonProcessingException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    });
            }
        });
    }
    private void init(){
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        alpha_in = AnimationUtils.loadAnimation(this, R.anim.alpha_in);
        alpha_out = AnimationUtils.loadAnimation(this, R.anim.alpha_out);
        backBtn = findViewById(R.id.backBtn);
        inChatAvatar = findViewById(R.id.inChatAvatar);
        username = findViewById(R.id.username);
        onlineText = findViewById(R.id.onlineText);
        typingMsg = findViewById(R.id.typingMsg);
        stickersHolder = findViewById(R.id.stickersHolder);
        openStickersBtn = findViewById(R.id.openStickersBtn);
        recordAudio = findViewById(R.id.recordAudio);
        sendMediaBtn = findViewById(R.id.sendMediaBtn);
        chatLayout = findViewById(R.id.chatLayout);
        replyHolder = findViewById(R.id.replyHolder);
        closeReplyHolder = findViewById(R.id.closeReplyHolder);
        messagesRecycler = findViewById(R.id.messagesRecycler);
        recordingAudioImage = findViewById(R.id.recordingAudioImage);
        recordTimeText = findViewById(R.id.recordTimeText);
        userInfoCard = findViewById(R.id.userInfoCard);
        adapter = new MessagesAdapter(this, messages, this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        messagesRecycler.setLayoutManager(layoutManager);
        messagesRecycler.setAdapter(adapter);

        messagesRecycler.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int id = layoutManager.findFirstVisibleItemPosition();
                if (id < 5){
                    if (!loadingMessagesNow) {
                        loadMessages();
                    }
                }
            }
        });

        stickersRecycler = findViewById(R.id.stickersRecycler);
        stickersAdapter = new StickersAdapter(this, stickers, this);
        stickersRecycler.setLayoutManager(new GridLayoutManager(this, 3));
        stickersRecycler.setAdapter(stickersAdapter);

        messageText = findViewById(R.id.messageText);
        sendBtn = findViewById(R.id.sendBtn);

        objectMapper.coercionConfigFor(LogicalType.POJO)
                .setCoercion(CoercionInputShape.EmptyString, CoercionAction.AsNull);

        messageText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (stickersHolder.getVisibility() == View.VISIBLE)
                    closeStickers();
                String value = messageText.getText().toString().toLowerCase();
                if (!value.isEmpty()){
                    recordAudio.setVisibility(View.GONE);
                    sendBtn.setVisibility(View.VISIBLE);
                }
                else{
                    recordAudio.setVisibility(View.VISIBLE);
                    sendBtn.setVisibility(View.GONE);
                }
                try {
                    UniversalJSONObject obj = RequestCreationFactory.create("Typing", "text");
                    webSocket.send(objectMapper.writeValueAsString(obj));
                }
                catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
                stickers.clear();

                boolean has = false;
                for (int i = 0; i < hints.size(); i++) {
                    if (hints.get(i).contains(value)){
                        has = true;
                        stickers.add(allStickers.get(i));
                    }
                }
                stickersAdapter.notifyDataSetChanged();
                if (has)
                    openStickers();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeChat();
            }
        });

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!messageText.getText().toString().isEmpty()){
                    sendMessage(messageText.getText().toString().trim());
                    messageText.setText("");
                }
                else{
                    Toast.makeText(ChatActivity.this, "Введите текст сообщения!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        openStickersBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (stickersHolder.getVisibility() == View.GONE) {
                    stickers.clear();
                    stickers.addAll(allStickers);
                    stickersAdapter.notifyDataSetChanged();
                    openStickers();
                }
                else
                    closeStickers();
            }
        });
        closeReplyHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endReplying();
            }
        });
        sendMediaBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getStoragePermission();

                Intent mediaPickerIntent = new Intent(Intent.ACTION_PICK);

                mediaPickerIntent.setType("image/*, video/*");

                startActivityForResult(mediaPickerIntent, GALLERY_REQUEST);

            }
        });
        recordAudio.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (!isMicrophonePresent()) {
                    getMicrophonePermission();
                    return;
                }
                if(!recording){
                    Log.e("Has MICRO!", "Has MICRO!");
                    try {
                        File file = new File(dirAudio, audioFileName);
                        mediaRecorder = new MediaRecorder();
                        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
                        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
                        mediaRecorder.setAudioEncodingBitRate(48000);
                        mediaRecorder.setAudioSamplingRate(48000);
                        mediaRecorder.setOutputFile(file.getPath());
                        mediaRecorder.prepare();
                        mediaRecorder.start();
                        recording = true;
                        Toast.makeText(getApplicationContext(), "Запись началась!", Toast.LENGTH_SHORT).show();
                        Picasso.with(ChatActivity.this).load(R.drawable.white_mic_active).into(recordingAudioImage);
                        messageText.startAnimation(alpha_out);
                        messageText.setVisibility(View.INVISIBLE);
                        recordTime = 0;
                        recordTimeText.setVisibility(View.VISIBLE);
                        recordTimeText.startAnimation(alpha_in);

                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (recording) {
                                    recordTime++;
                                    int mins = recordTime / 60;
                                    int secs = recordTime % 60;
                                    String strMins = String.valueOf(mins);
                                    if (strMins.length() < 2)
                                        strMins = "0" + strMins;
                                    String strSecs = String.valueOf(secs);
                                    if (strSecs.length() < 2)
                                        strSecs = "0" + strSecs;
                                    recordTimeText.setText(strMins + ":" + strSecs);

                                    UniversalJSONObject obj = RequestCreationFactory.create("Typing", "audio");
                                    try {
                                        webSocket.send(objectMapper.writeValueAsString(obj));
                                    } catch (JsonProcessingException e) {
                                        throw new RuntimeException(e);
                                    }

                                    handler.postDelayed(this, 1000);
                                }
                            }
                        }, 1000);

                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Ошибка!", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    if (mediaRecorder != null) {
                        mediaRecorder.stop();
                        mediaRecorder.release();
                        uploadFile(new File(dirAudio, audioFileName));
                        recording = false;
                        recordTimeText.startAnimation(alpha_out);
                        recordTimeText.setVisibility(View.GONE);
                        messageText.setVisibility(View.VISIBLE);
                        messageText.startAnimation(alpha_in);
                        Picasso.with(ChatActivity.this).load(R.drawable.white_mic).into(recordingAudioImage);
                    }
                }
            }
        });

        userInfoCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UserData.isLocalChat == 1) {
                    UserPageActivity.openedUser = openedUser;
                    Intent intent = new Intent(ChatActivity.this, UserPageActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();

            uploadFile(uri);
        }
    }
    private void uploadFile(Uri fileUri) {

        FileUploadService service = ServiceGenerator.createService(FileUploadService.class);

        File file = FileUtils.getFile(this, fileUri);
        String type = getContentResolver().getType(fileUri);
        RequestBody requestFile;
        if (type != null)
            requestFile =
                RequestBody.create(
                        MediaType.parse(type),
                        file
                );
        else
            requestFile =
                    RequestBody.create(
                            MediaType.parse("audio/amr"),
                            file
                    );

        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);

        String descriptionString = "file";
        RequestBody description = RequestBody.create(okhttp3.MultipartBody.FORM, descriptionString);

        Call<ResponseBody> call = service.upload(description, body);
        call.enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                if (response.isSuccessful()){
                    try {
                        String reply = response.body().string();
                        UniversalJSONObject obj = objectMapper.readValue(reply, UniversalJSONObject.class);
                        String[] typeData = obj.filePath.split("\\.");
                        String type = typeData[typeData.length-1];
                        if (type.equals("jpg") || type.equals("png") || type.equals("jpeg") || type.equals("gif"))
                            sendMedia(obj.filePath, "image");
                        else if (type.equals("mp4") || type.equals("avi"))
                            sendMedia(obj.filePath, "video");
                        else if (type.equals("mp3") || type.equals("wav") || type.equals("amr"))
                            sendMedia(obj.filePath, "audio");
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("Upload error:", t.getMessage());
            }
        });
    }
    private void uploadFile(File file) {

        FileUploadService service = ServiceGenerator.createService(FileUploadService.class);

        Uri fileUri = Uri.fromFile(file);
        String type = getContentResolver().getType(fileUri);
        RequestBody requestFile;
        if (type == null)
            type = URLConnection.guessContentTypeFromName(file.getName());
        Log.e("Type", type);
        requestFile =
                RequestBody.create(
                        MediaType.parse(type),
                        file
                );

        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);

        String descriptionString = "file";
        RequestBody description = RequestBody.create(okhttp3.MultipartBody.FORM, descriptionString);

        Call<ResponseBody> call = service.upload(description, body);
        call.enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                if (response.isSuccessful()){
                    try {
                        String reply = response.body().string();
                        UniversalJSONObject obj = objectMapper.readValue(reply, UniversalJSONObject.class);
                        String[] typeData = obj.filePath.split("\\.");
                        String type = typeData[typeData.length-1];
                        if (type.equals("jpg") || type.equals("png") || type.equals("jpeg") || type.equals("gif"))
                            sendMedia(obj.filePath, "image");
                        else if (type.equals("mp4") || type.equals("avi"))
                            sendMedia(obj.filePath, "video");
                        else if (type.equals("mp3") || type.equals("wav") || type.equals("amr") || type.equals("ogg"))
                            sendMedia(obj.filePath, "audio");
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("Upload error:", t.getMessage());
            }
        });
    }
    private void sendMessage(String messageText) {
        try {
            if (editingMessage == null) {
                UniversalJSONObject msg = RequestCreationFactory.create("message","text", messageText, replyingMessage);
                webSocket.send(objectMapper.writeValueAsString(msg));
                endReplying();
            }
            else{
                endEditing();
            }
        }
        catch (JsonProcessingException e){
            throw new RuntimeException(e);
        }
    }

    private void sendMedia(String url, String type){
        try {
            if (editingMessage == null) {
                UniversalJSONObject msg = RequestCreationFactory.create("message",type, url, replyingMessage);
                webSocket.send(objectMapper.writeValueAsString(msg));
                endReplying();
            }
            else{
                endEditing();
            }
        }
        catch (JsonProcessingException e){
            throw new RuntimeException(e);
        }
    }
    private void loadMessages(){
        try {
            loadingMessagesNow = true;
            UniversalJSONObject obj = RequestCreationFactory.create("GetChatMessages",Long.toString(loadedMessages));
            webSocket.send(objectMapper.writeValueAsString(obj));
        }
        catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
    private void setLastOnlineText(){
        Date onlineDateAsDate = new Date(lastOnline * 1000);
        Date nowDateAsDate = new Date(System.currentTimeMillis());
        String onlineDate = new SimpleDateFormat("dd.MM.YYYY").format(onlineDateAsDate);
        String nowDate = new SimpleDateFormat("dd.MM.YYYY").format(nowDateAsDate);
        String time = new SimpleDateFormat("HH:mm").format(onlineDateAsDate);

        if (onlineDate.equals(nowDate)){
            onlineText.setText("Был(а) в сети: " + time);
        }
        else if (onlineDateAsDate.getYear() == nowDateAsDate.getYear() && onlineDateAsDate.getMonth() == nowDateAsDate.getMonth()
                && onlineDateAsDate.getDay() == nowDateAsDate.getDay() - 1){
            onlineText.setText("Был(а) в сети: вчера в " + time);
        }
        else{
            onlineText.setText("Был(а) в сети: " + new SimpleDateFormat("dd.MM.YYYY").format(onlineDateAsDate) + " в " + time);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            closeChat();
        }
        return super.onKeyDown(keyCode, event);
    }
    private void openStickers(){
        stickersHolder.setVisibility(View.VISIBLE);
        stickersHolder.startAnimation(alpha_in);
    }
    public void closeStickers(){
        stickersHolder.startAnimation(alpha_out);
        stickersHolder.setVisibility(View.GONE);
    }
    public void sendSticker(String v){
        try {
            UniversalJSONObject msg = RequestCreationFactory.create("message","sticker", v, replyingMessage);
            webSocket.send(objectMapper.writeValueAsString(msg));
            endReplying();
        }
        catch (JsonProcessingException e){
            throw new RuntimeException(e);
        }
    }

    private int getUserIndex(String identifier){
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).identifier.equals(identifier)){
                return i;
            }
        }
        return -1;
    }
    private int getMessageIndex(long id){
        if (messages.get(0).id <= id) {
            int left = 0;
            int right = messages.size();
            int lastIndex = 0;
            boolean end = false;
            while (!end) {
                int middle = (right + left) / 2;
                if (middle == lastIndex)
                    end = true;
                if (messages.get(middle).id > id)
                    right = middle;
                else if (messages.get(middle).id < id)
                    left = middle;
                else
                    return middle;
                lastIndex = middle;
            }
            return lastIndex;
        }
        else{
            return -1;
        }
    }
    public void startReplying(Message message){
        replyingMessage = message;
        replyHolder.setVisibility(View.VISIBLE);
        String replyText = message.username + ": ";
        if (message.dataType.equals("text")){
            replyText += message.value;
        }
        else{
            replyText += "Вложение";
        }
        ((TextView) findViewById(R.id.replyTextHolder)).setText(replyText);
        messageText.setSelection(messageText.getText().length());
        messageText.postDelayed(new Runnable() {
            @Override
            public void run() {
                messageText.requestFocus();
                imm.showSoftInput(messageText, InputMethodManager.SHOW_IMPLICIT);
            }
        }, 200);
    }
    public void endReplying(){
        replyingMessage = null;
        replyHolder.setVisibility(View.GONE);
    }
    public void startEditing(Message message){
        editingMessage = message;
        messageText.setText(message.value);
        messageText.setSelection(messageText.getText().length());
        messageText.postDelayed(new Runnable() {
            @Override
            public void run() {
                messageText.requestFocus();
                imm.showSoftInput(messageText, InputMethodManager.SHOW_IMPLICIT);
            }
        }, 200);
    }
    public void endEditing(){
        UniversalJSONObject msg = RequestCreationFactory.create("EditMessage", messageText.getText().toString(), editingMessage.id);
        try {
            webSocket.send(objectMapper.writeValueAsString(msg));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        editingMessage = null;
        messageText.setText("");
    }
    public void deleteMessage(Message message){
        long id = message.id;
        UniversalJSONObject msg = RequestCreationFactory.create("DeleteMessage", "", id);
        try {
            webSocket.send(objectMapper.writeValueAsString(msg));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
    public void addReaction(String type, long id){
        UniversalJSONObject msg = RequestCreationFactory.create("AddReaction", type, id);
        try {
            webSocket.send(objectMapper.writeValueAsString(msg));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
    public void removeReaction(long id){
        UniversalJSONObject msg = RequestCreationFactory.create("RemoveReaction", "", id);
        try {
            webSocket.send(objectMapper.writeValueAsString(msg));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public void copyMessage(Message message){
        ClipboardManager clipboard = (ClipboardManager) this.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("", message.value);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(ChatActivity.this, "Скопировано в буфер обмена", Toast.LENGTH_SHORT).show();
    }

    private void closeChat(){
        UserData.table_name = null;
        UserData.chatId = null;
        UserData.isLocalChat = 0;
        webSocket.close(1000, null);
        finish();
    }

    public void scrollToMessage(long id){
        int index = getMessageIndex(id);
        if (index != -1){
            messagesRecycler.smoothScrollToPosition(index);
        }
    }
    private boolean isMicrophonePresent(){
        return ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;
    }
    private void getStoragePermission(){
        if (PermissionUtils.hasPermissions(this)) return;
        PermissionUtils.requestPermissions(this, PERMISSION_STORAGE);
    }
    private void getMicrophonePermission(){
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.RECORD_AUDIO}, MICROPHONE_PERMISSION_CODE);
    }

    @Override
    protected void onStop() {
        isStop = true;
        super.onStop();
    }

    @Override
    protected void onResume() {
        isStop = false;
        for (int i = 0; i < waitingMessages.size(); i++) {
            Message message = waitingMessages.get(i);
            if (!message.sender.equals(UserData.identifier) && message.isRead == 0 && !isStop){
                UniversalJSONObject readMsg = RequestCreationFactory.create("ReadMessage", Long.toString(message.id));
                try {
                    webSocket.send(objectMapper.writeValueAsString(readMsg));
                } catch (JsonProcessingException e) {
                    Log.e("Json", "Json");
                }
            }
        }
        super.onResume();
    }
}
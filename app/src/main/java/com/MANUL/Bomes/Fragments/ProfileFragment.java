package com.MANUL.Bomes.Fragments;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.MANUL.Bomes.Activities.ChatsActivity;
import com.MANUL.Bomes.ImportantClasses.FileUploadService;
import com.MANUL.Bomes.Utils.FileUtils;
import com.MANUL.Bomes.Utils.PermissionUtils;
import com.MANUL.Bomes.R;
import com.MANUL.Bomes.ImportantClasses.ServiceGenerator;
import com.MANUL.Bomes.SimpleObjects.UniversalJSONObject;
import com.MANUL.Bomes.SimpleObjects.UserData;
import com.bumptech.glide.Glide;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

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

public class ProfileFragment extends Fragment {

    ObjectMapper objectMapper = new ObjectMapper();
    WebSocket webSocket;

    private static final int PERMISSION_STORAGE = 101;
    private final int GALLERY_REQUEST = 100;

    ChatsActivity activity;

    ImageView avatar;
    EditText username, description;
    CardView saveChanges;

    public ProfileFragment(ChatsActivity activity){
        this.activity = activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
        setValues();
        connectToServer();
    }
    private void init(View view){
        avatar = view.findViewById(R.id.profile_avatar);
        username = view.findViewById(R.id.username_profile_edittext);
        description = view.findViewById(R.id.description_profile_edittext);
        saveChanges = view.findViewById(R.id.saveBtn_profile);

        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getStoragePermission();

                Intent mediaPickerIntent = new Intent(Intent.ACTION_PICK);

                mediaPickerIntent.setType("image/*");

                startActivityForResult(mediaPickerIntent, GALLERY_REQUEST);
            }
        });
        saveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UniversalJSONObject saveData = new UniversalJSONObject();
                saveData.name = username.getText().toString().trim();
                saveData.description = description.getText().toString().trim();
                saveData.where = UserData.identifier;
                saveData.event = "UpdateUserData";

                if (!saveData.name.isEmpty()) {
                    try {
                        webSocket.send(objectMapper.writeValueAsString(saveData));
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                }
                else
                    Toast.makeText(activity, "Поле имени не может быть пустым!", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void setValues(){
        if (UserData.avatar.isEmpty())
            Glide.with(activity).load("https://bomes.ru/media/icon.png").into(avatar);
        else
            Glide.with(activity).load("https://bomes.ru/" + UserData.avatar).into(avatar);
        username.setText(UserData.username);
        description.setText(UserData.description);
    }

    private void connectToServer(){
        OkHttpClient client = new OkHttpClient.Builder().build();
        Request request = new Request.Builder().url("wss://bomes.ru:8000").build();

        webSocket = client.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onFailure(@NonNull WebSocket ws, @NonNull Throwable t, @Nullable Response response) {
                super.onFailure(ws, t, response);
                Log.e("Fail", t.getMessage());
            }
            @Override
            public void onMessage(@NonNull WebSocket ws, @NonNull String text) {
                super.onMessage(ws, text);
                activity.runOnUiThread(new Runnable() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void run() {
                        try {
                            UniversalJSONObject obj = objectMapper.readValue(text, UniversalJSONObject.class);
                            if (obj.event.equals("ReturnUser")){
                                if (obj.user.identifier.equals(UserData.identifier)){
                                    UserData.username = obj.user.username;
                                    UserData.password = obj.user.password;
                                    UserData.email = obj.user.email;
                                    UserData.description = obj.user.description;
                                    UserData.avatar = obj.user.avatar;
                                    setValues();
                                }
                            }
                            else if (obj.event.equals("MatDetected")){
                                Toast.makeText(activity, "Обнаружена нецензурная лексика!", Toast.LENGTH_LONG).show();
                            }
                            else if(obj.event.equals("WithoutMats")){
                                UserData.username = obj.name;
                                UserData.description = obj.description;
                                Intent intent = new Intent(activity, ChatsActivity.class);
                                activity.startActivity(intent);
                                activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                activity.finish();
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
                try {
                    UniversalJSONObject obj = new UniversalJSONObject();
                    obj.event = "setIdentifier";
                    obj.identifier = UserData.identifier;
                    webSocket.send(objectMapper.writeValueAsString(obj));

                    UniversalJSONObject loadMe = new UniversalJSONObject();
                    loadMe.event = "GetUser";
                    loadMe.identifier = UserData.identifier;
                    loadMe.friendId = UserData.identifier;
                    webSocket.send(objectMapper.writeValueAsString(loadMe));
                }
                catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
    private void getStoragePermission(){
        if (PermissionUtils.hasPermissions(activity)) return;
        PermissionUtils.requestPermissions(activity, PERMISSION_STORAGE);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();

            uploadAvatar(uri);
        }
    }
    private void uploadAvatar(Uri fileUri) {

        FileUploadService service = ServiceGenerator.createService(FileUploadService.class);

        File file = FileUtils.getFile(activity, fileUri);
        String type = activity.getContentResolver().getType(fileUri);
        RequestBody requestFile;
        requestFile =
                RequestBody.create(
                        MediaType.parse(type),
                        file
                );

        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);

        String descriptionString = "file";
        RequestBody description = RequestBody.create(okhttp3.MultipartBody.FORM, descriptionString);

        Call<ResponseBody> call = service.avatar(description, body);
        call.enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                if (response.isSuccessful()){
                    try {
                        String reply = response.body().string();
                        UniversalJSONObject obj = objectMapper.readValue(reply, UniversalJSONObject.class);

                        Glide.with(activity).load("https://bomes.ru/" + obj.filePath).into(avatar);

                        UniversalJSONObject updAvatar = new UniversalJSONObject();
                        updAvatar.table = "users";
                        updAvatar.column = "identifier";
                        updAvatar.where = UserData.identifier;
                        updAvatar.variable = "avatar";
                        updAvatar.value = obj.filePath;
                        updAvatar.event = "UpdateValue";

                        webSocket.send(objectMapper.writeValueAsString(updAvatar));
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
}
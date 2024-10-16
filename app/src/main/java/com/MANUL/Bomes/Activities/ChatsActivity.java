package com.MANUL.Bomes.Activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.MANUL.Bomes.Fragments.ChatsFragment;
import com.MANUL.Bomes.Fragments.CreatingChatFragment;
import com.MANUL.Bomes.Fragments.ProfileFragment;
import com.MANUL.Bomes.Fragments.UsersFragment;
import com.MANUL.Bomes.R;
import com.MANUL.Bomes.SimpleObjects.UserData;
import com.bumptech.glide.Glide;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.util.AbstractDrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerImageLoader;

public class ChatsActivity extends AppCompatActivity {
    Toolbar mainToolbar;
    AccountHeader header;
    Drawer drawer;
    SharedPreferences prefs;

    private final ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    // FCM SDK (and your app) can post notifications.
                } else {
                    // TODO: Inform user that that your app will not show notifications.
                }
            });

    ChatsFragment chatsFragment = new ChatsFragment(this);
    UsersFragment usersFragment = new UsersFragment(this);
    ProfileFragment profileFragment = new ProfileFragment(this);
    CreatingChatFragment creatingChatFragment = new CreatingChatFragment();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chats);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.blue));

        if (UserData.avatar == null){
            toSplash();
        }

        prefs = getSharedPreferences("user", Context.MODE_PRIVATE);

        askNotificationPermission();
        init();

        switchFragment(chatsFragment);
    }

    private void init(){
        mainToolbar = findViewById(R.id.mainToolbar);
        mainToolbar.setTitle("Чаты");

        setSupportActionBar(mainToolbar);

        DrawerImageLoader.init(new AbstractDrawerImageLoader() {
            @Override
            public void set(ImageView imageView, Uri uri, Drawable placeholder) {
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                Glide.with(ChatsActivity.this).load(uri).placeholder(placeholder).into(imageView);
            }
        });

        setHeader();
        setDrawer();
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.blue));
    }
    private void setDrawer(){
        drawer = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(mainToolbar)
                .withAccountHeader(header)
                .addDrawerItems(
                        new PrimaryDrawerItem()
                                .withIconTintingEnabled(true)
                                .withName("Чаты")
                                .withSelectable(true)
                                .withIcon(R.drawable.chats)
                                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                                    @Override
                                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                                        switchFragment(chatsFragment);
                                        mainToolbar.setTitle("Чаты");
                                        return false;
                                    }
                                }),
                        new PrimaryDrawerItem()
                                .withIconTintingEnabled(true)
                                .withName("Все пользователи")
                                .withSelectable(true)
                                .withIcon(R.drawable.people)
                                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                                    @Override
                                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                                        switchFragment(usersFragment);
                                        mainToolbar.setTitle("Все пользователи");
                                        return false;
                                    }
                                }),
                        new PrimaryDrawerItem()
                                .withIconTintingEnabled(true)
                                .withName("Создать чат")
                                .withSelectable(true)
                                .withIcon(R.drawable.new_chat)
                                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                                    @Override
                                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                                        switchFragment(creatingChatFragment);
                                        mainToolbar.setTitle("Создание чата");
                                        return false;
                                    }
                                }),
                        new PrimaryDrawerItem()
                                .withIconTintingEnabled(true)
                                .withName("Профиль")
                                .withSelectable(true)
                                .withIcon(R.drawable.human)
                                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                                    @Override
                                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                                        switchFragment(profileFragment);
                                        mainToolbar.setTitle("Профиль");
                                        return false;
                                    }
                                }),
                        new PrimaryDrawerItem()
                                .withIconTintingEnabled(true)
                                .withName("Выйти")
                                .withSelectable(true)
                                .withIcon(R.drawable.logout)
                                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                                    @Override
                                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                                        UserData.identifier = null;
                                        UserData.avatar = null;
                                        UserData.email = null;
                                        UserData.chatId = null;
                                        UserData.password = null;
                                        UserData.description = null;
                                        UserData.isLocalChat = 0;
                                        UserData.chatAvatar = null;
                                        UserData.table_name = null;
                                        UserData.chatName = null;
                                        prefs.edit().putString("identifier", "none").apply();
                                        Intent intent = new Intent(ChatsActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                        finish();
                                        return false;
                                    }
                                })
                )
                .build();
    }

    private void setHeader(){
        Log.e("Data", UserData.username + " " + UserData.email + " " + UserData.avatar);
        ProfileDrawerItem profileDrawerItem = new ProfileDrawerItem()
                .withName(UserData.username)
                .withEmail(UserData.email);
        if (UserData.avatar.isEmpty())
            profileDrawerItem.withIcon(R.drawable.icon);
        else
            profileDrawerItem.withIcon("https://bomes.ru/" + UserData.avatar);
        header = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.gradient)
                .addProfiles(
                        profileDrawerItem
                ).build();
    }
    public void openChat(){
        Intent intent = new Intent(ChatsActivity.this, ChatActivity.class);
        startActivity(intent);
    }
    private void askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                    PackageManager.PERMISSION_GRANTED) {
                // FCM SDK (and your app) can post notifications.
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                // TODO: display an educational UI explaining to the user the features that will be enabled
                //       by them granting the POST_NOTIFICATION permission. This UI should provide the user
                //       "OK" and "No thanks" buttons. If the user selects "OK," directly request the permission.
                //       If the user selects "No thanks," allow the user to continue without notifications.
            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    }

    private void switchFragment(Fragment fragment){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frameMainMenu, fragment);
        fragmentTransaction.commit();
    }
    private void toSplash(){
        Intent intent = new Intent(ChatsActivity.this, SplashScreen.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }
}
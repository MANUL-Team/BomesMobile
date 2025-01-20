package com.MANUL.Bomes.Activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.MANUL.Bomes.R
import com.MANUL.Bomes.SimpleObjects.UserData


class SettingsActivity : AppCompatActivity() {
    private var stickersHintsSettings: CardView? = null
    private var exitFromAccountSettings: CardView? = null
    private var backBtn: CardView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        init()
    }

    private fun init() {
        stickersHintsSettings = findViewById(R.id.stickers_hints_settings)
        stickersHintsSettings?.setOnClickListener {
            val intent = Intent(
                this,
                SelectStickerForHints::class.java
            )
            startActivity(intent)
        }
        exitFromAccountSettings = findViewById(R.id.exit_from_account_settings)
        exitFromAccountSettings?.setOnClickListener {
            val prefs = getSharedPreferences("user", MODE_PRIVATE)
            UserData.identifier = null
            UserData.avatar = null
            UserData.email = null
            UserData.chatId = null
            UserData.password = null
            UserData.description = null
            UserData.isLocalChat = 0
            UserData.chatAvatar = null
            UserData.table_name = null
            UserData.chatName = null
            prefs.edit().putString("identifier", "none").apply()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                overrideActivityTransition(
                    OVERRIDE_TRANSITION_CLOSE,
                    android.R.anim.fade_in,
                    android.R.anim.fade_out
                )
            } else
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        }
        backBtn = findViewById(R.id.backBtn)
        backBtn?.setOnClickListener {
            finish()
        }
    }
    override fun finish() {
        super.finish()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            overrideActivityTransition(
                OVERRIDE_TRANSITION_CLOSE,
                R.anim.nothing,
                R.anim.activity_switch_reverse_first
            )
        } else
            overridePendingTransition(R.anim.nothing, R.anim.activity_switch_reverse_first)
    }
}
package com.MANUL.Bomes.presentation

import android.R
import android.content.Intent
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.MANUL.Bomes.Activities.MainActivity
import com.MANUL.Bomes.SimpleObjects.UniversalJSONObject
import com.MANUL.Bomes.SimpleObjects.User
import com.MANUL.Bomes.SimpleObjects.UserData
import com.MANUL.Bomes.SimpleObjects.UserDataKt
import com.MANUL.Bomes.Utils.RequestEvent

open class BaseRequestHandler(
    protected val activity: FragmentActivity
    ) {
    fun start(obj: UniversalJSONObject) {
        activity.runOnUiThread {
            run {
                when (obj.event) {
                    RequestEvent.WrongAuthInIdentifier -> responseWrongAuthInIdentifier(obj)
                    RequestEvent.ReturnFriends -> responseReturnFriends(obj)
                    RequestEvent.ChatCreated -> chatCreated(obj)
                    RequestEvent.ReturnUser -> responseReturnUser(obj)
                    RequestEvent.MatDetected -> responseProfileChanges(obj)
                    RequestEvent.WithoutMats -> responseProfileChanges(obj)
                }
            }
        }
    }

    protected open fun chatCreated(obj: UniversalJSONObject) {}

    protected open fun responseReturnFriends(obj: UniversalJSONObject) {
        UserDataKt.users.clear()
        for (jsonObject in obj.users) {
            val user = User(
                jsonObject.username,
                jsonObject.avatar,
                jsonObject.identifier,
                jsonObject.friendsCount
            )

            UserDataKt.users.add(user)
        }
    }
    protected open fun responseReturnUser(obj: UniversalJSONObject) {}
    protected open fun responseProfileChanges(obj: UniversalJSONObject) {}

    private fun responseWrongAuthInIdentifier(obj: UniversalJSONObject) {
        Toast.makeText(activity, "Данные авторизации устарели!", Toast.LENGTH_LONG).show()
        UserData.avatar = null
        UserData.identifier = null
        UserData.email = null
        UserData.description = null
        UserData.username = null
        UserData.table_name = null
        UserData.chatId = null
        UserData.chatAvatar = null
        UserData.isLocalChat = 0
        val intent = Intent(activity, MainActivity::class.java)
        activity.startActivity(intent)
        activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        activity.finish()
    }
}
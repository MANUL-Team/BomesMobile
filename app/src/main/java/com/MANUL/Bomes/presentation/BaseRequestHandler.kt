package com.MANUL.Bomes.presentation

import android.R
import android.content.Intent
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.MANUL.Bomes.presentation.view.activities.MainActivity
import com.MANUL.Bomes.data.model.UniversalJSONObject
import com.MANUL.Bomes.domain.SimpleObjects.User
import com.MANUL.Bomes.domain.SimpleObjects.UserData
import com.MANUL.Bomes.domain.SimpleObjects.UserDataKt
import com.MANUL.Bomes.domain.Utils.RequestEvent
import com.MANUL.Bomes.domain.Utils.mapToObjectGson
import com.MANUL.Bomes.domain.model.FriendsList

open class BaseRequestHandler(
    protected val activity: FragmentActivity
) {
    fun start(map: Map<String, Any?>) {
        val obj = mapToObjectGson(map, UniversalJSONObject::class.java)
        when (map["event"]) {
            RequestEvent.WrongAuthInIdentifier -> responseWrongAuthInIdentifier()
            RequestEvent.ReturnFriends -> responseReturnFriends(map)
            RequestEvent.ChatCreated -> chatCreated(obj)
            RequestEvent.ReturnUser -> responseReturnUser(obj)
            RequestEvent.MatDetected -> responseProfileChanges(obj)
            RequestEvent.WithoutMats -> responseProfileChanges(obj)
        }

    }

    protected open fun chatCreated(obj: UniversalJSONObject) {}

    protected open fun responseReturnFriends(map: Map<String, Any?>) {
        val obj = mapToObjectGson(map, FriendsList::class.java)
        UserDataKt.users.clear()
        UserDataKt.users.addAll(obj.users?.toList() ?: emptyList())
    }

    protected open fun responseReturnUser(obj: UniversalJSONObject) {}
    protected open fun responseProfileChanges(obj: UniversalJSONObject) {}

    private fun responseWrongAuthInIdentifier() {
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
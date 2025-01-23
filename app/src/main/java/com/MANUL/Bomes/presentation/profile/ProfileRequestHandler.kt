package com.MANUL.Bomes.presentation.profile

import android.R
import android.content.Intent
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.MANUL.Bomes.Activities.ChatsActivity
import com.MANUL.Bomes.SimpleObjects.UniversalJSONObject
import com.MANUL.Bomes.SimpleObjects.UserData
import com.MANUL.Bomes.Utils.RequestEvent
import com.MANUL.Bomes.presentation.BaseRequestHandler
import okhttp3.WebSocket

class ProfileRequestHandler(
    activity: FragmentActivity
) : BaseRequestHandler(activity) {

    override fun responseReturnUser(obj: UniversalJSONObject) {
        if (obj.user.identifier == UserData.identifier) {
            UserData.username = obj.user.username
            UserData.email = obj.user.email
            UserData.description = obj.user.description
            UserData.avatar = obj.user.avatar
        }
    }

    override fun responseProfileChanges(obj: UniversalJSONObject) {
        if (obj.event == RequestEvent.MatDetected) {
            Toast.makeText(activity, "Обнаружена нецензурная лексика!", Toast.LENGTH_LONG).show()
        } else if (obj.event == RequestEvent.WithoutMats) {
            UserData.username = obj.name
            UserData.description = obj.description
            activity.finish()
            activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }
    }
}
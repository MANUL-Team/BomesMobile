package com.MANUL.Bomes.Activities

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.MANUL.Bomes.ImportantClasses.FileUploadService
import com.MANUL.Bomes.ImportantClasses.ServiceGenerator
import com.MANUL.Bomes.R
import com.MANUL.Bomes.SimpleObjects.UniversalJSONObject
import com.MANUL.Bomes.SimpleObjects.UserData
import com.MANUL.Bomes.Utils.BoMesWebSocket
import com.MANUL.Bomes.Utils.BoMesWebSocketListener
import com.MANUL.Bomes.Utils.FileUtils
import com.MANUL.Bomes.Utils.PermissionUtils
import com.MANUL.Bomes.Utils.RequestCreationFactory
import com.MANUL.Bomes.Utils.RequestCreationFactory.Companion.create
import com.MANUL.Bomes.Utils.RequestEvent
import com.MANUL.Bomes.presentation.profile.ProfileRequestHandler
import com.bumptech.glide.Glide
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import java.io.IOException

class ProfileActivity : AppCompatActivity() {
    private val objectMapper by lazy {
        ObjectMapper()
    }
    private val webSocket by lazy {
        BoMesWebSocket.get()
    }
    private var requestHandler: ProfileRequestHandler? = null

    private var avatar: ImageView? = null
    private var username: EditText? = null
    private var description: EditText? = null
    private var saveChanges: CardView? = null
    private var backBtn: CardView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        init()
        setValues()
        connectToServer()
    }

    private fun init() {
        avatar = findViewById(R.id.profile_avatar)
        username = findViewById(R.id.username_profile_edittext)
        description = findViewById(R.id.description_profile_edittext)
        saveChanges = findViewById(R.id.saveBtn_profile)

        avatar?.setOnClickListener {
            getStoragePermission()
            val mediaPickerIntent = Intent(Intent.ACTION_PICK)

            mediaPickerIntent.setType("image/*")
            startActivityForResult(mediaPickerIntent, GALLERY_REQUEST)
        }
        saveChanges?.setOnClickListener {
            val saveData = RequestCreationFactory.create(
                RequestEvent.UpdateUserData,
                username?.text.toString(),
                description?.text.toString(),
                null
            )
            if (saveData != null) {
                try {
                    webSocket.send(objectMapper.writeValueAsString(saveData))
                } catch (e: JsonProcessingException) {
                    throw RuntimeException(e)
                }
            } else Toast.makeText(this, "Поле имени не может быть пустым!", Toast.LENGTH_SHORT)
                .show()
        }

        backBtn = findViewById(R.id.backBtn)
        backBtn?.setOnClickListener {
            finish()
        }
    }

    private fun setValues() {
        if (UserData.avatar.isEmpty()) Glide.with(this)
            .load("https://bomes.ru/media/icon.png").into(
                avatar!!
            )
        else Glide.with(this).load("https://bomes.ru/" + UserData.avatar).into(
            avatar!!
        )
        username?.setText(UserData.username)
        description?.setText(UserData.description)
    }

    private fun connectToServer() {
        val loadMe = RequestCreationFactory.create(RequestEvent.GetUser)
        webSocket.send(objectMapper.writeValueAsString(loadMe))

        requestHandler = ProfileRequestHandler(this)
        BoMesWebSocketListener.get().setRequestHandler(requestHandler)
    }

    private fun getStoragePermission() {
        if (PermissionUtils.hasPermissions(this)) return
        PermissionUtils.requestPermissions(this, Companion.PERMISSION_STORAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == Companion.GALLERY_REQUEST && resultCode == RESULT_OK && data != null && data.data != null) {
            val uri = data.data

            uploadAvatar(uri)
        }
    }

    private fun uploadAvatar(fileUri: Uri?) {
        val service = ServiceGenerator.createService(
            FileUploadService::class.java
        )

        val file = FileUtils.getFile(this, fileUri)
        val type = contentResolver.getType(fileUri!!)
        val requestFile = RequestBody.create(
            type?.toMediaTypeOrNull(),
            file
        )

        val body: MultipartBody.Part =
            MultipartBody.Part.createFormData("file", file.name, requestFile)

        val descriptionString = "file"
        val description = RequestBody.create(MultipartBody.FORM, descriptionString)

        val call = service.avatar(description, body)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(
                call: Call<ResponseBody>,
                response: retrofit2.Response<ResponseBody>
            ) {
                if (response.isSuccessful) {
                    try {
                        val reply = response.body()!!.string()
                        val obj = objectMapper.readValue(
                            reply,
                            UniversalJSONObject::class.java
                        )

                        Glide.with(this@ProfileActivity).load("https://bomes.ru/" + obj.filePath)
                            .into(avatar!!)

                        val updAvatar = create(RequestEvent.UpdateValue, obj.filePath)
                        webSocket.send(objectMapper.writeValueAsString(updAvatar))
                    } catch (e: IOException) {
                        throw RuntimeException(e)
                    }
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("Upload error:", t.message!!)
            }
        })
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

    companion object {
        private const val PERMISSION_STORAGE: Int = 101
        private const val GALLERY_REQUEST = 100
    }
}
package com.MANUL.Bomes.presentation.view.activities

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.MANUL.Bomes.domain.ImportantClasses.ImportantClasses.FileUploadService
import com.MANUL.Bomes.domain.ImportantClasses.ImportantClasses.ServiceGenerator
import com.MANUL.Bomes.R
import com.MANUL.Bomes.domain.SimpleObjects.CreatingChatUser
import com.MANUL.Bomes.domain.SimpleObjects.UniversalJSONObject
import com.MANUL.Bomes.domain.SimpleObjects.UserData
import com.MANUL.Bomes.data.webSocket.BoMesWebSocket
import com.MANUL.Bomes.data.webSocket.BoMesWebSocketListener
import com.MANUL.Bomes.domain.Utils.FileUtils
import com.MANUL.Bomes.domain.Utils.PermissionUtils
import com.MANUL.Bomes.domain.Utils.RequestCreationFactory
import com.MANUL.Bomes.domain.Utils.RequestEvent
import com.MANUL.Bomes.presentation.createChat.CreatingChatRequestHandler
import com.MANUL.Bomes.presentation.createChat.CreatingChatViewModel
import com.fasterxml.jackson.databind.ObjectMapper
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.util.Calendar


class CreatingChatActivity : AppCompatActivity() {
    private var viewModel: CreatingChatViewModel? = null
    private val webSocket by lazy{
        BoMesWebSocket.get()
    }
    private lateinit var requestHandler: CreatingChatRequestHandler

    private var pathImage: String = ""

    private val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val uri: Uri? = result.data?.data

                if (uri != null) {
                    uploadAvatar(uri)
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val objectMapper = ObjectMapper()
        viewModel = CreatingChatViewModel(layoutInflater, this)
        setContentView(viewModel?.binding?.root)

        requestHandler =
            CreatingChatRequestHandler(this, viewModel!!, pathImage)
        BoMesWebSocketListener.get().setRequestHandler(requestHandler)

        val obj = RequestCreationFactory.create(RequestEvent.GetFriends)
        webSocket?.send(objectMapper.writeValueAsString(obj))

        viewModel?.binding?.apply {
            createChatAvatar.setOnClickListener {
                getStoragePermission()
                val mediaPickerIntent = Intent(Intent.ACTION_PICK)
                mediaPickerIntent.setType("image/*")
                startForResult.launch(mediaPickerIntent)
            }

            buttonCreatingChat.setOnClickListener {
                val addedUserList = viewModel?.getUserAddedListForCreateChat()
                val request = addedUserList?.let { it1 -> requestCreateChatForm(it1) }
                if (request != null) {
                    webSocket.send(request)
                }
                else{
                    Log.e("WEBSOCKET ERROR!", "WEBSOCKET ERROR!");
                }
            }
        }
    }

    private fun getStoragePermission() {
        if (PermissionUtils.hasPermissions(this)) return
        PermissionUtils.requestPermissions(this, 101)
    }

    private fun uploadAvatar(fileUri: Uri) {
        val service = ServiceGenerator.createService(
            FileUploadService::class.java
        )

        val file = FileUtils.getFile(this, fileUri)
        val type = contentResolver?.getType(fileUri)
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
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    try {
                        processingPhotoUploadRequest(response)
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

    private fun processingPhotoUploadRequest(response: Response<ResponseBody>) {
        val objectMapper = ObjectMapper()
        val reply = response.body()!!.string()
        val obj: UniversalJSONObject = objectMapper.readValue(
            reply,
            UniversalJSONObject::class.java
        )
        pathImage = obj.filePath
        viewModel?.insertingImage(obj)
    }

    private fun requestCreateChatForm(addedUserList: MutableList<CreatingChatUser>): String {
        val objectMapper by lazy { ObjectMapper() }
        val addingUsers: Array<String?> = arrayOfNulls(addedUserList.size + 1)
        var tableName = Calendar.getInstance().time.toString()
        for (i in 0 until addedUserList.size) {
            tableName += "-" + addedUserList[i].user.identifier
            addingUsers[i] = (addedUserList[i].user.identifier)
        }
        addingUsers[addedUserList.size] = UserData.identifier
        tableName += "-" + UserData.identifier
        tableName = UserPageActivity.md5(tableName)

        val creatingChat = RequestCreationFactory.create(
            RequestEvent.CreateChat,
            tableName,
            addingUsers,
            viewModel?.binding?.createChatEditText?.text.toString(),
            0,
            pathImage
        )
        return objectMapper.writeValueAsString(creatingChat)
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
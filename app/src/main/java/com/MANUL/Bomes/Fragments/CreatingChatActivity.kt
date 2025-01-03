package com.MANUL.Bomes.Fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.MANUL.Bomes.Activities.UserPageActivity
import com.MANUL.Bomes.ImportantClasses.FileUploadService
import com.MANUL.Bomes.ImportantClasses.ServiceGenerator
import com.MANUL.Bomes.SimpleObjects.CreatingChatUser
import com.MANUL.Bomes.SimpleObjects.UniversalJSONObject
import com.MANUL.Bomes.SimpleObjects.UserData
import com.MANUL.Bomes.Utils.BoMesWebSocketListener
import com.MANUL.Bomes.Utils.FileUtils
import com.MANUL.Bomes.Utils.NowRequest
import com.MANUL.Bomes.Utils.PermissionUtils
import com.MANUL.Bomes.Utils.RequestCreationFactory
import com.MANUL.Bomes.Utils.RequestEvent
import com.MANUL.Bomes.presentation.createChat.CreatingChatRequestHandler
import com.MANUL.Bomes.presentation.createChat.CreatingChatViewModel
import com.fasterxml.jackson.databind.ObjectMapper
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.ResponseBody
import okhttp3.WebSocket
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.util.Calendar


class CreatingChatActivity : AppCompatActivity() {

    private lateinit var webSocketListener: BoMesWebSocketListener
    private lateinit var viewModel: CreatingChatViewModel
    private val okHttpClient by lazy {
        OkHttpClient()
    }
    private var webSocket: WebSocket? = null
    private lateinit var requestHandler: CreatingChatRequestHandler

    private var pathImage: String = ""
    private val userAddList: MutableList<CreatingChatUser> = mutableListOf()

    val startForResult =
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
        viewModel = CreatingChatViewModel(layoutInflater, this)
        setContentView(viewModel.binding.root)

        requestHandler = CreatingChatRequestHandler(this, viewModel, userAddList, pathImage)
        webSocketListener = BoMesWebSocketListener(requestHandler)
        webSocket = okHttpClient.newWebSocket(NowRequest, webSocketListener)

        viewModel.binding.apply {
            createChatAvatar.setOnClickListener {
                getStoragePermission()
                val mediaPickerIntent = Intent(Intent.ACTION_PICK)
                mediaPickerIntent.setType("image/*")
                startForResult.launch(mediaPickerIntent)
            }

            buttonCreatingChat.setOnClickListener {
                val addedUserList = viewModel.getUserAddedListForCreateChat()
                val request = addedUserList?.let { it1 -> requestCreateChatForm(it1) }
                if (request != null) {
                    webSocket!!.send(request)
                }
            }
        }
    }

    private fun getStoragePermission() {
        if (PermissionUtils.hasPermissions(this)) return
        PermissionUtils.requestPermissions(this, 101)
    }


    override fun onDestroy() {
        super.onDestroy()
        webSocket?.close(1000, null)
    }

    fun uploadAvatar(fileUri: Uri) {
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
        val obj: UniversalJSONObject = objectMapper.readValue<UniversalJSONObject>(
            reply,
            UniversalJSONObject::class.java
        )
        pathImage = obj.filePath
        viewModel.insertingImage(obj)
    }

    fun requestCreateChatForm(addedUserList: MutableList<CreatingChatUser>): String {
        val objectMapper by lazy { ObjectMapper() }
        val addingUsers: Array<String?> = arrayOfNulls<String>(addedUserList.size + 1)
        var tableName = Calendar.getInstance().time.toString()
        for (i in 0 until addedUserList.size) {
            tableName += "-" + addedUserList[i].user.identifier
            addingUsers[i] = (addedUserList[i].user.identifier)
        }
        addingUsers[addedUserList.size] = UserData.identifier
        tableName += "-" + UserData.identifier
        tableName = UserPageActivity.md5(tableName)
        //Log.e("requestCreateChatForm", tableName)

        val creatingChat = RequestCreationFactory.create(
            RequestEvent.CreateChat,
            tableName,
            addingUsers,
            viewModel.binding.createChatEditText.text.toString(),
            0,
            pathImage
        )
        return objectMapper.writeValueAsString(creatingChat)
    }

}
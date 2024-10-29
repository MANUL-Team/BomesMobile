package com.MANUL.Bomes.Fragments

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.MANUL.Bomes.ImportantClasses.FileUploadService
import com.MANUL.Bomes.ImportantClasses.ServiceGenerator
import com.MANUL.Bomes.SimpleObjects.UniversalJSONObject
import com.MANUL.Bomes.SimpleObjects.UserData
import com.MANUL.Bomes.Utils.FileUtils
import com.MANUL.Bomes.presentation.createChat.CreatingChatViewModel
import com.MANUL.Bomes.presentation.createChat.CreatingChatWebSocketListener
import com.bumptech.glide.Glide
import com.fasterxml.jackson.databind.ObjectMapper
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.ResponseBody
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException


class CreatingChatFragment : Fragment() {

    private lateinit var webSocketListener: CreatingChatWebSocketListener
    private lateinit var viewModel: CreatingChatViewModel
    private val okHttpClient = OkHttpClient()
    private var webSocket: WebSocket? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        viewModel = ViewModelProvider(this)[CreatingChatViewModel::class.java]

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = CreatingChatViewModel(inflater, activity)

        webSocketListener = CreatingChatWebSocketListener(viewModel){obj ->
            activity?.runOnUiThread{
                run {
                    if (obj.event == "ReturnFriends") {
                        viewModel.responseReturnFriends(obj)
                    }
                    else if (obj.event == "ChatCreated") {
                        Log.e("obj.event", "ChatCreated")

                    }
                }
            }
        }
        webSocket = okHttpClient.newWebSocket(createRequest(), webSocketListener)

        return viewModel.binding.root
    }


    override fun onDestroy() {
        super.onDestroy()
        webSocket?.close(1000, null)
    }

    private fun createRequest(): Request {
        val webSocketUrl = "wss://bomes.ru:8000"
        return Request.Builder()
            .url(webSocketUrl)
            .build()
    }

    fun uploadAvatar(fileUri: Uri) {
        val service = ServiceGenerator.createService(
            FileUploadService::class.java
        )

        val file = FileUtils.getFile(activity, fileUri)
        val type = activity?.contentResolver?.getType(fileUri)
        val requestFile = RequestBody.create(
            type?.toMediaTypeOrNull(),
            file
        )

        val body: MultipartBody.Part = MultipartBody.Part.createFormData("file", file.name, requestFile)

        val descriptionString = "file"
        val description = RequestBody.create(MultipartBody.FORM, descriptionString)

        val call = service.avatar(description, body)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    try {
                        val reply = response.body()!!.string()
                        val obj: UniversalJSONObject = objectMapper.readValue<UniversalJSONObject>(
                            reply,
                            UniversalJSONObject::class.java
                        )

                        Glide.with(activity!!).load("https://bomes.ru/" + obj.filePath).into(binding.createChatAvatar)

                        val updAvatar = UniversalJSONObject()
                        updAvatar.table = "users"
                        updAvatar.column = "identifier"
                        updAvatar.where = UserData.identifier
                        updAvatar.variable = "avatar"
                        updAvatar.value = obj.filePath
                        updAvatar.event = "UpdateValue"

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

}
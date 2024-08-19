package com.MANUL.Bomes;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface FileUploadService {
    @Multipart
    @POST("upload")
    Call<ResponseBody> upload(
            @Part("description") RequestBody description,
            @Part MultipartBody.Part file
    );

    @Multipart
    @POST("avatar")
    Call<ResponseBody> avatar(
            @Part("description") RequestBody description,
            @Part MultipartBody.Part file
    );
    @FormUrlEncoded
    Call<Object> getFile(@Field("fileName") String fileName,
                           @Field("filePath") String filePath);
}
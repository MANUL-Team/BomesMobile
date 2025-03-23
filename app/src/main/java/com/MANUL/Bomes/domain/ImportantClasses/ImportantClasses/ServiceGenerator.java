package com.MANUL.Bomes.domain.ImportantClasses.ImportantClasses;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

public class ServiceGenerator {

    private static final String BASE_URL = "https://bomes.ru:5000/";

    private static Retrofit.Builder builder =
            new Retrofit.Builder()
                    .baseUrl(BASE_URL);

    private static Retrofit retrofit = builder.build();

    private static OkHttpClient.Builder httpClient =
            new OkHttpClient.Builder();

    public static <S> S createService(
            Class<S> serviceClass) {
        return retrofit.create(serviceClass);
    }
}
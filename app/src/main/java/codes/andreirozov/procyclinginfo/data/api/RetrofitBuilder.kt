package codes.andreirozov.procyclinginfo.data.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object RetrofitBuilder {
    private const val BASE_URL = "https://api.andreirozov.codes/uciapi/"

    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
    }

    val apiService: ApiService by lazy {
        getRetrofit().create(ApiService::class.java)
    }
}
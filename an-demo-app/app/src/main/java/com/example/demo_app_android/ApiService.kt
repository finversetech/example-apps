package com.example.demo_app_android

import android.util.Log
import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

data class LoginCredential(
        @SerializedName("username") val username: String?,
        @SerializedName("password") val password: String?,
)

data class LoginResponse(
        @SerializedName("accessToken") val accessToken: String?,
        @SerializedName("tokenType") val tokenType: String?,
        @SerializedName("expiresIn") val expiresIn: Int?
)

// Cut down version of full LIID Object
data class LIIDData(
        @SerializedName("login_identity_id") val liid: String?,
)

// Cut down version of full institution Object
data class InstitutionData(
        @SerializedName("institution_name") val name: String?,
)

data class LIIDWrapper(
        @SerializedName("institution") val institution: InstitutionData?,
        @SerializedName("login_identity") val liid: LIIDData?,
)

interface ApiService {
    @Headers("Content-Type: application/json")
    @POST("/login")
    fun login(@Body credential: LoginCredential): Call<LoginResponse>

    @Headers("Content-Type: application/json")
    @GET("/link")
    fun getLinkURL(@Header("Authorization") bearerToken: String?): Call<String>

    @Headers("Content-Type: application/json")
    @GET("/login-identity")
    fun getLIID(@Header("Authorization") bearerToken: String?): Call<List<LIIDWrapper>>
}

private var retrofit =
        Retrofit.Builder()
                .baseUrl("https://demo-api.dev2.finverse.net")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

var service: ApiService = retrofit.create(ApiService::class.java)

fun login(cred: LoginCredential, onResult: (String?) -> Unit) {
    service.login(cred)
            .enqueue(
                    object : Callback<LoginResponse> {
                        override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                            Log.e("login", "failed", t)
                            onResult(null)
                        }

                        override fun onResponse(
                                call: Call<LoginResponse>,
                                response: Response<LoginResponse>
                        ) {
                            val body = response.body()
                            if (body != null) {
                                onResult(body.accessToken)
                            }
                            onResult(null)
                        }
                    }
            )
}

fun getLinkUrl(accessToken: String, onResult: (String?) -> Unit) {
    service.getLinkURL("Bearer $accessToken")
            .enqueue(
                    object : Callback<String> {
                        override fun onFailure(call: Call<String>, t: Throwable) {
                            Log.e("getLink", "failed", t)
                            onResult(null)
                        }

                        override fun onResponse(call: Call<String>, response: Response<String>) {
                            val body = response.body()
                            Log.d("getLink", "url: $body")
                            if (body != null) {
                                onResult(body)
                            }
                            onResult(null)
                        }
                    }
            )
}

fun fetchUser(accessToken: String, onResult: (LIIDWrapper?) -> Unit) {
    service.getLIID("Bearer $accessToken")
            .enqueue(
                    object : Callback<List<LIIDWrapper>> {
                        override fun onFailure(call: Call<List<LIIDWrapper>>, t: Throwable) {
                            Log.e("getLink", "failed", t)
                            onResult(null)
                        }

                        override fun onResponse(
                                call: Call<List<LIIDWrapper>>,
                                response: Response<List<LIIDWrapper>>
                        ) {
                            val body = response.body()
                            Log.d("getLink", "url: $body")
                            if (body != null) {
                                onResult(body[0])
                            }
                            onResult(null)
                        }
                    }
            )
}

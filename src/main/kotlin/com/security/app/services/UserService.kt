package com.security.app.services

import com.nimbusds.jose.shaded.gson.Gson
import com.security.app.model.Message
import com.security.app.model.UserModel
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient

@Service
class UserService(
    private val webClient: WebClient,
) {
    private final val PROFILE_SERVICE_URL = System.getenv("PROFILE_SERVICE_URL")
    fun getUserInformation(tokenString: String): UserModel? {
        webClient.get()
            .uri("$PROFILE_SERVICE_URL/me")
            .headers { headers ->
                headers.set("Authorization", "Bearer $tokenString")
            }
            .retrieve()
            .bodyToMono(Message.Success::class.java)
            .block()
            ?.let {
                val gson = Gson()
                return gson.fromJson(gson.toJson(it.data), UserModel::class.java)
            }
        return null
    }
}
package com.security.app.request

import com.security.app.entities.Subscription

data class UpdateUserProfileRequest(
    val type: String,
    val userId : String,
    val subscription: Subscription?
)
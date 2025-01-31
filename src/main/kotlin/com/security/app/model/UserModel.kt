package com.security.app.model

import java.util.*

class UserModel {
    var userId: UUID = UUID.randomUUID()

    var email: String = ""

    var username: String = ""

    var googleId: String? = null

    var facebookId: String? = null

    var phoneNumber: String? = null
}
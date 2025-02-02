package com.security.app.services

import com.security.app.utils.JwtTokenUtils
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService

class JwtUserDetailService(
    private val jwtTokenUtils: JwtTokenUtils
) : UserDetailsService {
    override fun loadUserByUsername(username: String): UserDetails {
        val user = jwtTokenUtils.getUserId(username)
            ?: throw Exception("User not found")

        return org.springframework.security.core.userdetails.User
            .withUsername(user)
            .password("")
            .build()
    }
}
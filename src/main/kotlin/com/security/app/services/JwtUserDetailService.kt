package com.security.app.services

import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService

class JwtUserDetailService(
    private val userService: UserService
) : UserDetailsService {
    override fun loadUserByUsername(username: String): UserDetails {
        val user = userService.getUserInformation(username)
            ?: throw Exception("User not found")

        return org.springframework.security.core.userdetails.User
            .withUsername(user.userId.toString())
            .password("")
            .build()
    }
}
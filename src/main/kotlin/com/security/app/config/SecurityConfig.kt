package com.security.app.config

import com.security.app.filters.JwtAuthorizationFilter
import com.security.app.services.JwtUserDetailService
import com.security.app.utils.JwtTokenUtils
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.password.NoOpPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.reactive.function.client.WebClient

@Configuration
@EnableWebSecurity
class SecurityConfig {
    @Bean
    fun userDetailsService(jwtTokenUtils: JwtTokenUtils): UserDetailsService =
        JwtUserDetailService(jwtTokenUtils)


    @Bean
    fun authenticationProvider(jwtTokenUtils: JwtTokenUtils): AuthenticationProvider =
        DaoAuthenticationProvider()
            .also {
                it.setUserDetailsService(userDetailsService(jwtTokenUtils))
                it.setPasswordEncoder(passwordEncoder())
            }


    @Bean
    fun passwordEncoder(): PasswordEncoder = NoOpPasswordEncoder.getInstance()

    @Bean
    fun securityFilterChain(
        http: HttpSecurity,
        jwtAuthenticationFilter: JwtAuthorizationFilter,
        authenticationProvider: AuthenticationProvider
    ): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .cors{ it.disable() }
            .authorizeHttpRequests {
                it.requestMatchers("/api/v1/auth/**", "/error", "/api/v1/payments/confirmation", "/api/v1/payments/returnUrl").permitAll()
                it.anyRequest().authenticated()
            }
            .sessionManagement {
                it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }
            .authenticationProvider(authenticationProvider)
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)
        return http.build()
    }

    @Bean
    fun webClient(builder: WebClient.Builder): WebClient {
        return builder.build()
    }
}

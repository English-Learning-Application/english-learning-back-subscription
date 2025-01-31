package com.security.app.controllers

import com.security.app.model.Message
import com.security.app.request.PaymentRequest
import com.security.app.services.PaymentService
import com.security.app.utils.JwtTokenUtils
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/payments")
class PaymentController(
    private val paymentService: PaymentService,
    private val jwtTokenUtils: JwtTokenUtils
) {
    @GetMapping("/create-payment")
    fun createPayment(request: HttpServletRequest, @RequestBody paymentRequest: PaymentRequest ) : ResponseEntity<Message<String>> {
        val ipAddress = getClientIpAddress(request)

        val tokenString = request.getHeader("Authorization").removePrefix("Bearer ")
        val userId = jwtTokenUtils.getUserId(tokenString)?: return ResponseEntity.badRequest().body(Message.BadRequest("Invalid token"))

        val paymentUrl = paymentService.createVnPaymentUrl(ipAddress, paymentRequest.subscriptionId, userId)

        if(paymentUrl == null) {
            return ResponseEntity.badRequest().body(Message.BadRequest("Subscription not found"))
        }

        return ResponseEntity.ok(Message.Success("Payment created successfully", paymentUrl))
    }

    @GetMapping("/confirmation")
    fun confirmPayment(request: HttpServletRequest) : ResponseEntity<Map<String, *>> {
        val vnpParamNames = request.parameterNames

        val resp = paymentService.validatePayment(request, vnpParamNames)

        return ResponseEntity.ok(resp)
    }

    @GetMapping("/returnUrl")
    fun returnUrl(request: HttpServletRequest) : ResponseEntity<Map<String, *>> {
        val vnpParamNames = request.parameterNames

        val resp = paymentService.validatePayment(request, vnpParamNames)

        return ResponseEntity.ok(resp)
    }

    private fun getClientIpAddress(request: HttpServletRequest): String {
        val headers = listOf(
            "X-Forwarded-For", "Proxy-Client-IP", "WL-Proxy-Client-IP",
            "HTTP_X_FORWARDED_FOR", "HTTP_X_FORWARDED", "HTTP_X_CLUSTER_CLIENT_IP",
            "HTTP_CLIENT_IP", "HTTP_FORWARDED_FOR", "HTTP_FORWARDED", "REMOTE_ADDR"
        )

        for (header in headers) {
            val ip = request.getHeader(header)
            if (!ip.isNullOrEmpty() && ip != "unknown") {
                return ip.split(",")[0] // In case of multiple IPs, take the first one
            }
        }
        return request.remoteAddr ?: "Unknown"
    }

}
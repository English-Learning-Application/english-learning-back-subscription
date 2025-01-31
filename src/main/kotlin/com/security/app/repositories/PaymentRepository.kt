package com.security.app.repositories

import com.security.app.entities.Payment
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface PaymentRepository : JpaRepository<Payment, String> {
    fun findByPaymentId(paymentId: String): Payment?
}
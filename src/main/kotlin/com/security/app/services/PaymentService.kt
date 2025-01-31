package com.security.app.services

import com.security.app.entities.Payment
import com.security.app.model.PaymentStatus
import com.security.app.repositories.PaymentRepository
import com.security.app.repositories.SubscriptionRepository
import com.security.app.utils.VNPayUtils
import com.security.app.utils.toUUID
import jakarta.servlet.http.HttpServletRequest
import org.springframework.stereotype.Service
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.*


@Service
class PaymentService(
    private val vnPayUtils: VNPayUtils,
    private val subscriptionRepository: SubscriptionRepository,
    private val paymentRepository: PaymentRepository
) {
    fun createVnPaymentUrl(
        ipAddress: String,
        subscriptionId: String,
        userId : String
    ) : String? {
        val subscription = subscriptionRepository.findBySubscriptionId(subscriptionId.toUUID())
            ?: return null

        val paymentUrl = vnPayUtils.createVnPaymentUrl(subscription.subscriptionPrice, ipAddress)
        val payment = Payment().let {
            it.paymentId = paymentUrl.second
            it.subscription = subscription
            it.paymentAmount = subscription.subscriptionPrice
            it.userId = userId.toUUID()
            it.secureHashed = paymentUrl.third
            it
        }

        paymentRepository.save(payment)

        return paymentUrl.first
    }

    fun validatePayment(
        vnpResponse: HttpServletRequest,
        vnpNames: Enumeration<String>
    ) : Map<String, *> {
        val fields: HashMap<String, String> = HashMap<String, String>()

        while (vnpNames.hasMoreElements()) {
            val name = URLEncoder.encode(vnpNames.nextElement() as String, StandardCharsets.US_ASCII.toString())
            val value = URLEncoder.encode(vnpResponse.getParameter(name), StandardCharsets.US_ASCII.toString())

            if (!value.isNullOrEmpty()) {
                fields[name] = value
            }
        }

        if(fields.containsKey("vnp_SecureHashType")) {
            fields.remove("vnp_SecureHashType")
        }
        if(fields.containsKey("vnp_SecureHash")) {
            fields.remove("vnp_SecureHash")
        }

        val payment = fields["vnp_TxnRef"]?.let { paymentRepository.findByPaymentId(it) }
            ?: return createMessageResponse("Order not Found", "01")

        val vnpAmount = (fields["vnp_Amount"]?.toLong() ?: 0)/100

        if((vnpAmount) != payment.paymentAmount.toLong()) {
            return createMessageResponse("Invalid Amount", "04")
        }

        val transactionStatus = fields["vnp_TransactionStatus"]
        if(transactionStatus != "00") {
            return createMessageResponse("Order already confirmed", "02")
        }

        val responseCode = fields["vnp_ResponseCode"]
        if("00" == responseCode) {
            payment.paymentStatus = PaymentStatus.SUCCESSFUL
            paymentRepository.save(payment)
        }
        else {
            payment.paymentStatus = PaymentStatus.FAILED
            paymentRepository.save(payment)
        }

        return createMessageResponse("Confirm Success", responseCode ?: "00")
    }

    private fun createMessageResponse(message: String, statusCode: String) : Map<String, *> {
        return mapOf("Message" to message, "RspCode" to statusCode)
    }
}
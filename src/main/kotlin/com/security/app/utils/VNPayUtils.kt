package com.security.app.utils

import org.springframework.stereotype.Component
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

@Component
class VNPayUtils {
    private final val VNPAY_TMNCODE = System.getenv("VNPAY_TMNCODE")
    private final val VNPAY_SECRET_KEY = System.getenv("VNPAY_SECRET_KEY")
    private final val vnpPayUrl: String = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html"

    fun createVnPaymentUrl(
        amount: Double,
        ipAddr: String,
    ) : Triple<String, String, String> {
        val vnpVersion = "2.1.0"
        val vnpCommand = "pay"
        val orderType = "other"
        val parsedAmount: Long = (amount * 100).toLong()
        val vnpAmount = parsedAmount.toString()
        val vnpTxnRef = getVnpTxnRef()

        val vnpParams : Map<String, String> = mapOf(
            "vnp_Version" to vnpVersion,
            "vnp_Command" to vnpCommand,
            "vnp_TmnCode" to VNPAY_TMNCODE,
            "vnp_Amount" to vnpAmount,
            "vnp_CurrCode" to "VND",
            "vnp_TxnRef" to vnpTxnRef,
            "vnp_OrderInfo" to "Thanh toan don hang $vnpTxnRef",
            "vnp_OrderType" to orderType,
            "vnp_Locale" to "vn",
            "vnp_ReturnUrl" to "http://k8s-default-appingre-c4549af8b6-365889156.ap-southeast-2.elb.amazonaws.com/api/v1/payments/returnUrl",
            "vnp_IpAddr" to ipAddr,
            "vnp_CreateDate" to getVnpCreateDate(),
            "vnp_ExpireDate" to getVnpExpireDate(),
        )

        val (query, hashData) = buildVnpQueryParams(vnpParams)

        val vnpSecureHash = hmacSHA512(VNPAY_SECRET_KEY, hashData)

        val queryUrl = "$query&vnp_SecureHash=$vnpSecureHash"
        val payUrl = "$vnpPayUrl?$queryUrl"

        return Triple(payUrl, vnpTxnRef, vnpSecureHash)
    }

    private fun getVnpCreateDate(): String {
        val formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss")
        val now = ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")) // GMT+7 timezone
        return now.format(formatter)
    }

    private fun getVnpExpireDate(): String {
        val formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss")
        val now = ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")) // GMT+7 timezone
        return now.plusMinutes(15).format(formatter)
    }

    private fun getVnpTxnRef(): String {
        return "EL_PAY_${UUID.randomUUID()}"
    }

    private fun buildVnpQueryParams(vnpParams: Map<String, String>): Pair<String, String> {
        // Sort the parameter names
        val fieldNames = vnpParams.keys.sorted()

        val hashData = StringBuilder()
        val query = StringBuilder()

        for (i in fieldNames.indices) {
            val fieldName = fieldNames[i]
            val fieldValue = vnpParams[fieldName]

            if (!fieldValue.isNullOrEmpty()) {
                // Build hash data
                hashData.append(fieldName)
                hashData.append('=')
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()))

                // Build query
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()))
                query.append('=')
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()))

                if (i < fieldNames.size - 1) {
                    query.append('&')
                    hashData.append('&')
                }
            }
        }

        return Pair(query.toString(), hashData.toString())
    }

    fun hashAllFields(fields: Map<String, String>) : String {
        val fieldNames = fields.keys.sorted()

        println("FieldNames: $fieldNames")

        val hashData = StringBuilder()
        val query = StringBuilder()

        for (i in fieldNames.indices) {
            val fieldName = fieldNames[i]
            val fieldValue = fields[fieldName]

            if (!fieldValue.isNullOrEmpty()) {
                // Build hash data
                hashData.append(fieldName)
                hashData.append('=')
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()))

                // Build query
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()))
                query.append('=')
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()))

                if (i < fieldNames.size - 1) {
                    query.append('&')
                    hashData.append('&')
                }
            }
        }

        return hmacSHA512(VNPAY_SECRET_KEY, hashData.toString())
    }

    private fun hmacSHA512(key: String?, data: String?): String {
        try {
            if (key == null || data == null) {
                throw NullPointerException()
            }
            val hmac512 = Mac.getInstance("HmacSHA512")
            val hmacKeyBytes = key.toByteArray()
            val secretKey = SecretKeySpec(hmacKeyBytes, "HmacSHA512")
            hmac512.init(secretKey)
            val dataBytes = data.toByteArray(StandardCharsets.UTF_8)
            val result = hmac512.doFinal(dataBytes)
            val sb = java.lang.StringBuilder(2 * result.size)
            for (b in result) {
                sb.append(String.format("%02x", b.toInt() and 0xff))
            }
            return sb.toString()
        } catch (ex: Exception) {
            return ""
        }
    }

}
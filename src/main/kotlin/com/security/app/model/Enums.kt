package com.security.app.model

enum class DurationLength(val serverValue: String) {
    DAY("DAY"),
    WEEK("WEEK"),
    MONTH("MONTH"),
    YEAR("YEAR");

    companion object {
        fun fromServerValue(serverValue: String): DurationLength {
            return when (serverValue) {
                "DAY" -> DAY
                "WEEK" -> WEEK
                "MONTH" -> MONTH
                "YEAR" -> YEAR
                else -> throw IllegalArgumentException("Invalid value $serverValue")
            }
        }
    }
}

enum class PaymentStatus(val serverValue: String) {
    PENDING("PENDING"),
    SUCCESSFUL("SUCCESSFUL"),
    FAILED("FAILED");

    companion object {
        fun fromServerValue(serverValue: String): PaymentStatus {
            return when (serverValue) {
                "PENDING" -> PENDING
                "SUCCESSFUL" -> SUCCESSFUL
                "FAILED" -> FAILED
                else -> throw IllegalArgumentException("Invalid value $serverValue")
            }
        }
    }
}

enum class UpdateUserProfileType(val serverValue: String){
    SUBSCRIPTION("SUBSCRIPTION"),
    ACHIEVEMENT("ACHIEVEMENT");

    companion object {
        fun fromString(value: String): UpdateUserProfileType {
            return when(value) {
                "SUBSCRIPTION" -> SUBSCRIPTION
                "ACHIEVEMENT" -> ACHIEVEMENT
                else -> throw IllegalArgumentException("Update user profile type not found")
            }
        }
    }
}
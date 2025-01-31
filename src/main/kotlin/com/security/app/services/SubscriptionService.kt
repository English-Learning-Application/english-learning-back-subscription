package com.security.app.services

import com.security.app.entities.Subscription
import com.security.app.repositories.SubscriptionRepository
import org.springframework.stereotype.Service

@Service
class SubscriptionService(
    private val subscriptionRepository: SubscriptionRepository
) {
    fun getAllSubscriptions(): List<Subscription> {
        return subscriptionRepository.findAllByIsEnabled(true)
    }
}
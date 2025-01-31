package com.security.app.repositories

import com.security.app.entities.Subscription
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface SubscriptionRepository : JpaRepository<Subscription, UUID> {
    fun findAllByIsEnabled(enabled: Boolean): List<Subscription>
    fun findBySubscriptionId(subscriptionId: UUID): Subscription?
}
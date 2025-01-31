package com.security.app.controllers

import com.security.app.entities.Subscription
import com.security.app.model.ListMessage
import com.security.app.services.SubscriptionService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/subscriptions")
class SubscriptionController(
    private val subscriptionService: SubscriptionService
) {
    @GetMapping("/all")
    fun getAllSubscriptions() : ResponseEntity<ListMessage<Subscription>>{
        try{
            val subscriptions = subscriptionService.getAllSubscriptions()
            return ResponseEntity.ok(ListMessage.Success("Subscriptions retrieved successfully", subscriptions))
        }
        catch (ex: Exception){
            return ResponseEntity.badRequest().body(ListMessage.BadRequest(ex.message ?: "An error occurred while retrieving subscriptions"))
        }
    }
}
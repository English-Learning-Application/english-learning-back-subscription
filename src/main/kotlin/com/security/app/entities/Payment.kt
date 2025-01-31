package com.security.app.entities

import com.fasterxml.jackson.annotation.JsonIgnore
import com.security.app.model.PaymentStatus
import jakarta.persistence.*
import lombok.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime
import java.util.*

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@ToString
@EntityListeners(AuditingEntityListener::class)
class Payment(
    @Id
    var paymentId: String? = null,
){
    @PrePersist
    fun generateId() {
        if (paymentId == null) {
            paymentId = "EL_PAY_${UUID.randomUUID()}"
        }
    }

    @Column(nullable = false)
    var paymentAmount: Double = 0.0

    @Column(nullable = false)
    var paymentDate: LocalDateTime = LocalDateTime.now()

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    var paymentStatus: PaymentStatus = PaymentStatus.PENDING

    @ManyToOne
    @JoinColumn(name = "subscription_id")
    @JsonIgnore
    var subscription: Subscription? = null

    @Column(nullable = true)
    var userId: UUID? = null

    @Column(nullable = false, columnDefinition = "TEXT DEFAULT ''")
    var secureHashed: String = ""

    @Column(nullable = false, updatable = false)
    @CreatedDate
    var createdAt: LocalDateTime = LocalDateTime.now()

    @Column(nullable = false)
    @LastModifiedDate
    var updatedAt: LocalDateTime = LocalDateTime.now()
}
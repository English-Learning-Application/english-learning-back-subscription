package com.security.app.entities

import com.fasterxml.jackson.annotation.JsonIgnore
import com.security.app.model.DurationLength
import jakarta.persistence.*
import lombok.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@ToString
@EntityListeners(AuditingEntityListener::class)
class Subscription {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    lateinit var subscriptionId: UUID

    @Column(nullable = false)
    var subscriptionName: String = ""

    @Column(nullable = false)
    var subscriptionDescription: String = ""

    @Column(nullable = false)
    var subscriptionPrice: Double = 0.0

    @Column(nullable = false)
    var subscriptionDuration: Int = 0

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    var subscriptionDurationLength: DurationLength = DurationLength.MONTH

    @Column(nullable = false)
    var isEnabled: Boolean = true

    @ManyToMany(fetch = FetchType.LAZY, cascade = [CascadeType.PERSIST])
    @JoinTable(
        name = "subscription_benefits",
        joinColumns = [JoinColumn(name = "subscription_id")],
        inverseJoinColumns = [JoinColumn(name = "benefit_id")]
    )
    var benefits: MutableSet<Benefits> = mutableSetOf()

    @OneToMany(mappedBy = "subscription", fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    @JsonIgnore
    var payments: List<Payment> = mutableListOf()

    @Column(nullable = false, updatable = false)
    @CreatedDate
    var createdAt: LocalDateTime = LocalDateTime.now()

    @Column(nullable = false)
    @LastModifiedDate
    var updatedAt: LocalDateTime = LocalDateTime.now()
}
package com.security.app.entities

import com.fasterxml.jackson.annotation.JsonIgnore
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
class Benefits {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    lateinit var benefitId: UUID

    @Column(nullable = false)
    var benefitName: String = ""

    @Column(nullable = false)
    var benefitDescription: String = ""

    @ManyToMany(mappedBy = "benefits")
    @JsonIgnore
    var subscriptions: MutableSet<Subscription> = mutableSetOf()

    @Column(nullable = false, updatable = false)
    @CreatedDate
    var createdAt: LocalDateTime = LocalDateTime.now()

    @Column(nullable = false)
    @LastModifiedDate
    var updatedAt: LocalDateTime = LocalDateTime.now()
}
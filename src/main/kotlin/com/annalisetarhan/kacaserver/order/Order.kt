package com.annalisetarhan.kacaserver.order

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document
class Order(
        @Id
        val id: String? = null,

        val customerId: String,
        val courierId: String,
        val messageThread: MutableList<String>,

        val itemName: String,
        val itemDescription: String,
        val imageLocation: UUID,
        val deliveryPrice: Float,
        val deliveryLocation: String,

        var currentItemPrice: Float? = null,
        var currentItemImageLocation: UUID? = null,
        var currentItemAccepted: Boolean = false,

        val timeBidAccepted: Long,
        val deliveryTime: Long,

        var totalTimePaused: Long = 0,
        var lastTimePaused: Long? = null,

        var additionalTimeRequested: Long? = null,
        var totalAdditionalTime: Long = 0,

        var deadline: Long              // do I need an isPaused bool?
        )
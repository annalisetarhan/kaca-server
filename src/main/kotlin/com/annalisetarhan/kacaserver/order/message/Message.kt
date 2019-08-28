package com.annalisetarhan.kacaserver.order.message

import org.springframework.data.annotation.Id

data class Message(
        @Id
        val id: String? = null,
        val orderId: String,

        val fromCourier: Boolean,
        val timeSent: Long,
        val message: String,

        var sendToBoth: Boolean = false    // used for auto-generated messages
)
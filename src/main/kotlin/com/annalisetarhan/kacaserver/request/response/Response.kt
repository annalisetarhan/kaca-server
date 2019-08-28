package com.annalisetarhan.kacaserver.request.response

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document
data class Response(
        @Id
        val id: String? = null,

        val bidNotQuestion: Boolean,
        val courierId: String,
        val requestId: String,
        val timeSubmitted: String,

        val deliveryPrice: Float? = null,
        val deliveryTime: String? = null,

        val question: String? = null,
        var answer: String? = null
)
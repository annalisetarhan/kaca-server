package com.annalisetarhan.kacaserver.request

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document
data class Request(
        @Id
        val id: String? = null,

        val customerId: String,
        val customerReputation: Int,    // TBD

        val itemName: String,
        val itemDescription: String,
        val imageLocation: String,
        val deliveryLocation: String,
        val timeSubmitted: String,

        var bidList: MutableList<String>
        )
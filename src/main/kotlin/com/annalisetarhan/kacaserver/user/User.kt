package com.annalisetarhan.kacaserver.user

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document
data class User(
        @Id
        val id: String? = null,

        var phoneNumber: String,
        var displayName: String,
        var password: String,
        var userType: UserType,             // CUSTOMER, COURIER, BOTH
        var customerNotCourier: Boolean,    // Clarifies current status of userType BOTH

        var currentStatus: UserStatus = UserStatus.NEITHER,          // BIDDING, ORDERING, NEITHER
        var currentRequestId: String? = null,           // Only applicable to CUSTOMER status BIDDING
        var courierBidList: List<String>? = null,       // Only applicable to COURIER status BIDDING
        var currentOrderId: String? = null,             // CUSTOMER or COURIER status ORDER

        var paymentInfo: String? = null,
        var paymentHistory: List<String>? = null,

        var customerReputation: Int? = null,
        var customerSuccessfulOrders: Int? = null,
        var customerUnsuccessfulOrders: Int? = null,
        var customerFeedback: List<String>? = null,

        var courierReputation: Int? = null,
        var courierSuccessfulOrders: Int? = null,
        var courierUnsuccessfulOrders: Int? = null,
        var courierFeedback: List<String>? = null
) {
        init {
                if (customerNotCourier) {
                        customerReputation = 0
                        customerSuccessfulOrders = 0
                        customerUnsuccessfulOrders = 0
                        customerFeedback = listOf()
                } else {
                        courierReputation = 0
                        courierSuccessfulOrders = 0
                        courierUnsuccessfulOrders = 0
                        courierFeedback = listOf()
                }
        }
}
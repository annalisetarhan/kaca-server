package com.annalisetarhan.kacaserver.order

import com.annalisetarhan.kacaserver.order.message.Message
import com.annalisetarhan.kacaserver.order.message.MessageRepository
import com.annalisetarhan.kacaserver.request.Request
import com.annalisetarhan.kacaserver.request.response.Response
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*

interface OrderService {
    fun createOrder(bid: Response, request: Request): String
    fun sendMessage(orderId: String, messageMap: Map<String, String>): String
    fun propose(orderId: String, proposalMap: Map<String, String>)
    fun respondToProposal(orderId: String, responseMap: Map<String, String>)
    fun requestMoreTime(orderId: String, time: Long)
    fun respondToTimeRequest(orderId: String, approved: Boolean)
}

@Service("orderService")
class OrderServiceImpl : OrderService {

    @Autowired
    lateinit var orderRepository: OrderRepository
    lateinit var messageRepository: MessageRepository

    override fun createOrder(bid: Response, request: Request): String {
        val customerId = request.customerId
        val courierId = bid.courierId

        val itemName = request.itemName
        val itemDescription = request.itemDescription
        val imageLocation = UUID.fromString(request.imageLocation)
        val deliveryPrice = bid.deliveryPrice ?: error("deliveryPrice not found")
        val deliveryLocation = request.deliveryLocation

        val timeBidAccepted = Date().time
        val deliveryTimeString = bid.deliveryTime ?: error("deliveryTime not found")
        val deliveryTime = deliveryTimeString.toLong()

        val newOrder = Order(
                customerId = customerId,
                courierId = courierId,
                messageThread = mutableListOf(),
                itemName = itemName,
                itemDescription = itemDescription,
                imageLocation = imageLocation,
                deliveryPrice = deliveryPrice,
                deliveryLocation = deliveryLocation,
                timeBidAccepted = timeBidAccepted,
                deliveryTime = deliveryTime,
                deadline = timeBidAccepted + deliveryTime
        )
        orderRepository.save(newOrder)
        return newOrder.id ?: error("orderId not found")
    }

    /*
     * messageMap: fromCourier(t/f), message
     * returns: time received
     */
    override fun sendMessage(orderId: String, messageMap: Map<String, String>): String {
        val order = getOrderFromId(orderId)     // validates orderId
        val fromCourierString = messageMap["fromCourier"] ?: error("fromCourier not found")
        val fromCourierBool = translateToBoolean(fromCourierString)
        val messageString = messageMap["message"] ?: error("message string not found")
        val time = Date().time

        val message = Message(
                orderId = orderId,
                fromCourier = fromCourierBool,
                timeSent = time,
                message = messageString
        )
        messageRepository.save(message)
        val messageId = message.id ?: error("messageId not found")
        order.messageThread.add(messageId)
        return time.toString()
    }

    /*
     * proposalMap: itemPrice, imageLocation
     */
    override fun propose(orderId: String, proposalMap: Map<String, String>) {
        val order = getOrderFromId(orderId)
        val itemPriceString = proposalMap["itemPrice"] ?: error("itemPrice not found")
        val imageLocationString = proposalMap["imageLocation"] ?: error("imageLocation not found")

        pauseTimer(order)
        order.currentItemPrice = itemPriceString.toFloat()
        order.currentItemImageLocation = UUID.fromString(imageLocationString)
    }

    /*
     * responseMap: orderAccepted(t)
     *              orderRejected(f), reason
     */
    override fun respondToProposal(orderId: String, responseMap: Map<String, String>) {
        val order = getOrderFromId(orderId)
        val orderAcceptedString = responseMap["orderAccepted"] ?: error("orderAccepted not found")
        val orderAccepted = translateToBoolean(orderAcceptedString)
        if (orderAccepted) {
            order.currentItemAccepted = true
        } else {
            val reason = responseMap["reason"] ?: error("reason not found")
            addRejectionToMessages(order, reason)
        }
        restartTimer(order)
    }

    private fun addRejectionToMessages(order: Order, reason: String) {
        val rejectionMessage = Message(
                orderId = order.id ?: error("order id not found"),
                fromCourier = false,
                timeSent = Date().time,
                message = reason,
                sendToBoth = true
        )
        messageRepository.save(rejectionMessage)
        val messageId = rejectionMessage.id ?: error("message id not found")
        order.messageThread.add(messageId)
    }

    override fun requestMoreTime(orderId: String, time: Long) {
        val order = getOrderFromId(orderId)
        pauseTimer(order)
        order.additionalTimeRequested = time
    }

    override fun respondToTimeRequest(orderId: String, approved: Boolean) {
        val order = getOrderFromId(orderId)
        if (approved) {
            val timeRequested = order.additionalTimeRequested ?: error("additionalTimeRequested is null")
            order.totalAdditionalTime += timeRequested
        }
        order.additionalTimeRequested = null
        restartTimer(order)
    }

    /*
     *     UTILITY FUNCTIONS
     */

    private fun getOrderFromId(orderId: String): Order {
        val orderHolder = orderRepository.findById(orderId)
        if (orderHolder.isEmpty) {
            error("Order not found")
        }
        return orderHolder.get()
    }

    private fun translateToBoolean(string: String): Boolean {
        return when {
            (string == "true") -> true
            (string == "false") -> false
            else -> error("string not 'true' or 'false'")
        }
    }

    private fun pauseTimer(order: Order) {
        val time = Date().time
        order.lastTimePaused = time
    }

    private fun restartTimer(order: Order) {
        val lastTimePaused = order.lastTimePaused ?: error("timer doesn't appear to have been paused")
        val currentTime = Date().time
        val timePaused = currentTime - lastTimePaused
        order.totalTimePaused += timePaused
        order.lastTimePaused = null
        updateDeadline(order)
    }

    private fun updateDeadline(order: Order) {
        val startTime = order.timeBidAccepted
        val originalDeliveryTime = order.deliveryTime
        val timePaused = order.totalTimePaused
        val additionalTime = order.totalAdditionalTime
        order.deadline = startTime + originalDeliveryTime + timePaused + additionalTime
    }
}
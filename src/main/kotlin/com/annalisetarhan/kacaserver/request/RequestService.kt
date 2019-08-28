package com.annalisetarhan.kacaserver.request

import com.annalisetarhan.kacaserver.order.OrderService
import com.annalisetarhan.kacaserver.request.response.Response
import com.annalisetarhan.kacaserver.request.response.ResponseRepository
import com.annalisetarhan.kacaserver.user.UserService
import com.annalisetarhan.kacaserver.user.UserStatus
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

interface RequestService {
    fun submitRequest(requestMap: Map<String, String>): String
    fun askQuestion(questionMap: Map<String, String>): String
    fun answerQuestion(answerMap: Map<String, String>)
    fun submitBid(bidMap: Map<String, String>): String
    fun acceptBid(bidId: String): String
    fun getRequestId(customerId: String): String
}

@Service("requestService")
class RequestServiceImpl : RequestService {

    @Autowired
    lateinit var requestRepository: RequestRepository
    lateinit var responseRepository: ResponseRepository
    lateinit var userService: UserService
    lateinit var orderService: OrderService

    /*
     * requestMap: customerId, itemName, itemDescription, imageLocation, deliveryLocation, timeSubmitted
     */
    override fun submitRequest(requestMap: Map<String, String>): String {
        val customerId = requestMap["customerId"] ?: error("Missing customerId")

        checkCurrentOrderStatus(customerId)

        val customerReputation = userService.getCustomerReputation(customerId)
        val itemName = requestMap["itemName"] ?: error("Missing itemName")
        val itemDescription = requestMap["itemDescription"] ?: error("Missing itemDescription")
        val imageLocation = requestMap["imageLocation"] ?: error("Missing imageLocation")
        val deliveryLocation = requestMap["deliveryLocation"] ?: error("Missing deliveryLocation")
        val timeSubmitted = requestMap["timeSubmitted"] ?: error("Missing timeSubmitted")

        requestRepository.save(Request(
                customerId = customerId,
                customerReputation = customerReputation,
                itemName = itemName,
                itemDescription = itemDescription,
                imageLocation = imageLocation,
                deliveryLocation = deliveryLocation,
                timeSubmitted = timeSubmitted,
                bidList = mutableListOf()
        ))
        return getRequestId(customerId)
    }

    private fun checkCurrentOrderStatus(customerId: String) {
        if (userService.getCurrentOrderStatus(customerId) != UserStatus.NEITHER) {
            error("User already has open request or order")
        }
    }

    /*
     * requestMap: requestId, courierId, timeSubmitted, question
     */
    override fun askQuestion(questionMap: Map<String, String>): String {
        val requestId = questionMap["requestId"] ?: error("Missing requestId")
        val request = getRequestFromId(requestId)

        val courierId = questionMap["courierId"] ?: error("Missing courierId")
        val timeSubmitted = questionMap["timeSubmitted"] ?: error("Missing timeSubmitted")
        val question = questionMap["question"] ?: error("Missing question")

        val newQuestion = Response(
                bidNotQuestion = false,
                courierId = courierId,
                requestId = requestId,
                timeSubmitted = timeSubmitted,
                question = question
        )
        responseRepository.save(newQuestion)
        val questionId = newQuestion.id ?: error("questionId not found")
        request.bidList.add(questionId)
        return questionId
    }

    /*
     * requestMap: questionId, answer
     */
    override fun answerQuestion(answerMap: Map<String, String>) {
        val questionId = answerMap["questionId"] ?: error("Missing questionId")
        val answer = answerMap["answer"] ?: error("Missing answer")

        val questionHolder = responseRepository.findById(questionId)

        if (questionHolder.isEmpty) {
            error("Question not found")
        }

        val question = questionHolder.get()
        question.answer = answer
    }

    /*
     * requestMap: requestId, courierId, timeSubmitted, deliveryPrice, deliveryTime
     */
    override fun submitBid(bidMap: Map<String, String>): String {
        val requestId = bidMap["requestId"] ?: error("Missing requestId")
        val request = getRequestFromId(requestId)

        val courierId = bidMap["courierId"] ?: error("Missing courierId")
        val timeSubmitted = bidMap["timeSubmitted"] ?: error("Missing timeSubmitted")
        val deliveryPrice = bidMap["deliveryPrice"] ?: error("Missing deliveryPrice")
        val deliveryTime = bidMap["deliveryTime"] ?: error("Missing deliveryTime")

        val bid = Response(
                bidNotQuestion = true,
                courierId = courierId,
                requestId = requestId,
                timeSubmitted = timeSubmitted,
                deliveryPrice = deliveryPrice.toFloat(),
                deliveryTime = deliveryTime
        )
        responseRepository.save(bid)
        return  bid.id ?: error("bidId not found")
    }

    override fun acceptBid(bidId: String): String {
        val bidHolder = responseRepository.findById(bidId)
        if (bidHolder.isEmpty) {
            error("Bid not found")
        }
        val bid = bidHolder.get()
        val requestId = bid.requestId
        val request = getRequestFromId(requestId)
        return orderService.createOrder(bid, request)
    }

    override fun getRequestId(customerId: String): String {
        val requestRecord = requestRepository.findByCustomerId(customerId) ?: error("Request not found")
        return requestRecord.id ?: error("requestId not found")
    }

    private fun getRequestFromId(requestId: String): Request {
        val requestHolder = requestRepository.findById(requestId)
        if (requestHolder.isEmpty) {
            error("Invalid requestId")
        }
        return requestHolder.get()
    }
}
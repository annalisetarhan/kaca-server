package com.annalisetarhan.kacaserver.order

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/order")
class OrderController {

    @Autowired
    lateinit var orderService: OrderService

    @PostMapping("/message/{orderId}")
    fun sendMessage(
            @PathVariable orderId: String,
            @RequestParam messageMap: Map<String, String>
    ): String = orderService.sendMessage(orderId, messageMap)

    @PostMapping("/proposal/{orderId}")
    fun propose(
            @PathVariable orderId: String,
            @RequestParam proposalMap: Map<String, String>
    ) = orderService.propose(orderId, proposalMap)

    @PatchMapping("/proposal/{orderId}")
    fun respondToProposal(
            @PathVariable orderId: String,
            @RequestParam responseMap: Map<String, String>
    ) = orderService.respondToProposal(orderId, responseMap)

    @PostMapping("/time-request/{orderId}")
    fun requestMoreTime(
            @PathVariable orderId: String,
            @RequestParam time: Long
    ) = orderService.requestMoreTime(orderId, time)

    @PatchMapping("/time-request/{orderId}")
    fun respondToTimeRequest(
            @PathVariable orderId: String,
            @RequestParam approved: Boolean
    ) = orderService.respondToTimeRequest(orderId, approved)
}
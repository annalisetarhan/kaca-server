package com.annalisetarhan.kacaserver.request

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/request")
class RequestController {

    @Autowired
    lateinit var requestService: RequestService

    @PostMapping
    fun submitRequest(
            @RequestParam requestMap: Map<String, String>
    ): String = requestService.submitRequest(requestMap)

    @PostMapping("/question")
    fun askQuestion(
            @RequestParam questionMap: Map<String, String>
    ): String = requestService.askQuestion(questionMap)

    @PatchMapping("/question")
    fun answerQuestion(
            @RequestParam answerMap: Map<String, String>
    ) = requestService.answerQuestion(answerMap)

    @PostMapping("/bid")
    fun submitBid(
            @RequestParam bidMap: Map<String, String>
    ): String = requestService.submitBid(bidMap)

    @PatchMapping("/bid/{id}")
    fun acceptBid(
            @PathVariable bidId: String
    ): String = requestService.acceptBid(bidId)
}
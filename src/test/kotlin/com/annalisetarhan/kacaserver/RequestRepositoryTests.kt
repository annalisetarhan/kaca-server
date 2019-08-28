package com.annalisetarhan.kacaserver

import com.annalisetarhan.kacaserver.request.RequestController
import com.annalisetarhan.kacaserver.user.UserController
import com.annalisetarhan.kacaserver.user.UserService
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@SpringBootTest
class RequestRepositoryTests {

    @Autowired
    lateinit var requestController: RequestController

    @Test
    fun testSubmitRequestAskQuestionAnswerQuestion() {

        // Submit request
        val requestId = requestController.submitRequest(
                mapOf(
                        "customerPhoneNumber" to "909090",
                        "customerId" to "0000",
                        "itemName" to "salca",
                        "itemDescription" to "big one",
                        "imageLocation" to "somewhere",
                        "deliveryLocation" to "Korkuteli",
                        "timeSubmitted" to "midnight"
                ))

        // Ask question
        val questionId = requestController.askQuestion(
                mapOf(
                        "requestId" to requestId,
                        "courierId" to "9999",
                        "timeSubmitted" to "00:00",
                        "question" to "why?"
                )
        )

        // Answer question
        requestController.answerQuestion(
                mapOf(
                        "questionId" to questionId,
                        "answer" to "because"
                )
        )
    }
}

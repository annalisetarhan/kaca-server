package com.annalisetarhan.kacaserver.user

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/user")
class UserController {

    @Autowired
    lateinit var userService: UserService

    @PostMapping
    fun userLogin(
            @RequestParam userMap: Map<String, String>
    ): Map<String, String> = userService.userLogin(userMap)

    @PostMapping
    fun addUser(
            @RequestParam userMap: Map<String, String>
    ): String = userService.addUser(userMap)

    @PatchMapping("/courier_session/{id}")
    fun switchToCourier(
            @PathVariable userId: String
    ) = userService.switchToCourier(userId)

    @PatchMapping("/customer_session/{id}")
    fun switchToCustomer(
            @PathVariable userId: String
    ) = userService.switchToCustomer(userId)

    @PatchMapping("/courier_registration/{id}")
    fun registerAsCourier(
            @PathVariable userId: String
    ) = userService.registerAsCourier(userId)

    @PatchMapping("/customer_registration/{id}")
    fun registerAsCustomer(
            @PathVariable userId: String
    ) = userService.registerAsCustomer(userId)
}
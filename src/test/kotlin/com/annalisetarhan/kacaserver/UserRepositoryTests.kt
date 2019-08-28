package com.annalisetarhan.kacaserver

import com.annalisetarhan.kacaserver.user.UserController
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@SpringBootTest
class UserRepositoryTests {

	@Autowired
	lateinit var userController: UserController

	@Test
	fun testAddUserLoginRegisterAndSwitchToCustomer() {

		// Add user
		val requestMap = mapOf(
				"phoneNumber" to "8088088080",
				"displayName" to "mehmet t.",
				"password" to "wouldn't you like to know",
				"userType" to "courier"
		)
		val userId = userController.addUser(requestMap)

		// Login
		val requestMap2 = mapOf(
				"phoneNumber" to "8088088080",
				"password" to "wouldn't you like to know",
				"userType" to "courier"
		)
		val resultMap = userController.userLogin(requestMap2)
		assert(resultMap["displayName"] == "mehmet t.")

		// Register as customer
		userController.registerAsCustomer(userId)

		// Switch to customer
		userController.switchToCustomer(userId)
	}
}

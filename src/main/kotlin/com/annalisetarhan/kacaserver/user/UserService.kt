package com.annalisetarhan.kacaserver.user

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

interface UserService {
    fun userLogin(userMap: Map<String, String>): Map<String, String>
    fun getUserId(phoneNumber: String): String
    fun getCustomerReputation(phoneNumber: String): Int      // This is just internal, right?
    fun addUser(userMap: Map<String, String>): String
    fun switchToCustomer(userId: String)
    fun switchToCourier(userId: String)
    fun registerAsCustomer(userId: String)
    fun registerAsCourier(userId: String)
    fun getCurrentOrderStatus(userId: String): UserStatus
}

@Service
class UserServiceImpl : UserService {

    @Autowired
    lateinit var userRepository: UserRepository

    /*
     * userMap: phoneNumber, password, userType = {"courier", "customer"}
     * returnMap: displayName, userId, currentStatus = "neither"
     *            displayName, userId, currentStatus = "bidding", requestId
     *            displayName, userId, currentStatus = "ordering", orderId
     */
    override fun userLogin(userMap: Map<String, String>): Map<String, String> {
        val returnMap: MutableMap<String, String> = mutableMapOf()
        val userRecord: User

        val phoneNumber = userMap["phoneNumber"] ?:  error("Missing phoneNumber")
        val password = userMap["password"] ?: error("Missing password")
        val userType = userMap["userType"] ?: error("Missing userType")

        userRecord = userRepository.findByPhoneNumber(phoneNumber) ?: error("Phone number not recognized")

        checkPassword(userRecord, password)
        checkUserType(userRecord, userType)

        returnMap["displayName"] = userRecord.displayName
        returnMap["userId"] = userRecord.id.toString()

        when (userRecord.currentStatus) {
            UserStatus.BIDDING -> {
                returnMap["currentStatus"] = "bidding"
                returnMap["requestId"] = userRecord.currentRequestId.toString()
            }
            UserStatus.ORDERING -> {
                returnMap["currentStatus"] = "ordering"
                returnMap["orderId"] = userRecord.currentOrderId.toString()
            }
            UserStatus.NEITHER -> {
                returnMap["currentStatus"] = "neither"
            }
        }
        return returnMap
    }

    private fun checkPassword(userRecord: User, password: String) {
        // TODO: security!
        if (userRecord.password != password) {
            error("Incorrect password")
        }
    }

    private fun checkUserType(userRecord: User, userType: String) {
        when (userRecord.userType) {
            UserType.BOTH -> {
                setCurrentUserType(userRecord, userType)
            }
            UserType.CUSTOMER -> {
                if (userType != "customer") error("Incorrect userType")
            }
            UserType.COURIER -> {
                if (userType != "courier") error("Incorrect userType")
            }
        }
    }

    override fun getUserId(phoneNumber: String): String {
        val userRecord = userRepository.findByPhoneNumber(phoneNumber) ?: error("Customer not found")
        return userRecord.id ?: error("userId not found")
    }

    fun setCurrentUserType(user: User, userType: String) {
        when (userType) {
            "customer" -> {
                checkForOpenCourierOrder(user)
                user.customerNotCourier = true
            }
            "courier" -> {
                checkForOpenCustomerOrder(user)
                user.customerNotCourier = false
            }
            else -> error("Invalid userType")
        }
    }

    private fun checkForOpenCustomerOrder(user: User) {
        if (user.customerNotCourier && user.currentStatus != UserStatus.NEITHER) {
            error("Cannot login as courier with open customer order")
        }
    }

    private fun checkForOpenCourierOrder(user: User) {
        if (!user.customerNotCourier && user.currentStatus != UserStatus.NEITHER) {
            error("Cannot log in as customer with open courier order")
        }
    }

    override fun getCustomerReputation(phoneNumber: String): Int {
        val userRecord = userRepository.findByPhoneNumber(phoneNumber) ?: error("Customer not found")
        return userRecord.customerReputation ?: error("Customer reputation not found")
    }

    /**
     * userMap: phoneNumber, displayName, password, userType
     */
    override fun addUser(userMap: Map<String, String>): String {

        // TODO: validate inputs
        val phoneNumber: String = userMap["phoneNumber"] ?: error("Missing phoneNumber")
        val displayName: String = userMap["displayName"] ?: error("Missing displayName")
        val password: String = userMap["password"] ?: error("Missing password")
        val userTypeString = userMap["userType"] ?: error("Missing userType")
        val userType = validateUserType(userTypeString)
        val customerNotCourier = (userType == UserType.CUSTOMER)

        if (userRepository.findByPhoneNumber(phoneNumber) != null) {
            error("User already exists")
        }

        val newUser = User(
                phoneNumber = phoneNumber,
                displayName = displayName,
                password = password,
                userType = userType,
                customerNotCourier = customerNotCourier
        )
        userRepository.save(newUser)
        return getUserId(phoneNumber)
    }

    fun validateUserType(userType: String): UserType {
        return when (userType) {
            "courier" -> UserType.COURIER
            "customer" -> UserType.CUSTOMER
            else -> error("Invalid userType")
        }
    }

    override fun switchToCourier(userId: String){
        val userRecord = getUserRecord(userId)

        when {
            !userRecord.customerNotCourier -> {
                error("User is already signed in as a courier")
            }
            userRecord.userType == UserType.CUSTOMER -> {
                error("User is not a registered courier")
            }
            userRecord.currentStatus != UserStatus.NEITHER -> {
                error("Cannot login as courier with open customer order")
            }
            else -> {
                userRecord.customerNotCourier = false
                userRepository.save(userRecord)
            }
        }
    }

    override fun switchToCustomer(userId: String){
        val userRecord = getUserRecord(userId)

        when {
            userRecord.customerNotCourier -> {
                error("User is already signed in as a customer")
            }
            userRecord.userType == UserType.COURIER -> {
                error("User is not a registered customer")
            }
            userRecord.currentStatus != UserStatus.NEITHER -> {
                error("Cannot login as a customer with open courier order")
            }
            else -> {
                userRecord.customerNotCourier = true
                userRepository.save(userRecord)
            }
        }
    }

    override fun registerAsCourier(userId: String) {
        val userRecord = getUserRecord(userId)

        if (userRecord.userType != UserType.CUSTOMER) {
            error("User is already a registered courier")
        } else {
            userRecord.userType = UserType.BOTH
            userRecord.courierReputation = 0
            userRecord.courierSuccessfulOrders = 0
            userRecord.courierUnsuccessfulOrders = 0
            userRecord.courierFeedback = mutableListOf()
            userRepository.save(userRecord)
        }
    }

    override fun registerAsCustomer(userId: String){
        val userRecord = getUserRecord(userId)

        if (userRecord.userType != UserType.COURIER) {
            error("User is already a registered customer")
        } else {
            userRecord.userType = UserType.BOTH
            userRecord.customerReputation = 0
            userRecord.customerSuccessfulOrders = 0
            userRecord.customerUnsuccessfulOrders = 0
            userRecord.courierFeedback = mutableListOf()
            userRepository.save(userRecord)
        }
    }

    private fun getUserRecord(userId: String): User {
        val userRecordHolder = userRepository.findById(userId)
        if (userRecordHolder.isEmpty) {
            error("userId not recognized")
        }
        return userRecordHolder.get()
    }

    override fun getCurrentOrderStatus(userId: String): UserStatus {
        val user = getUserRecord(userId)
        return user.currentStatus
    }
}
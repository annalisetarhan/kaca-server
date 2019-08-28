package com.annalisetarhan.kacaserver.user

import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : PagingAndSortingRepository<User, String> {
    fun findByPhoneNumber(phoneNumber: String): User?
}

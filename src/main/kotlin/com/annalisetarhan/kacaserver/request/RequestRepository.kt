package com.annalisetarhan.kacaserver.request

import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface RequestRepository : PagingAndSortingRepository<Request, String> {
    fun findByCustomerId(customerId: String): Request?
}

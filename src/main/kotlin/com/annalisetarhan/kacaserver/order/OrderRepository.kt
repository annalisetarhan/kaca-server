package com.annalisetarhan.kacaserver.order

import org.springframework.data.repository.PagingAndSortingRepository

interface OrderRepository : PagingAndSortingRepository<Order, String> {
}
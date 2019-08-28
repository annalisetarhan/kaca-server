package com.annalisetarhan.kacaserver.order.message

import org.springframework.data.repository.PagingAndSortingRepository

interface MessageRepository : PagingAndSortingRepository<Message, String>
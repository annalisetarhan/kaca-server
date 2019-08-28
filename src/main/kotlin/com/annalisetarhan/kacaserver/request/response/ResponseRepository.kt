package com.annalisetarhan.kacaserver.request.response

import org.springframework.data.repository.PagingAndSortingRepository

interface ResponseRepository : PagingAndSortingRepository<Response, String>
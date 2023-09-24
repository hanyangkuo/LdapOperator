package com.young.ldap.controller

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.test.web.reactive.server.EntityExchangeResult
import org.springframework.web.reactive.function.client.WebClient







class UserControllerTest {

    var webTestClient = WebClient.create()

//    @Test
//    fun whenUserNameIsBaeldung_thenWebFilterIsApplied() {
//        val result: EntityExchangeResult<String> = webTestClient.get()
//            .uri("/users/baeldung")
//            .exchangeToMono()
//            .expectStatus().isOk()
//            .expectBody(String::class.java)
//            .returnResult()
//        assertEquals(result.responseBody, "baeldung")
//        assertEquals(
//            result.responseHeaders.getFirst("web-filter"),
//            "web-filter-test"
//        )
//    }
//
//    @Test
//    fun whenUserNameIsTest_thenHandlerFilterFunctionIsNotApplied() {
//        webTestClient.get().uri("/users/test")
//            .exchange()
//            .expectStatus().isOk()
//    }
}
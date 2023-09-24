package com.young.ldap.security

import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono


@Component
@Order(0)
class SecurityWebFilter : WebFilter {
    override fun filter(
        serverWebExchange: ServerWebExchange,
        webFilterChain: WebFilterChain
    ): Mono<Void> {
        println("SecurityFilter1")
        println(serverWebExchange.request.remoteAddress)
        println(serverWebExchange.request.localAddress)
        var jwtToken = serverWebExchange.request.headers.get("Authorization")
        serverWebExchange.response
            .headers.add("web-filter", "web-filter-test")
        return webFilterChain.filter(serverWebExchange)
    }
}
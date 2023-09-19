package com.young.ldap.controller

import com.young.ldap.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PutMapping
import reactor.core.publisher.Mono
import reactor.core.publisher.MonoSink


@Controller
class UserController {
    @Autowired
    private val userService: UserService?= null


//    @PostMapping("/update")
//    fun updateUser(@RequestBody user: User): Mono<ServerResponse> {
//        return ServerResponse.ok().contentType(MediaType.TEXT_PLAIN)
//            .body(BodyInserters.fromValue("Hello World!"))
//    }

    @PutMapping("/update")
    fun updateEmployee(): Mono<String?> {
        val secondIsValuedSuccess = Mono.create { sink: MonoSink<String?> ->
            sink.success("foo")
            sink.success("bar")
        }
        return secondIsValuedSuccess
    }

}
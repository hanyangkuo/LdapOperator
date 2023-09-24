package com.young.ldap.controller

import com.young.ldap.model.User
import com.young.ldap.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono


@RestController
class UserController {
    @Autowired
    private lateinit var userService: UserService


    @PutMapping("/update")
    @ResponseStatus(HttpStatus.OK)
    fun updateEmployee(@RequestBody user: User): Mono<Unit> {
        return userService.updateUserPassword(user.username!!, user.userPassword!!)
    }

}
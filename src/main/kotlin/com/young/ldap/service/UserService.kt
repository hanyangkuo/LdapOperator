package com.young.ldap.service

import com.young.ldap.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class UserService {

    @Autowired
    private lateinit var userRepository: UserRepository

    fun updateUserPassword(username: String, password: String): Mono<Unit> {
        val user = userRepository.findByUsername(username) ?: return Mono.error(Exception("User Not Found"))
        user.userPassword = password
        userRepository.save(user)
        return Mono.empty()
    }
}
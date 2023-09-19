package com.young.ldap.service

import com.young.ldap.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import javax.net.ssl.SSLEngineResult

@Service
class UserService {
    @Autowired
    private val userRepository: UserRepository?= null

    fun updateUserPassword(username: String, password: String): Boolean {
        val user = userRepository!!.findByUsername("user01")
        if( user != null ){
//            user.userPassword = "Test"
            println("${user.dn}")
            println("${user.username}")
            user.userPassword = password
            userRepository.save(user)
            return true
        }
        return false
    }
}
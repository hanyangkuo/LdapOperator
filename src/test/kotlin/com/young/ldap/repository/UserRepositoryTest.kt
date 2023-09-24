package com.young.ldap.repository

import com.young.ldap.LdapOperatorApplication
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.sql.Date
import java.sql.Time

@ExtendWith(SpringExtension::class)
@SpringBootTest(classes = [LdapOperatorApplication::class])
class UserRepositoryTest {

    @Autowired
    private lateinit var userRepository: UserRepository

    @Test
    fun testFindByUsername(){
        var user = userRepository.findByUsername("user01")
        //            user.userPassword = "Test"
        if (user != null ){
            println("${user.dn}")
            println("${user.username}")
            userRepository.save(user)
        }
        user = userRepository.findByUsername("user01")
        if( user != null ){
            println("${user.dn}")
            println("${user.username}")
            println("${user.surname}")
            println("${user.userPassword}")
        }
    }
}
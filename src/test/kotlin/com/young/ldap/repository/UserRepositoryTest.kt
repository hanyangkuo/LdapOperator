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
    private val userRepository: UserRepository?= null

    @Test
    fun testFindByUsername(){
        var user = userRepository!!.findByUsername("user01")
        if( user != null ){
//            user.userPassword = "Test"
            println("${user.dn}")
            println("${user.username}")
            user.updateDate = "123"
            userRepository.save(user)
        }
        user = userRepository!!.findByUsername("user01")
        if( user != null ){
            println("${user.dn}")
            println("${user.username}")
            println("${user.surname}")
            println("${user.userPassword}")
            println("${user.updateDate}")
        }
    }
}
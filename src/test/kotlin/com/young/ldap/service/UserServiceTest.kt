package com.young.ldap.service

import com.young.ldap.LdapOperatorApplication
import com.young.ldap.repository.UserRepository
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension


@ExtendWith(SpringExtension::class)
@SpringBootTest(classes = [LdapOperatorApplication::class])
class UserServiceTest {

    @Autowired
    private lateinit var userService: UserService

    @Test
    fun testCreate(){
        userService.createLdif()
    }

}
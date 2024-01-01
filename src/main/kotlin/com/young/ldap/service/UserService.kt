package com.young.ldap.service

import com.young.ldap.repository.UserRepository
import com.young.ldap.util.ldif.LdapRecord
import com.young.ldap.util.ldif.LdifManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.ClassPathResource
import org.springframework.ldap.core.LdapTemplate
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.util.logging.Logger


@Service
class UserService {

    private var logger = Logger.getLogger(UserService::javaClass.name)

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var ldapTemplate: LdapTemplate

    fun updateUserPassword(username: String, password: String): Mono<Unit> {
        val user = userRepository.findByUsername(username) ?: return Mono.error(Exception("User Not Found"))
        user.userPassword = password
        userRepository.save(user)
        return Mono.empty()
    }

    fun readLdif() {

        val parser = LdifManager(ClassPathResource("user01.ldif"))
        parser.open()
        while (parser.hasMoreRecords()) {
            val record = parser.getRecord()
            if (record != null) {
                when (record.changerecord) {
                    LdapRecord.CHANGE_ADD -> {
                        logger.info("Find record:\n" +
                                "dn: ${record.getName()}\n" +
                                "changerecord: ${record.changerecord}\n" +
                                "attributes:\n" +
                                "${record.attributes}\n" +
                                "--------------------------------------------------------------")
                    }
                    LdapRecord.CHANGE_MODIFY -> {
                        logger.info("Find record:\n" +
                                "dn: ${record.getName()}\n" +
                                "changerecord: ${record.changerecord}\n" +
                                "modificationItems:\n" +
                                "${record.modificationItems}\n" +
                                "--------------------------------------------------------------")
                    }
                }
            }
        }
    }
}
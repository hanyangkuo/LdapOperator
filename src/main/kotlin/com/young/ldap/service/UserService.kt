package com.young.ldap.service

import com.young.ldap.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.ClassPathResource
import org.springframework.ldap.core.LdapTemplate
import org.springframework.ldap.ldif.parser.LdifParser
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import javax.naming.ldap.LdapName


@Service
class UserService {

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

    fun createLdif() {
        val parser = LdifParser(ClassPathResource("user01.ldif"))
        parser.open()
        while (parser.hasMoreRecords()) {
            val record = parser.record
            val dn: LdapName = record.name
            println(record)
            println(dn)
            ldapTemplate.modifyAttributes(dn, record.all)
        }

        val ldifParser: LdifParser = LdifParser()
        val ldifEntries: List<String> = ldifParser.   .parse(ldifString.toString())

        // Iterate over parsed LDIF entries

        // Iterate over parsed LDIF entries
        for (entry in ldifEntries) {
            // Add entry using LdapTemplate
            ldapTemplate.create(entry)
        }
    }
}
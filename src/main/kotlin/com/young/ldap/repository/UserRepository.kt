package com.young.ldap.repository

import com.young.ldap.model.User
import org.springframework.data.ldap.repository.LdapRepository
import org.springframework.stereotype.Repository


@Repository
interface UserRepository: LdapRepository<User> {
    fun findByUsername(username: String?): User?
}
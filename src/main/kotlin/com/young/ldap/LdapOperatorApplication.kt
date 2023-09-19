package com.young.ldap

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class LdapOperatorApplication

fun main(args: Array<String>) {
	runApplication<LdapOperatorApplication>(*args)
}

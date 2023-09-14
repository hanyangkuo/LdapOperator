package com.young.ldap.ldapOperator.config

import org.springframework.context.annotation.Configuration
import org.springframework.data.ldap.repository.config.EnableLdapRepositories

@Configuration
@EnableLdapRepositories(basePackages = ["com.baeldung.ldap.**"])
class LdapConfig {

}

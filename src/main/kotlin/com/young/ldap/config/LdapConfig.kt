package com.young.ldap.config

import org.springframework.context.annotation.Configuration
import org.springframework.data.ldap.repository.config.EnableLdapRepositories

@Configuration
@EnableLdapRepositories(basePackages = ["com.young.ldap.**"])
class LdapConfig
package com.young.ldap.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.ldap.repository.config.EnableLdapRepositories
import org.springframework.ldap.core.LdapTemplate
import org.springframework.ldap.core.support.LdapContextSource


@Configuration
@EnableLdapRepositories(basePackages = ["com.young.ldap.**"])
class LdapConfig(){

    @Value("\${spring.ldap.urls}")
    private val ldapUrl: String? = null

    @Value("\${spring.ldap.base}")
    private val ldapPartitionSuffix: String? = null

    @Value("\${spring.ldap.username}")
    private val ldapPrincipal: String? = null

    @Value("\${spring.ldap.password}")
    private val ldapPassword: String? = null

    @Bean
    fun contextSource(): LdapContextSource {
        val contextSource = LdapContextSource()
        contextSource.setUrl(ldapUrl)
        contextSource.setBase(ldapPartitionSuffix)
        contextSource.userDn = ldapPrincipal
        contextSource.password = ldapPassword
        return contextSource
    }

    @Bean
    fun ldapTemplate(): LdapTemplate {
        return LdapTemplate(contextSource())
    }
}
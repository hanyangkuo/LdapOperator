package com.young.ldap.ldapOperator.model

import org.springframework.ldap.odm.annotations.Attribute
import org.springframework.ldap.odm.annotations.Entry
import org.springframework.ldap.odm.annotations.Id
import javax.naming.Name

@Entry(base = "ou=users", objectClasses = ["inetOrgPerson","posixAccount","shadowAccount"])
 class User {
    @Id
    private val dn: Name? = null

    @Attribute(name = "uid")
    private val uid: String? = null

    @Attribute(name = "cn")
    private val cn: String? = null

    @Attribute(name = "userPassword")
    val userPassword: String? = null
}
package com.young.ldap.model

import org.springframework.ldap.odm.annotations.Attribute
import org.springframework.ldap.odm.annotations.Entry
import org.springframework.ldap.odm.annotations.Id
import java.util.Date
import javax.naming.Name

@Entry(base = "ou=users", objectClasses = ["inetOrgPerson","posixAccount","shadowAccount"])
 class User {
    @Id
    val dn: Name? = null

    @Attribute(name = "cn")
    val username: String? = null

    @Attribute(name = "sn")
    val surname: String? = null

    @Attribute(name = "userPassword")
    var userPassword: String? = null
}
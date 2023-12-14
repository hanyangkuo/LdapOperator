package com.young.ldap.model

import org.springframework.ldap.core.LdapAttributes


class LdapRecord() {
    var changeRecord: String = "NONE"
    var ldapAttributes: LdapAttributes? = null

}


enum class ChangeType {

}

package com.young.ldap.util.ldif

import org.slf4j.LoggerFactory
import org.springframework.ldap.core.LdapAttributes
import org.springframework.ldap.ldif.InvalidRecordFormatException
import org.springframework.ldap.ldif.support.AttributeValidationPolicy
import org.springframework.ldap.ldif.support.DefaultAttributeValidationPolicy
import org.springframework.ldap.support.LdapUtils
import org.springframework.util.StringUtils
import javax.naming.NamingException
import javax.naming.directory.DirContext
import javax.naming.directory.ModificationItem
import javax.naming.ldap.LdapName

class LdapRecord {
    var changerecord = ""
    var modspec = ""
    var attrvalspec = ""
    var attributes = LdapAttributes()
    var modificationItems = mutableListOf<ModificationItem>()
    private var attributePolicy: AttributeValidationPolicy = DefaultAttributeValidationPolicy()
    var isValid = true
    fun getName(): LdapName {
        return attributes.name
    }
    fun setChangeType(type: String) {
        changerecord = when (type) {
            CHANGE_ADD -> CHANGE_ADD
            CHANGE_DELETE -> CHANGE_DELETE
            CHANGE_MODIFY -> CHANGE_MODIFY
            else -> throw InvalidRecordFormatException("Invalid changetype $type.")
        }
    }
    fun flushModificationItem() {
        LOG.trace("flushModificationItem, {}, {}, {}", modspec, attrvalspec, attributes[attrvalspec])
        when(modspec){
            MODSPEC_ADD -> modificationItems.add(ModificationItem(DirContext.ADD_ATTRIBUTE, attributes[attrvalspec]))
            MODSPEC_DELETE -> modificationItems.add(ModificationItem(DirContext.REMOVE_ATTRIBUTE, attributes[attrvalspec]))
            MODSPEC_REPLACE -> modificationItems.add(ModificationItem(DirContext.REPLACE_ATTRIBUTE, attributes[attrvalspec]))
        }
        modspec = ""
        attrvalspec = ""
    }
    fun addAttributeToRecord(buffer: String) {
        try {
            if (StringUtils.hasLength(buffer)) {
                // Validate previous attribute and add to record.
                val attribute = attributePolicy.parse(buffer)
                if (attribute.id.equals("dn", ignoreCase = true)) {
                    LOG.trace("...adding DN to record.")
                    val dn: String = if (attribute.get() is ByteArray) {
                        String((attribute.get() as ByteArray))
                    } else {
                        attribute.get() as String
                    }
                    attributes.setName(LdapUtils.newLdapName(dn));
                } else {
                    when(changerecord) {
                        CHANGE_ADD -> {
                            val attr = attributes[attribute.id]
                            attr?.add(attribute.get()) ?: attributes.put(attribute)
                        }
                        CHANGE_MODIFY -> {
                            if (modspec == "") {
                                if (buffer.matches(MODSPEC_REGEX.toRegex())){
                                    LOG.trace("set attrvalspec $attrvalspec")
                                    modspec = attribute.id
                                    attrvalspec = attribute.get().toString()
                                    attributes.remove(attrvalspec)
                                } else {
                                    LOG.trace("Cannot find modspec", InvalidRecordFormatException("Invalid modspec $modspec."))
                                    isValid = false
                                }
                            }
                            else if (!StringUtils.endsWithIgnoreCase(attrvalspec, attribute.id)){
                                LOG.trace("Invalid attrvalspec", InvalidRecordFormatException("Invalid attribute require $attrvalspec, get ${attribute.id}."))
                                isValid = false
                            } else {
                                val attr = attributes[attribute.id]
                                attr?.add(attribute.get()) ?: attributes.put(attribute)
                                LOG.trace("add attribute {} = {}", attribute.id, attributes[attribute.id])
                            }
                        }
                        CHANGE_DELETE -> LOG.trace("Attribute will be ignored when change type = delete.")
                        else -> {
                            LOG.trace("Change type not set correct", InvalidRecordFormatException("Invalid change type $changerecord."))
                            isValid = false
                        }
                    }
                }
            }
        } catch (ex: NamingException) {
            LOG.error("Error adding attribute to record", ex)
            isValid = false
        } catch (ex: NoSuchElementException) {
            LOG.error("Error adding attribute to record", ex)
            isValid = false
        }
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(LdifManager::class.java)

        const val CHANGE_ADD = "add"
        const val CHANGE_DELETE = "delete"
        const val CHANGE_MODIFY = "modify"
        private const val MODSPEC_ADD = "add"
        private const val MODSPEC_DELETE = "delete"
        private const val MODSPEC_REPLACE = "replace"
        private const val MODSPEC_REGEX = "^(add|delete|replace):.*"
    }
}
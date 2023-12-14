/*
 * Copyright 2005-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.young.ldap.util

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.InitializingBean
import org.springframework.core.io.FileSystemResource
import org.springframework.core.io.Resource
import org.springframework.ldap.core.LdapAttributes
import org.springframework.ldap.ldif.InvalidRecordFormatException
import org.springframework.ldap.ldif.parser.Parser
import org.springframework.ldap.ldif.support.AttributeValidationPolicy
import org.springframework.ldap.ldif.support.DefaultAttributeValidationPolicy
import org.springframework.ldap.ldif.support.LineIdentifier
import org.springframework.ldap.ldif.support.SeparatorPolicy

import org.springframework.ldap.schema.DefaultSchemaSpecification
import org.springframework.ldap.schema.Specification
import org.springframework.ldap.support.LdapUtils
import org.springframework.util.Assert
import org.springframework.util.StringUtils
import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.io.InputStreamReader
import javax.naming.NamingException

/**
 * The [LdifParser] is the main class of the
 * [org.springframework.ldap.ldif] package. This class reads lines from a resource
 * and assembles them into an [LdapAttributes] object. The
 * [LdifParser] does ignores *changetype* LDIF entries as their
 * usefulness in the context of an application has yet to be determined.
 *
 *
 * **Design**<br></br>
 * [LdifParser] provides the main interface for operation but requires
 * three supporting classes to enable operation:
 *
 *  * [SeparatorPolicy] - establishes the mechanism by which lines
 * are assembled into attributes.
 *  * [AttributeValidationPolicy] - ensures that
 * attributes are correctly structured prior to parsing.
 *  * [Specification] - provides a mechanism by which object
 * structure can be validated after assembly.
 *
 * Together, these 4 classes read from the resource line by line and translate the data
 * into objects for use.
 *
 *
 * **Usage**<br></br>
 * [getRecord()][.getRecord] reads the next available record from the resource.
 * Lines are read and passed to the [SeparatorPolicy] for
 * interpretation. The parser continues to read lines and appends them to the buffer until
 * it encounters the start of a new attribute or an end of record delimiter. When the new
 * attribute or end of record is encountered, the buffer is passed to the
 * [AttributeValidationPolicy] which ensures the buffer
 * conforms to a valid attribute definition as defined in RFC2849 and returns an
 * [LdapAttribute][org.springframework.ldap.core.LdapAttribute] object which is then
 * added to the record, an [LdapAttributes] object. Upon encountering
 * the end of record, the record is validated by the [Specification]
 * policy and, if valid, returned to the requester.
 *
 *
 * *NOTE: By default, objects are not validated. If validation is required, an
 * appropriate specification object must be set.*
 *
 *
 * The parser requires the resource to be [open()][.open] prior to an invocation of
 * [getRecord()][.getRecord]. [hasMoreRecords()][.hasMoreRecords] can be
 * used to loop over the resource until all records have been retrieved. Likewise, the
 * [reset()][.reset] method will reset the resource.
 *
 *
 * Objects implementing the [Attributes][javax.naming.directory.Attributes] interface
 * are required to support a case sensitivity setting which controls whether or not the
 * attribute IDs of the object are case sensitive. The [ caseInsensitive][.caseInsensitive] setting of the [LdifParser] is passed to the
 * constructor of any [Attributes][javax.naming.directory.Attributes] created. The
 * default value for this setting is true so that case insensitive objects are created.
 *
 * @author Keith Barlow
 */
class LdifManager : Parser, InitializingBean {
    /**
     * The resource to parse.
     */
    private var resource: Resource? = null

    /**
     * A BufferedReader to read the file.
     */
    private var reader: BufferedReader? = null

    /**
     * The SeparatorPolicy to use for interpreting attributes from the lines of the
     * resource.
     */
    private var separatorPolicy = SeparatorPolicy()

    /**
     * The AttributeValidationPolicy to use to interpret attributes.
     */
    private var attributePolicy: AttributeValidationPolicy = DefaultAttributeValidationPolicy()

    /**
     * The RecordSpecification for validating records produced.
     */
    private var specification: Specification<LdapAttributes> = DefaultSchemaSpecification()

    /**
     * This setting is used to control the case sensitivity of LdapAttribute objects
     * returned by the parser.
     */
    private var caseInsensitive = true

    /**
     * Default constructor.
     */
    constructor()

    /**
     * Creates a LdifParser with the indicated case sensitivity setting.
     * @param caseInsensitive Case sensitivity setting for LdapAttributes objects returned
     * by the parser.
     */
    constructor(caseInsensitive: Boolean) {
        this.caseInsensitive = caseInsensitive
    }

    /**
     * Creates an LdifParser for the specified resource with the provided case sensitivity
     * setting.
     * @param resource The resource to parse.
     * @param caseInsensitive Case sensitivity setting for LdapAttributes objects returned
     * by the parser.
     */
    constructor(resource: Resource?, caseInsensitive: Boolean) {
        this.resource = resource
        this.caseInsensitive = caseInsensitive
    }

    /**
     * Convenience constructor for resource specification.
     * @param resource The resource to parse.
     */
    constructor(resource: Resource?) {
        this.resource = resource
    }

    /**
     * Convenience constructor: accepts a File object.
     * @param file The file to parse.
     */
    constructor(file: File?) {
        resource = FileSystemResource(file!!)
    }

    /**
     * Set the separator policy.
     *
     * The default separator policy should suffice for most needs.
     * @param separatorPolicy Separator policy.
     */
    fun setSeparatorPolicy(separatorPolicy: SeparatorPolicy) {
        this.separatorPolicy = separatorPolicy
    }

    /**
     * Policy object enforcing the rules for acceptable attributes.
     * @param avPolicy Attribute validation policy.
     */
    fun setAttributeValidationPolicy(avPolicy: AttributeValidationPolicy) {
        attributePolicy = avPolicy
    }

    /**
     * Policy object for enforcing rules to acceptable LDAP objects.
     *
     * This policy may be used to enforce schema restrictions.
     * @param specification
     */
    fun setRecordSpecification(specification: Specification<LdapAttributes>) {
        this.specification = specification
    }

    override fun setResource(resource: Resource) {
        this.resource = resource
    }

    override fun setCaseInsensitive(caseInsensitive: Boolean) {
        this.caseInsensitive = caseInsensitive
    }

    @Throws(IOException::class)
    override fun open() {
        Assert.notNull(resource, "Resource must be set.")
        reader = BufferedReader(InputStreamReader(resource!!.inputStream))
    }

    @Throws(IOException::class)
    override fun isReady(): Boolean {
        return reader!!.ready()
    }

    @Throws(IOException::class)
    override fun close() {
        if (resource!!.isOpen) {
            reader!!.close()
        }
    }

    @Throws(IOException::class)
    override fun reset() {
        Assert.notNull(reader, "A reader has not been obtained.")
        reader!!.reset()
    }

    @Throws(IOException::class)
    override fun hasMoreRecords(): Boolean {
        return reader!!.ready()
    }

    @Throws(IOException::class)
    override fun getRecord(): LdapAttributes? {
        Assert.notNull(reader, "A reader must be obtained: parser not open.")
        if (!reader!!.ready()) {
            LOG.debug("Reader not ready!")
            return null
        }
        var record: LdapAttributes? = null
        var builder: StringBuilder? = StringBuilder()
        var line = reader!!.readLine()
        while (true) {
            val identifier = separatorPolicy.assess(line)
            when (identifier) {
                LineIdentifier.NewRecord -> {
                    LOG.trace("Starting new record.")
                    // Start new record.
                    record = LdapAttributes(caseInsensitive)
                    builder = StringBuilder(line)
                }

                LineIdentifier.Control -> {
                    LOG.trace("'control' encountered.")

                    // Log WARN and discard record.
                    LOG.warn("LDIF change records have no implementation: record will be ignored.")
                    builder = null
                    record = null
                }

                LineIdentifier.ChangeType -> {
                    LOG.trace("'changetype' encountered.")

                    // Log WARN and discard record.
                    LOG.warn("LDIF change records have no implementation: record will be ignored.")

                }

                LineIdentifier.Attribute -> {
                    // flush buffer.
                    addAttributeToRecord(builder.toString(), record)
                    LOG.trace("Starting new attribute.")
                    // Start new attribute.
                    builder = StringBuilder(line)
                }

                LineIdentifier.Continuation -> {
                    LOG.trace("...appending line to buffer.")
                    // Append line to buffer.
                    builder!!.append(line!!.replaceFirst(" ".toRegex(), ""))
                }

                LineIdentifier.EndOfRecord -> {
                    LOG.trace("...done parsing record. (EndOfRecord)")
                    // Validate record and return.
                    return if (record == null) {
                        null
                    } else {
                        try {
                            // flush buffer.
                            addAttributeToRecord(builder.toString(), record)
                            if (specification.isSatisfiedBy(record)) {
                                LOG.debug("record parsed:\n$record")
                                record
                            } else {
                                throw InvalidRecordFormatException(
                                    "Record [dn: " + record.dn + "] does not conform to specification."
                                )
                            }
                        } catch (ex: NamingException) {
                            LOG.error("Error adding attribute to record", ex)
                            null
                        }
                    }
                }
                else -> {
                }
            }
            line = reader!!.readLine()
            if (line == null && record == null) {
                // Never encountered a valid record.
                return null
            }
        }
    }

    private fun addAttributeToRecord(buffer: String, record: LdapAttributes?) {
        try {
            if (StringUtils.hasLength(buffer) && record != null) {
                // Validate previous attribute and add to record.
                val attribute = attributePolicy.parse(buffer)
                if (attribute.id.equals("dn", ignoreCase = true)) {
                    LOG.trace("...adding DN to record.")
                    val dn: String
                    dn = if (attribute.get() is ByteArray) {
                        String((attribute.get() as ByteArray))
                    } else {
                        attribute.get() as String
                    }
                    record.setName(LdapUtils.newLdapName(dn))
                } else {
                    LOG.trace("...adding attribute to record.")
                    val attr = record[attribute.id]
                    attr?.add(attribute.get()) ?: record.put(attribute)
                }
            }
        } catch (ex: NamingException) {
            LOG.error("Error adding attribute to record", ex)
        } catch (ex: NoSuchElementException) {
            LOG.error("Error adding attribute to record", ex)
        }
    }

    @Throws(Exception::class)
    override fun afterPropertiesSet() {
        Assert.notNull(resource, "A resource to parse is required.")
        Assert.isTrue(resource!!.exists(), resource!!.description + ": resource does not exist!")
        Assert.isTrue(resource!!.isReadable, "Resource is not readable.")
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(LdifManager::class.java)
    }
}

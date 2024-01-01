package com.young.ldap.util.ldif.support

import org.slf4j.LoggerFactory
import org.springframework.ldap.ldif.support.SeparatorPolicy
import org.springframework.util.StringUtils
import com.young.ldap.util.ldif.support.LineIdentifier

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
/**
 * Policy object for enforcing LDIF record separation rules. Designed explicitly for use
 * in LdifParser. This default separator policy should really not be required to be
 * replaced but it is modular just in case.
 *
 *
 * This class applies the separation policy prescribed in RFC2849 for LDIF files and
 * identifies the line type from the input.
 *
 * @author Keith Barlow
 */
class SeparatorPolicy {

    private var record = false
    private var skip = false

    /**
     * Assess a read line.
     *
     *
     * In LDIF, lines must adhere to a particular format. A line can only contain one
     * attribute and its value. The value may span multiple lines. Continuation lines are
     * marked by the presence of a single space in the 1st position. Non-continuation
     * lines must start in the first position.
     *
     */
    fun skip() {
        skip = true
    }

    fun assess(line: String): LineIdentifier {
        log.trace("Assessing --> [$line]")
        return if (record) {
            if (!StringUtils.hasLength(line)) {
                record = false
                skip = false
                LineIdentifier.EndOfRecord
            } else if (skip) {
                LineIdentifier.Void
            } else {
                if (line.startsWith(CONTROL)) {
                    skip = true
                    LineIdentifier.Control
                } else if (line.startsWith(CHANGE_TYPE)) {
                    LineIdentifier.ChangeType
                } else if (line.startsWith(COMMENT)) {
                    LineIdentifier.Comment
                } else if (line == DASH) {
                    LineIdentifier.Dash
                }else if (line.startsWith(CONTINUATION)) {
                    LineIdentifier.Continuation
                } else {
                    LineIdentifier.Attribute
                }
            }
        } else {
            if (StringUtils.hasLength(line) && line.matches(VERSION_IDENTIFIER.toRegex()) && !skip) {
                // Version Identifiers are ignored by parser.
                LineIdentifier.VersionIdentifier
            } else if (StringUtils.hasLength(line) && line.matches(NEW_RECORD.toRegex())) {
                record = true
                skip = false
                LineIdentifier.NewRecord
            } else {
                LineIdentifier.Void
            }
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(SeparatorPolicy::class.java)

        /*
	 * Line Identification Patterns.
	 */
        private const val VERSION_IDENTIFIER = "^version: [0-9]+(\\.[0-9]*){0,1}$"
        private const val CONTROL = "control:"
        private const val CHANGE_TYPE = "changetype:"
        private const val CONTINUATION = " "
        private const val DASH = "-"
        private const val COMMENT = "#"
        private const val NEW_RECORD = "^dn:.*$"
    }
}

/*
 * Copyright 2026 Marco Gomiero
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.prof18.secureqrreader.screens

import java.net.URI

internal sealed interface QrContent {
    val raw: String

    data class Url(
        override val raw: String,
        val host: String,
        val openableUrl: String,
    ) : QrContent

    data class Wifi(
        override val raw: String,
        val ssid: String,
        val security: String,
        val password: String?,
    ) : QrContent

    data class ContactInfo(
        override val raw: String,
        val name: String,
        val phone: String?,
        val email: String?,
    ) : QrContent

    data class PlainText(override val raw: String) : QrContent
}

internal fun parseQrContent(rawValue: String): QrContent {
    val value = rawValue.trim()
    parseWifi(value)?.let { return it }
    parseContact(value)?.let { return it }
    parseUrl(value)?.let { return it }
    return QrContent.PlainText(rawValue)
}

private fun parseUrl(value: String): QrContent.Url? {
    if (value.any(Char::isWhitespace)) return null

    val candidate = if (value.startsWith("http://", ignoreCase = true) ||
        value.startsWith("https://", ignoreCase = true)
    ) {
        value
    } else {
        "https://$value"
    }

    val uri = runCatching { URI(candidate) }.getOrNull() ?: return null
    val scheme = uri.scheme?.lowercase()
    val host = uri.host?.takeIf { it.contains('.') } ?: return null
    if (scheme != "http" && scheme != "https") return null

    return QrContent.Url(
        raw = value,
        host = host.removePrefix("www."),
        openableUrl = candidate,
    )
}

private fun parseWifi(value: String): QrContent.Wifi? {
    if (!value.startsWith("WIFI:", ignoreCase = true)) return null

    val fields = splitEscaped(value.substring(5), ';')
        .mapNotNull { field ->
            val separator = indexOfUnescaped(field, ':')
            if (separator < 0) null else {
                field.substring(0, separator).uppercase() to unescape(field.substring(separator + 1))
            }
        }
        .toMap()
    val ssid = fields["S"]?.takeIf(String::isNotBlank) ?: return null
    val security = when (fields["T"]?.uppercase()) {
        null, "", "NOPASS" -> "Open"
        "WPA" -> "WPA2"
        else -> fields.getValue("T")
    }

    return QrContent.Wifi(
        raw = value,
        ssid = ssid,
        security = security,
        password = fields["P"]?.takeIf(String::isNotEmpty),
    )
}

private fun parseContact(value: String): QrContent.ContactInfo? = when {
    value.startsWith("MECARD:", ignoreCase = true) -> parseMeCard(value)
    value.startsWith("BEGIN:VCARD", ignoreCase = true) -> parseVCard(value)
    else -> null
}

private fun parseMeCard(value: String): QrContent.ContactInfo? {
    val fields = splitEscaped(value.substringAfter(':'), ';')
        .mapNotNull { field ->
            val separator = indexOfUnescaped(field, ':')
            if (separator < 0) null else {
                field.substring(0, separator).uppercase() to unescape(field.substring(separator + 1))
            }
        }
        .groupBy({ it.first }, { it.second })
    val name = fields["N"]?.firstOrNull()
        ?.split(',')
        ?.asReversed()
        ?.joinToString(" ")
        ?.trim()
        ?.takeIf(String::isNotBlank)
        ?: return null

    return QrContent.ContactInfo(
        raw = value,
        name = name,
        phone = fields["TEL"]?.firstOrNull()?.takeIf(String::isNotBlank),
        email = fields["EMAIL"]?.firstOrNull()?.takeIf(String::isNotBlank),
    )
}

private fun parseVCard(value: String): QrContent.ContactInfo? {
    val fields = value.lineSequence()
        .map(String::trim)
        .mapNotNull { line ->
            val separator = line.indexOf(':')
            if (separator < 0) null else {
                line.substring(0, separator).substringBefore(';').uppercase() to
                    unescape(line.substring(separator + 1))
            }
        }
        .groupBy({ it.first }, { it.second })
    val name = fields["FN"]?.firstOrNull()?.takeIf(String::isNotBlank)
        ?: fields["N"]?.firstOrNull()
            ?.split(';')
            ?.filter(String::isNotBlank)
            ?.asReversed()
            ?.joinToString(" ")
            ?.takeIf(String::isNotBlank)
        ?: return null

    return QrContent.ContactInfo(
        raw = value,
        name = name,
        phone = fields["TEL"]?.firstOrNull()?.takeIf(String::isNotBlank),
        email = fields["EMAIL"]?.firstOrNull()?.takeIf(String::isNotBlank),
    )
}

private fun splitEscaped(value: String, delimiter: Char): List<String> {
    val result = mutableListOf<String>()
    val current = StringBuilder()
    var escaped = false
    value.forEach { character ->
        when {
            escaped -> {
                current.append('\\')
                current.append(character)
                escaped = false
            }
            character == '\\' -> escaped = true
            character == delimiter -> {
                result += current.toString()
                current.clear()
            }
            else -> current.append(character)
        }
    }
    if (escaped) current.append('\\')
    result += current.toString()
    return result
}

private fun indexOfUnescaped(value: String, character: Char): Int {
    var escaped = false
    value.forEachIndexed { index, current ->
        when {
            escaped -> escaped = false
            current == '\\' -> escaped = true
            current == character -> return index
        }
    }
    return -1
}

private fun unescape(value: String): String {
    val result = StringBuilder()
    var escaped = false
    value.forEach { character ->
        if (escaped) {
            result.append(
                when (character) {
                    'n', 'N' -> '\n'
                    else -> character
                }
            )
            escaped = false
        } else if (character == '\\') {
            escaped = true
        } else {
            result.append(character)
        }
    }
    if (escaped) result.append('\\')
    return result.toString()
}

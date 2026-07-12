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

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class QrContentTest {

    @Test
    fun `parses an https URL and removes www from the displayed host`() {
        val content = assertIs<QrContent.Url>(
            parseQrContent("https://www.marcogomiero.com/about")
        )

        assertEquals("marcogomiero.com", content.host)
        assertEquals("https://www.marcogomiero.com/about", content.openableUrl)
    }

    @Test
    fun `adds https to a URL without a scheme`() {
        val content = assertIs<QrContent.Url>(parseQrContent("marcogomiero.com"))

        assertEquals("https://marcogomiero.com", content.openableUrl)
    }

    @Test
    fun `parses escaped wifi fields`() {
        val content = assertIs<QrContent.Wifi>(
            parseQrContent("WIFI:T:WPA;S:Marco\\;Home;P:secret\\:value;;")
        )

        assertEquals("Marco;Home", content.ssid)
        assertEquals("WPA2", content.security)
        assertEquals("secret:value", content.password)
    }

    @Test
    fun `parses a MECARD contact`() {
        val content = assertIs<QrContent.ContactInfo>(
            parseQrContent("MECARD:N:Gomiero,Marco;TEL:+390123;EMAIL:marco@example.com;;")
        )

        assertEquals("Marco Gomiero", content.name)
        assertEquals("+390123", content.phone)
        assertEquals("marco@example.com", content.email)
    }

    @Test
    fun `parses a vCard contact`() {
        val content = assertIs<QrContent.ContactInfo>(
            parseQrContent(
                "BEGIN:VCARD\nVERSION:3.0\nFN:Marco Gomiero\nTEL;TYPE=CELL:+390123\n" +
                    "EMAIL:marco@example.com\nEND:VCARD"
            )
        )

        assertEquals("Marco Gomiero", content.name)
        assertEquals("+390123", content.phone)
        assertEquals("marco@example.com", content.email)
    }

    @Test
    fun `keeps unknown content as plain text`() {
        assertEquals(QrContent.PlainText("Hello world"), parseQrContent("Hello world"))
    }
}

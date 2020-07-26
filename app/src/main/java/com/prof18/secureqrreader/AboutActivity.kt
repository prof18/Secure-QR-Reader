/*
 * Copyright 2020 Marco Gomiero
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

package com.prof18.secureqrreader

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.mikepenz.aboutlibraries.LibsBuilder

class AboutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_about)

        // Toolbar stuff
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        findViewById<MaterialButton>(R.id.openSourceLicensesButton).setOnClickListener {
            LibsBuilder()
                .withLicenseShown(true)
                .withAboutAppName(getString(R.string.app_name))
                .withActivityTitle("Open Source Libraries")
                .withAboutDescription("<a href='https://it.freepik.com/foto-vettori-gratuito/tecnologia'>Vectors from freepik - it.freepik.com</a>")
                .withEdgeToEdge(true)
                .start(this)
        }

        findViewById<MaterialButton>(R.id.showGithubButton).setOnClickListener {
            val browserIntent =
                Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/prof18/Secure-QR-Reader"))
            startActivity(browserIntent)
        }


        // Author label and link
        val authorTextView =  findViewById<TextView>(R.id.welcome_screen_author)
        authorTextView.visibility = View.VISIBLE


        // Set link
        val completeText = getString(R.string.author_label)
        val link = "Marco Gomiero"
        val spannableStringBuilder = SpannableStringBuilder(completeText)
        spannableStringBuilder.setSpan(object: ClickableSpan() {
            override fun onClick(p0: View) {
                val browserIntent =
                    Intent(Intent.ACTION_VIEW, Uri.parse("https://marcogomiero.com"))
                startActivity(browserIntent)
            }

        }, completeText.indexOf(link),
            completeText.indexOf(link) + link.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        authorTextView.movementMethod = LinkMovementMethod.getInstance()
        authorTextView.text = spannableStringBuilder



        //

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

}


package com.prof18.secureqrreader

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton

class ResultFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_result, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val qrResult = arguments?.getString(QR_RESULT) ?: return
        view.findViewById<TextView>(R.id.qrContent).text = qrResult

        val openButton = view.findViewById<MaterialButton>(R.id.openButton)
        val isUrl = Patterns.WEB_URL.matcher(qrResult).matches();
        if (isUrl) {
            openButton.setOnClickListener {
                val browserIntent =
                    Intent(Intent.ACTION_VIEW, Uri.parse(qrResult))
                startActivity(browserIntent)
            }
        } else {
            openButton.visibility = View.GONE
        }

        view.findViewById<MaterialButton>(R.id.copyButton).setOnClickListener {
            val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip: ClipData = ClipData.newPlainText("QR Result", qrResult)
            clipboard.setPrimaryClip(clip)
        }
        view.findViewById<MaterialButton>(R.id.shareButton).setOnClickListener {
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, qrResult)
                type = "text/plain"
            }

            val shareIntent = Intent.createChooser(sendIntent, null)
            startActivity(shareIntent)

        }
        view.findViewById<MaterialButton>(R.id.scanAgainButton).setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    companion object {
        private const val QR_RESULT = "qr_result"
        const val RESULT_FRAGMENT_TAG = "Result Fragment TAG"

        fun create(qrResult: String): ResultFragment {
            val fragment = ResultFragment()
            val args = bundleOf(
                QR_RESULT to qrResult
            )
            fragment.arguments = args
            return fragment
        }
    }
}
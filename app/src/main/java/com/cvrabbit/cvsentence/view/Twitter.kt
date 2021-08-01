/*
 * Copyright (c) 2020 shinonistone
 *
 * This software is released under the MIT License.
 * https://opensource.org/licenses/mit-license.php
 *
 */

package com.cvrabbit.cvsentence.view

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.cvrabbit.cvsentence.R
import com.cvrabbit.cvsentence.databinding.FragmentTweetEntryDialogBinding
import com.cvrabbit.cvsentence.model.preferences.PreferenceAccess
import com.cvrabbit.cvsentence.util.constant.Constants.CALLBACK_URL
import com.cvrabbit.cvsentence.util.constant.Constants.CONSUMER_KEY
import com.cvrabbit.cvsentence.util.constant.Constants.CONSUMER_SECRET
import com.cvrabbit.cvsentence.util.constant.Constants.GOOGLE_PLAY_LINK
import com.cvrabbit.cvsentence.util.device.SizeMetrics
import com.cvrabbit.cvsentence.viewmodel.MainActivityViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import twitter4j.Twitter
import twitter4j.TwitterException
import twitter4j.TwitterFactory
import twitter4j.conf.ConfigurationBuilder

private const val TAG = "Twitter"

class Twitter : DialogFragment() {

    private var _binding: FragmentTweetEntryDialogBinding? = null
    private val binding
        get() = _binding!!
    private val mainActivityViewModel: MainActivityViewModel by activityViewModels()

    companion object {
        fun newInstance() = Twitter()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)
        _binding = FragmentTweetEntryDialogBinding.inflate(requireActivity().layoutInflater)
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(binding.root)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT)
        dialog.window!!.setDimAmount(0f)
        return dialog
    }

    override fun onStart() {
        super.onStart()
        setVisibility()
        setListeners()
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        _binding = null
    }

    private fun setVisibility() {
        binding.includeAppLink.isChecked = true
        binding.editTweet.setText(makeTweetSentence(), TextView.BufferType.EDITABLE)
    }

    private fun setListeners() {
        binding.tweetEntryBase.setOnClickListener {
            dismiss()
        }
        binding.closeTweetDialog.setOnClickListener {
            dismiss()
        }
        binding.doTweet.setOnClickListener {
            if (PreferenceAccess(requireContext()).getTwitterAccessToken().first == "") {
                getRequestToken()
            } else {
                val tweetContent =
                    binding.editTweet.text.toString() +
                            if(binding.includeAppLink.isChecked){ "\n" + GOOGLE_PLAY_LINK } else { "" }
                tweetIfAccessTokenValid(tweetContent)
            }
        }
        binding.resetTweetConfig.setOnClickListener {
            getRequestToken()
        }
        binding.editTweet.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val i = getHan1Zen2(s.toString())
                val expression = "$i/255"
                binding.byteLength.text = expression
            }
        })
    }

    fun getHan1Zen2(str: String): Int {
        var ret = 0
        val c = str.toCharArray()
        for (i in c.indices) {
            ret += if (c[i].toString().toByteArray().size <= 1) {
                1 //Hankaku 1
            } else {
                2 //Zenkaku 2
            }
        }
        return ret
    }

    private fun makeTweetSentence(): String {
        val wordObject = mainActivityViewModel.focusWord
        val theme = requireContext().getString(R.string.tw_theme)
        val word = requireContext().getString(R.string.tw_word) + wordObject?.word
        val meaning = requireContext().getString(R.string.tw_meaning) + wordObject?.mainMeaning
        val reference = requireContext().getString(R.string.tw_reference) + wordObject?.reference
        return theme + "\n" + word + "\n" + meaning + "\n" + reference
    }

    private fun tweetIfAccessTokenValid(tweetContent: String) {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Default) {
            try {
                tweet(PreferenceAccess(requireContext()).getTwitterAccessToken(), tweetContent)
                val content = requireContext().getText(R.string.tw_success).toString()
                makeToastUsingMainCoroutine(content)
            } catch (te: TwitterException) {
                when (te.errorCode) {
                    64 -> {
                        val content = requireContext().getText(R.string.tw_error_common).toString() +
                                requireContext().getText(R.string.tw_error_64)
                        makeToastUsingMainCoroutine(content)
                    }
                    89 -> {
                        val content = requireContext().getText(R.string.tw_error_common).toString() +
                                requireContext().getText(R.string.tw_error_89)
                        makeToastUsingMainCoroutine(content)
                    }
                    185 -> {
                        val content = requireContext().getText(R.string.tw_error_common).toString() +
                                requireContext().getText(R.string.tw_error_185)
                        makeToastUsingMainCoroutine(content)
                    }
                    186 -> {
                        val content = requireContext().getText(R.string.tw_error_common).toString() +
                                requireContext().getText(R.string.tw_error_186)
                        makeToastUsingMainCoroutine(content)
                    }
                    187 -> {
                        val content = requireContext().getText(R.string.tw_error_common).toString() +
                                requireContext().getText(R.string.tw_error_187)
                        makeToastUsingMainCoroutine(content)
                    }
                    else -> {
                        val content = requireContext().getText(R.string.tw_error_common).toString()
                        makeToastUsingMainCoroutine(content)
                    }
                }
            } catch (e: Exception) {
                val content = requireContext().getText(R.string.tw_error_common).toString()
                makeToastUsingMainCoroutine(content)
            }
        }
    }

    private fun makeToastUsingMainCoroutine(content: String) {
        CoroutineScope(Main).launch {
            Toast.makeText(activity, content, Toast.LENGTH_SHORT).show()
            dismiss()
        }
    }

    private fun tweet(accessToken: Pair<String?, String?>, tweetContent: String) {
        val cb = ConfigurationBuilder()
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey(CONSUMER_KEY)
                .setOAuthConsumerSecret(CONSUMER_SECRET)
                .setOAuthAccessToken(accessToken.first)
                .setOAuthAccessTokenSecret(accessToken.second)
        val tf = TwitterFactory(cb.build())
        val twitter = tf.instance
        twitter.updateStatus(tweetContent)
    }

    lateinit var twitter: Twitter
    private fun getRequestToken(){
        this.lifecycleScope.launch(Dispatchers.Default){

            val builder = ConfigurationBuilder()
                .setDebugEnabled(true)
                .setOAuthConsumerKey(CONSUMER_KEY)
                .setOAuthConsumerSecret(CONSUMER_SECRET)

            val config = builder.build()
            val factory = TwitterFactory(config)

            twitter = factory.instance

            try{
                val requestToken = twitter.oAuthRequestToken
                withContext(Main){
                    setupTwitterWebviewDialog(requestToken.authorizationURL)
                }
            } catch (e: IllegalStateException) {
                Log.e("ERROR: ", e.toString())
            }

        }
    }

    lateinit var twitterDialog: Dialog

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupTwitterWebviewDialog(url: String){
        twitterDialog = Dialog(requireContext())
        val webView = WebView(requireContext())

        webView.isVerticalScrollBarEnabled = false
        webView.isHorizontalScrollBarEnabled = false
        webView.webViewClient = TwitterWebViewClient()
        webView.settings.javaScriptEnabled = true
        webView.loadUrl(url)
        twitterDialog.setContentView(webView)
        twitterDialog.show()

    }

    inner class TwitterWebViewClient : WebViewClient() {
        override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
        ): Boolean {
            if (request?.url.toString().startsWith(CALLBACK_URL)) {
                Log.d("Authorization URL: ", request?.url.toString())
                handleUrl(request?.url.toString())

                if (request?.url.toString().contains(CALLBACK_URL)) {
                    twitterDialog.dismiss()
                }
                return true
            }
            return false
        }

        private fun handleUrl(url: String) {
            val uri = Uri.parse(url)
            val oauthVerifier = uri.getQueryParameter("oauth_verifier") ?: ""
            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Default) {
                val accToken = withContext(Dispatchers.IO) {
                    twitter.getOAuthAccessToken(oauthVerifier)
                }
                PreferenceAccess(requireContext()).saveTwitterAccessToken(accToken.token, accToken.tokenSecret)
                Log.i("token----------", accToken.token)
                Log.i("token secret---", accToken.tokenSecret)
                getUserProfile()
            }
        }

        private suspend fun getUserProfile(){
            val usr = withContext(Dispatchers.IO){ twitter.verifyCredentials() }
            Log.i("twitter", usr.name)
            Log.i("twitter", usr.screenName)
        }
    }
}
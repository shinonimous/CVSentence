/*
 * Copyright (c) 2020 shinonistone
 *
 * This software is released under the MIT License.
 * https://opensource.org/licenses/mit-license.php
 *
 */

package com.cvrabbit.cvsentence.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.cvrabbit.cvsentence.R
import com.cvrabbit.cvsentence.databinding.FragmentAdFrameBinding
import com.cvrabbit.cvsentence.util.constant.Constants.ADMOB_AD_UNIT_ID
import com.google.android.gms.ads.*
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView

private const val TAG = "AdMob"

var currentNativeAd: NativeAd? = null

class AdMob : Fragment() {

    private var _binding: FragmentAdFrameBinding? = null
    private val binding
        get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = FragmentAdFrameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        startNativeAd()
    }

    lateinit var mAdView: AdView

    private fun startNativeAd() {
        try {
            MobileAds.initialize(requireContext())
            refreshAd()
        } catch (e: java.lang.Exception) {
            print(e.printStackTrace())
        }
    }

    private fun refreshAd() {
        val builder = AdLoader.Builder(requireContext(), ADMOB_AD_UNIT_ID)
        builder.forNativeAd { nativeAd ->
            currentNativeAd?.destroy()
            currentNativeAd = nativeAd
            val adView = LayoutInflater.from(context)
                .inflate(R.layout.unified_ad, binding.adFrame, false) as NativeAdView
            populateNativeAdView(nativeAd, adView)
            binding.adFrame.removeAllViews()
            binding.adFrame.addView(adView)
        }

        val adLoader = builder.withAdListener(object : AdListener() {
            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                val error = """domain: ${loadAdError.domain}, code: ${loadAdError.code}, message: ${loadAdError.message}""""
                println(error)
            }
        }).build()

        adLoader.loadAd(AdRequest.Builder().build())
    }

    private fun populateNativeAdView(nativeAd: NativeAd, adView: NativeAdView) {
        adView.headlineView = adView.findViewById(R.id.ad_headline)
        //adView.mediaView = adView.findViewById(R.id.ad_media)
        adView.bodyView = adView.findViewById(R.id.adTextContent)
        adView.iconView = adView.findViewById(R.id.ad_app_icon)
        adView.callToActionView = adView.findViewById(R.id.ad_call_to_action)

        (adView.headlineView as TextView).text = nativeAd.headline
        //adView.mediaView.setMediaContent(nativeAd.mediaContent)
        val body = nativeAd.body
        if (body == null) {
            adView.bodyView!!.visibility = View.INVISIBLE
        } else {
            adView.bodyView!!.visibility = View.VISIBLE
            (adView.bodyView as TextView).text = body
        }
        if (nativeAd.icon == null) {
            adView.iconView!!.visibility = View.GONE
        } else {
            (adView.iconView as ImageView).setImageDrawable(
                nativeAd.icon!!.drawable
            )
            adView.iconView!!.visibility = View.VISIBLE
        }
        if (nativeAd.callToAction == null) {
            adView.callToActionView!!.visibility = View.INVISIBLE
        } else {
            adView.callToActionView!!.visibility = View.VISIBLE
            (adView.callToActionView as Button).text = nativeAd.callToAction
        }
        adView.setNativeAd(nativeAd)
    }

    fun createBannerAd(toAddFrameLayout: FrameLayout) {
        try {
            MobileAds.initialize(requireContext())
            mAdView = AdView(requireContext())
            mAdView.adUnitId = "ca-app-pub-3940256099942544/6300978111"
            mAdView.adSize = AdSize(280,50)
            val adRequest = AdRequest.Builder().build()
            mAdView.loadAd(adRequest)
            mAdView.adListener = object : AdListener() {
                override fun onAdLoaded() {
                    AdState.current = AdState.SHOW
                }
                override fun onAdFailedToLoad(e: LoadAdError) {
                    AdState.current = AdState.HIDE
                }
                override fun onAdClosed() {
                    mAdView.loadAd(AdRequest.Builder().build())
                }
            }
            toAddFrameLayout.removeAllViews()
            toAddFrameLayout.addView(mAdView)
        } catch (e: Exception) {
            AdState.current = AdState.HIDE
            Log.d("AdLoadFail",e.printStackTrace().toString())
        }
    }
}

enum class AdState {
    SHOW, HIDE;
    companion object {
        var current = SHOW
    }
}
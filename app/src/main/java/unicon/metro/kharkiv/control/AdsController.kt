package unicon.metro.kharkiv.control

import android.util.Log
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import unicon.metro.kharkiv.ADVIEW_KEY
import unicon.metro.kharkiv.DEBUG
import unicon.metro.kharkiv.INTERSTITIAL_KEY
import unicon.metro.kharkiv.activity.MainActivity
import kotlin.concurrent.thread

class AdsController(var activity: MainActivity) {
    // ads
    private lateinit var mInterstitialAd: InterstitialAd
    private lateinit var adView: AdView

    fun load(isBanner: Boolean) = thread {
        val testDeviceIds = listOf("9B942B4C4E9CE92FCF83851D1B14B507", "A202AC01FFCCD10FFEBC2DC2D24E61EC")
        val configuration = RequestConfiguration
            .Builder()
            .setTestDeviceIds(testDeviceIds)
            .build()

        // инициализация AdMob
        MobileAds.setRequestConfiguration(configuration)
        MobileAds.initialize(activity)

        val adRequest = AdRequest
            .Builder()
            .build()

        if(!isBanner) { // если нужен не баннер
            InterstitialAd.load(activity,
                INTERSTITIAL_KEY, adRequest, object : InterstitialAdLoadCallback() {
                    override fun onAdFailedToLoad(adError: LoadAdError) {
                        val error = "domain: ${adError.domain}, code: ${adError.code}, " +
                                "message: ${adError.message}"
                        Log.d("loadAds", error)
                    }

                    override fun onAdLoaded(interstitialAd: InterstitialAd) {
                        if(DEBUG) Log.d("loadAds", "Ad was loaded.")

                        mInterstitialAd = interstitialAd

                        activity.runOnUiThread {
                            mInterstitialAd.show(activity)
                        }
                    }
                })
        } else { // если нужен баннер
            adView = AdView(activity)
            adView.adSize = AdSize.BANNER
            adView.adUnitId = ADVIEW_KEY

            activity.runOnUiThread {
                activity.linear.addView(adView)
                adView.loadAd(adRequest)
            }
        }
    }
}
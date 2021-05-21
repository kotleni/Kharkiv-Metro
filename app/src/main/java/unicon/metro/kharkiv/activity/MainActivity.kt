package unicon.metro.kharkiv.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.LinearLayout
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.afollestad.materialdialogs.list.checkItem
import com.afollestad.materialdialogs.list.listItemsSingleChoice
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import unicon.metro.kharkiv.*
import unicon.metro.kharkiv.R
import unicon.metro.kharkiv.types.Point
import unicon.metro.kharkiv.types.Vector
import unicon.metro.kharkiv.types.elements.BaseElement
import unicon.metro.kharkiv.types.elements.BranchElement
import unicon.metro.kharkiv.types.elements.TransElement

class MainActivity : Activity() {
    private var metroview: MetroView? = null
    private var mapData: ArrayList<BaseElement>? = null

    private var mInterstitialAd: InterstitialAd? = null
    private var adView: AdView? = null
    private var isBanner = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        isBanner = getSharedPreferences("main", Context.MODE_PRIVATE).getBoolean("isBanner", true)

        if(!REMOVE_ADD) loadAds(isBanner)
        setupMapData()
        setupMap()
    }

    private fun setupMapData() {
        mapData = ArrayList()
        mapData!!.add(BranchElement(listOf(
                Point(Vector(173, 34), R.string.name_peremoga,
                    R.string.desc_peremoga
                ),
                Point(Vector(190, 51), R.string.name_olecsiivka,
                    R.string.desc_olecsiivka
                ),
                Point(Vector(209, 81), R.string.name_23serpnya,
                    R.string.desc_23serpnya
                ),
                Point(Vector(209, 104), R.string.name_botanichniysad,
                    R.string.desc_botanichniysad
                ),
                Point(Vector(209, 127), R.string.name_naukova,
                    R.string.desc_naukova
                ),
                Point(Vector(209, 151), R.string.name_dergprom,
                    R.string.desc_dergprom
                ),
                Point(Vector(255, 208), R.string.name_arh_beketova,
                    R.string.desc_arh_beketova
                ),
                Point(Vector(278, 231), R.string.name_zah_ukr,
                    R.string.desc_zah_ukr
                ),
                Point(Vector(302, 267), R.string.name_metrobud,
                    R.string.desc_metrobud
                )
        ), Color.parseColor("#41AF63")))

        // красная ветка
        mapData!!.add(BranchElement(listOf(
                Point(Vector(22, 279), R.string.name_holodnagora,
                    R.string.desc_holodnagora
                ),
                Point(Vector(46, 256), R.string.name_pivdvokzal,
                    R.string.desc_pivdvokzal
                ),
                Point(Vector(69, 233), R.string.name_centrarinok,
                    R.string.desc_centrarinok
                ),
                Point(Vector(151, 208), R.string.name_maydankonst,
                    R.string.desc_maydankonst
                ),
                Point(Vector(197, 243), R.string.name_prospect_gagarnina,
                    R.string.desc_prospect_gagarnina
                ),
                Point(Vector(290, 278), R.string.name_sportivna,
                    R.string.desc_sportivna
                ),
                Point(Vector(337, 290), R.string.name_zavimenimalisheva,
                    R.string.desc_zavimenimalisheva
                ),
                Point(Vector(360, 313), R.string.name_turboatom,
                    R.string.desc_turboatom
                ),
                Point(Vector(383, 337), R.string.name_palacsporta,
                    R.string.desc_palacsporta
                ),
                Point(Vector(407, 359), R.string.name_armiyska,
                    R.string.desc_armiyska
                ),
                Point(Vector(419, 383), R.string.name_imosmaselscogo,
                    R.string.desc_imosmaselscogo
                ),
                Point(Vector(419, 407), R.string.name_traktorzavod,
                    R.string.desc_traktorzavod
                ),
                Point(Vector(419, 430), R.string.name_industrial,
                    R.string.desc_industrial
                )
        ), Color.parseColor("#D94F44")))

        // синяя ветка
        mapData!!.add(BranchElement(listOf(
                Point(Vector(407, 58), R.string.name_garoivwork,
                    R.string.desc_garoivwork
                ),
                Point(Vector(384, 81), R.string.name_studenstka,
                    R.string.desc_studenstka
                ),
                Point(Vector(359, 105), R.string.name_akadempavl,
                    R.string.desc_akadempavl
                ),
                Point(Vector(336, 127), R.string.name_barabashova,
                    R.string.desc_barabashova
                ),
                Point(Vector(313, 151), R.string.name_kievska,
                    R.string.desc_kievska
                ),
                Point(Vector(280, 163), R.string.name_pushkinska,
                    R.string.desc_pushkinska
                ),
                Point(Vector(196, 163), R.string.name_univer,
                    R.string.desc_univer
                ),
                Point(Vector(167, 163), null, -1),
                Point(Vector(163, 167), null, -1),
                Point(Vector(162, 197), R.string.name_istormisei,
                    R.string.desc_istormisei
                )
        ), Color.parseColor("#4062A5")))

        mapData!!.add(TransElement(
                Vector(151, 208),
                Vector(162, 197)
        ))

        mapData!!.add(TransElement(
                Vector(209, 151),
                Vector(196, 163)
        ))

        mapData!!.add(TransElement(
                Vector(290, 278),
                Vector(302, 267)
        ))
    }

    private fun setupMap() {
        metroview = findViewById(R.id.metroview)
        metroview!!.setData(mapData!!)
        metroview!!.prepare(findViewById<LinearLayout>(R.id.linear))
        metroview!!.setOnItemClickListener {
            showDialog(it)
        }
    }

    private fun showDialog(st: Point?) {
        if(st == null) return

        MaterialDialog(this, BottomSheet(LayoutMode.WRAP_CONTENT)).show {
            title(-1, resources.getString(st.name!!))
            message(-1, getString(st.about))
        }
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.actionbar, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.noads -> {
                MaterialDialog(this, BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                    title(-1, getString(R.string.noads))
                    listItemsSingleChoice(R.array.array_name) { dialog, index, text ->
                        getSharedPreferences("main", Context.MODE_PRIVATE).edit().also {
                            it.putBoolean("isBanner", (index != 0))
                            it.apply()
                        }

                        if(DEBUG) println("isBanner => ${(index != 0)}")
                    }
                    checkItem(if(getSharedPreferences("main", Context.MODE_PRIVATE).getBoolean("isBanner", true)) 1 else 0)

                }
            }
            R.id.googlrplay -> {
                val browserIntent2 = Intent(Intent.ACTION_VIEW, Uri.parse(MARKET_URL))
                startActivity(browserIntent2)
            }
            R.id.about -> {
                val dialog = MaterialDialog(this, BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                    customView(R.layout.about)
                }

                val icon = dialog.getCustomView().findViewById<ImageView>(R.id.icon)

                icon.setOnClickListener {
                    with(it.animate()) {
                        rotation(it.rotation + 360f)
                        duration = 500
                        start()
                    }
                }

                icon.setOnLongClickListener { true }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    /* загрузить и отобразить рекламу */
    private fun loadAds(isBanner: Boolean) {
        val testDeviceIds = listOf("9B942B4C4E9CE92FCF83851D1B14B507")
        val configuration = RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build()

        MobileAds.setRequestConfiguration(configuration)
        MobileAds.initialize(this)

        val adRequest = AdRequest.Builder().build()

        if(!isBanner)
        InterstitialAd.load(this,
            INTERSTITIAL_KEY, adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                val error = "domain: ${adError.domain}, code: ${adError.code}, " +
                        "message: ${adError.message}"
                Log.d("loadAds", error)

                mInterstitialAd = null
            }

            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                Log.d("loadAds", "Ad was loaded.")

                mInterstitialAd = interstitialAd

                if(mInterstitialAd != null)
                    mInterstitialAd!!.show(this@MainActivity)
            }
        })

        if(isBanner) {
            adView = AdView(this)
            adView!!.adSize = AdSize.BANNER
            adView!!.adUnitId = ADVIEW_KEY

            findViewById<LinearLayout>(R.id.linear).addView(adView!!)
            adView!!.loadAd(adRequest)
        }
    }
}
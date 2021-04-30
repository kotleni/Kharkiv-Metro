package unicon.metro.kharkiv

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
import android.widget.Toolbar
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
import unicon.metro.kharkiv.types.Point
import unicon.metro.kharkiv.types.Vector
import unicon.metro.kharkiv.types.elements.BaseElement
import unicon.metro.kharkiv.types.elements.BranchElement
import unicon.metro.kharkiv.types.elements.TransElement

class MainActivity : Activity() {
    private var metroview: MetroView? = null

    private var mInterstitialAd: InterstitialAd? = null
    private var isBanner = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        isBanner = getSharedPreferences("main", Context.MODE_PRIVATE).getBoolean("isBanner", true)
        loadAds(isBanner)

        val arr = ArrayList<BaseElement>()
        arr.add(BranchElement(listOf(
                Point(Vector(173, 34), "Перемога", R.string.desc_peremoga),
                Point(Vector(190, 51), "Олексiївська", R.string.desc_olecsiivka),
                Point(Vector(209, 81), "23 Серпня", R.string.desc_23serpnya),
                Point(Vector(209, 104), "Ботанiчний сад", R.string.desc_botanichniysad),
                Point(Vector(209, 127), "Наукова", R.string.desc_naukova),
                Point(Vector(209, 151), "Держпром", R.string.desc_dergprom),
                Point(Vector(255, 208), "Архiтектора Бекетова", R.string.desc_arh_beketova),
                Point(Vector(278, 231), "Захистникiв України", R.string.desc_zah_ukr),
                Point(Vector(302, 267), "Метробудiвникiв", R.string.desc_metrobud)
        ), Color.parseColor("#41AF63")))

        // красная ветка
        arr.add(BranchElement(listOf(
                Point(Vector(22, 279), "Холодна гора", R.string.desc_holodnagora),
                Point(Vector(46, 256), "Пiвденний вокзал", R.string.desc_pivdvokzal),
                Point(Vector(69, 233), "Центральний ринок", R.string.desc_centrarinok),
                Point(Vector(151, 208), "Майдан Коституцiї", R.string.desc_maydankonst),
                Point(Vector(197, 243), "Проспект Гагарiна", R.string.desc_prospect_gagarnina),
                Point(Vector(233, 278), null, -1),
                Point(Vector(290, 278), "Спортивна", R.string.desc_sportivna),
                Point(Vector(337, 290), "Завод iменi Малишева", R.string.desc_zavimenimalisheva),
                Point(Vector(360, 313), "Турбоатом", R.string.desc_turboatom),
                Point(Vector(383, 337), "Палац Спорту", R.string.desc_palacsporta),
                Point(Vector(407, 359), "Армiйська", R.string.desc_armiyska),
                Point(Vector(419, 383), "Iм. О. С. Масельского", R.string.desc_imosmaselscogo),
                Point(Vector(419, 407), "Тракторний завод", R.string.desc_traktorzavod),
                Point(Vector(419, 430), "Iндустрiальна", R.string.desc_industrial)
        ), Color.parseColor("#D94F44")))

        // синяя ветка
        arr.add(BranchElement(listOf(
                Point(Vector(407, 58), "Героїв Працi", R.string.desc_garoivwork),
                Point(Vector(384, 81), "Студентська", R.string.desc_studenstka),
                Point(Vector(359, 105), "Академiка Павлова", R.string.desc_akadempavl),
                Point(Vector(336, 127), "Академiка Барабашова", R.string.desc_barabashova),
                Point(Vector(313, 151), "Київська", R.string.desc_kievska),
                Point(Vector(280, 163), "Пушкiнська", R.string.desc_pushkinska),
                Point(Vector(196, 163), "Унiверситет", R.string.desc_univer),
                Point(Vector(163, 163), null, -1),
                Point(Vector(162, 197), "Iсторичний музей", R.string.desc_istormisei)
        ), Color.parseColor("#4062A5")))

        arr.add(TransElement(
                Vector(151, 208),
                Vector(162, 197)
        ))

        arr.add(TransElement(
                Vector(209, 151),
                Vector(196, 163)
        ))

        arr.add(TransElement(
                Vector(290, 278),
                Vector(302, 267)
        ))

        metroview = findViewById(R.id.metroview)
        metroview!!.data = arr
        metroview!!.prepare()
        metroview!!.setOnItemClickListener {
            showDialog(it)
        }
    }

    fun showDialog(st: Point?) {
        if(st == null) return

        MaterialDialog(this, BottomSheet(LayoutMode.WRAP_CONTENT)).show {
            title(-1, st.name)
            message(-1, getString(st.resid))

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

                        println("isBanner => ${(index != 0)}")
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
                    it.animate().also {anim ->
                        anim.rotation(it.rotation + 360f)
                        anim.duration = 500
                        anim.start()
                    }
                }

                icon.setOnLongClickListener { true }
            }
        }
        return super.onOptionsItemSelected(item)
    }



    /* загрузить и отобразить рекламу */
    fun loadAds(isBanner: Boolean) {
        val testDeviceIds = listOf("9B942B4C4E9CE92FCF83851D1B14B507")
        val configuration = RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build()

        MobileAds.setRequestConfiguration(configuration)
        MobileAds.initialize(this)

        val adRequest = AdRequest.Builder().build()

        if(!isBanner)
        InterstitialAd.load(this,"ca-app-pub-8334416213766495/6209107906", adRequest, object : InterstitialAdLoadCallback() {
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
            val adView = AdView(this)
            adView.adSize = AdSize.BANNER
            adView.adUnitId = "ca-app-pub-8334416213766495/8544122047"

            findViewById<LinearLayout>(R.id.linear).addView(adView)

            adView.loadAd(adRequest)
        }
    }
}
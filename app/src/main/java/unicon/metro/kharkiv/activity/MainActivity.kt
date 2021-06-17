package unicon.metro.kharkiv.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageInfo
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.afollestad.materialdialogs.list.checkItem
import com.afollestad.materialdialogs.list.listItems
import com.afollestad.materialdialogs.list.listItemsSingleChoice
import unicon.metro.kharkiv.DEBUG
import unicon.metro.kharkiv.MARKET_URL
import unicon.metro.kharkiv.R
import unicon.metro.kharkiv.control.AdsController
import unicon.metro.kharkiv.dialog.SplashDialog
import unicon.metro.kharkiv.makeMapData
import unicon.metro.kharkiv.types.Point
import unicon.metro.kharkiv.types.elements.BaseElement
import unicon.metro.kharkiv.view.MetroView
import kotlin.concurrent.thread

class MainActivity : Activity() {
    // views
    private lateinit var metroview: MetroView
    lateinit var linear: LinearLayout

    // data
    private lateinit var mapData: ArrayList<BaseElement>
    private var isBanner = false

    // other
    private lateinit var prefs: SharedPreferences
    private lateinit var splash: SplashDialog
    private lateinit var adsController: AdsController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // получаем view
        metroview = findViewById(R.id.metroview)
        linear = findViewById(R.id.linear)

        // загружаем настройки
        prefs = getSharedPreferences("main", Context.MODE_PRIVATE)
        isBanner = prefs.getBoolean("isBanner", true)

        // контроллер для рекламы
        adsController = AdsController(this)

        // splash
        splash = SplashDialog(this)
        splash.show()

        // запускаем фоновый поток
        doBackground()
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
                    listItemsSingleChoice(R.array.array_name) { _, index, _ ->
                        prefs.edit().also {
                            it.putBoolean("isBanner", (index != 0))
                            it.apply()
                        }

                        if(DEBUG) println("isBanner => ${(index != 0)}")
                    }
                    checkItem(if(prefs.getBoolean("isBanner", true)) 1 else 0)

                }
            }

            R.id.googlrplay -> {
                val browserIntent2 = Intent(Intent.ACTION_VIEW, Uri.parse(MARKET_URL))
                startActivity(browserIntent2)
            }

            R.id.about -> {
                val dialog = MaterialDialog(this, BottomSheet(LayoutMode.WRAP_CONTENT)).show {
                    customView(R.layout.about)
//                    listItems(-1, listOf(
//                            "Версия: $appVersion",
//                            "Пакет: $packageName"
//                    ))
                }

                val icon = dialog.getCustomView().findViewById<ImageView>(R.id.icon)

                icon.setOnLongClickListener {
                    dialog.dismiss()
                    Toast.makeText(this, "I am an iron man.", Toast.LENGTH_SHORT)
                            .show()

                    true
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    /* загружаем данные в фоне */
    private fun doBackground() = thread {
        mapData = makeMapData()

        // возращаемся в ui поток
        runOnUiThread {
            splash.cancel()

            adsController.load(isBanner)

            setupMap()
        }
    }

    /* настраиваем view карты */
    private fun setupMap() {
        metroview.setData(mapData)
        metroview.prepare(linear)
        metroview.setOnItemClickListener {
            showDialog(it)
        }

        metroview.invalidate()
    }

    /* показать диалог */
    private fun showDialog(st: Point) =
            MaterialDialog(this, BottomSheet(LayoutMode.WRAP_CONTENT)).show {
        title(-1, resources.getString(st.name!!))
        message(-1, getString(st.about))
    }
}
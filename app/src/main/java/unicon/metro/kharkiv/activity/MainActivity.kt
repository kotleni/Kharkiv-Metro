package unicon.metro.kharkiv.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import unicon.metro.kharkiv.*
import unicon.metro.kharkiv.types.Point
import unicon.metro.kharkiv.types.elements.BaseElement
import unicon.metro.kharkiv.view.MetroView
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {
    // views
    private lateinit var metroview: MetroView
    private lateinit var linear: CoordinatorLayout
    private lateinit var fab: FloatingActionButton

    // ...
    private lateinit var prefs: SharedPreferences
    private lateinit var mapData: ArrayList<BaseElement>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        prefs = getSharedPreferences(PREFS_MAIN, Context.MODE_PRIVATE)

        // views
        metroview = findViewById(R.id.metroView)
        linear = findViewById(R.id.linear)
        fab = findViewById(R.id.fab)

        fab.setOnClickListener { showAboutDialog() }

        doBackground()
    }

    private fun openGooglePlay() {
        val browserIntent2 = Intent(Intent.ACTION_VIEW, Uri.parse(MARKET_URL))
        startActivity(browserIntent2)
    }

    private fun openSupportLink() {
        val browserIntent2 = Intent(Intent.ACTION_VIEW, Uri.parse(SUPPORT_URL))
        startActivity(browserIntent2)
    }

    /* показать даилог 'О приложении' */
    private fun showAboutDialog() {
        val dialog = MaterialDialog(this, BottomSheet(LayoutMode.WRAP_CONTENT)).show {
            customView(R.layout.about)
        }

        val icon = dialog.getCustomView().findViewById<ImageView>(R.id.icon)
        val market = dialog.getCustomView().findViewById<ImageView>(R.id.market)
        val support = dialog.getCustomView().findViewById<ImageView>(R.id.support)

        icon.setOnLongClickListener {
            dialog.dismiss()
            Snackbar.make(linear, EGG_TEXT, Snackbar.LENGTH_LONG)
                .show()

            true
        }

        market.setOnClickListener {
            openGooglePlay()
        }

        support.setOnClickListener {
            openSupportLink()
        }
    }

    /* показать диалог стацнии*/
    private fun showDialog(st: Point) {
        MaterialDialog(this, BottomSheet(LayoutMode.WRAP_CONTENT)).show {
            title(-1, resources.getString(st.name!!))
            message(-1, getString(st.about))
        }
    }

    /* загружаем данные в фоне */
    private fun doBackground() = thread {
        mapData = makeMapData()

        // возращаемся в ui поток
        runOnUiThread {
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
}
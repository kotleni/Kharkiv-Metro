package unicon.metro.kharkiv.activity

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import unicon.metro.kharkiv.*
import unicon.metro.kharkiv.dialog.AboutDialog
import unicon.metro.kharkiv.dialog.StationDialog
import unicon.metro.kharkiv.model.MainModel
import unicon.metro.kharkiv.types.Point
import unicon.metro.kharkiv.types.elements.BaseElement
import unicon.metro.kharkiv.view.MetroView
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity(), Observer {
    // views
    private lateinit var metroview: MetroView
    private lateinit var linear: CoordinatorLayout
    private lateinit var fab: FloatingActionButton

    // ...
    private lateinit var prefs: SharedPreferences
    private lateinit var mapData: ArrayList<BaseElement>

    // model
    private lateinit var mainModel: MainModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // model
        mainModel = MainModel()
        mainModel.addObserver(this)

        prefs = getSharedPreferences(PREFS_MAIN, Context.MODE_PRIVATE)

        // views
        metroview = findViewById(R.id.metroView)
        linear = findViewById(R.id.linear)
        fab = findViewById(R.id.fab)

        fab.setOnClickListener { showAboutDialog() }

        doBackground()
    }

    override fun update(o: Observable?, arg: Any?) {
        setupMap()
    }

    /* показать даилог 'О приложении' */
    private fun showAboutDialog() = AboutDialog(this).show()

    /* показать диалог стацнии*/
    private fun showStationDialog(st: Point) = StationDialog(this, st.name!!, st.about).show()

    /* загружаем данные в фоне */
    private fun doBackground() = thread {
        mapData = makeMapData()

        // возращаемся в ui поток
        runOnUiThread { mainModel.updateMap() }
    }

    /* настраиваем view карты */
    private fun setupMap() {
        metroview.setData(mapData)
        metroview.prepare(linear)
        metroview.setOnItemClickListener { showStationDialog(it) }

        metroview.invalidate()
    }
}
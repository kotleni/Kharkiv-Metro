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
    private val metroview: MetroView by lazy { findViewById(R.id.metroView) }
    private val linear: CoordinatorLayout by lazy { findViewById(R.id.linear) }
    private val fab: FloatingActionButton by lazy { findViewById(R.id.fab) }

    // ...
    private val prefs: SharedPreferences by lazy { getSharedPreferences(PREFS_MAIN, Context.MODE_PRIVATE) }
    private val mapData: ArrayList<BaseElement> = ArrayList()

    // model
    private lateinit var mainModel: MainModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // model
        mainModel = MainModel()
        mainModel.addObserver(this)

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
        // обновляем данные карты
        mapData.clear()
        mapData.addAll(makeMapData())

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
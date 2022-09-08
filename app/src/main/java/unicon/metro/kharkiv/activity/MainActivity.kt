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
    private val metroview: MetroView by lazy { findViewById(R.id.metroView) }
    // private val linear: CoordinatorLayout by lazy { findViewById(R.id.linear) }
    private val fab: FloatingActionButton by lazy { findViewById(R.id.fab) }

    private val mapData: ArrayList<BaseElement> = ArrayList()

    // fixme: stupid viewmodel
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

    // show about dialog
    private fun showAboutDialog() {
        AboutDialog(this).show()
    }

    // show station dialog
    private fun showStationDialog(st: Point) {
        StationDialog(this, st.name!!, st.about).show()
    }

    // loading data in background
    private fun doBackground() = thread {
        // update map data
        mapData.clear()
        mapData.addAll(makeMapData())

        runOnUiThread {
            mainModel.updateMap()
        }
    }

    // setup metroview map
    private fun setupMap() {
        metroview.setData(mapData)
        metroview.prepare()
        metroview.setOnItemClickListener { showStationDialog(it) }

        metroview.invalidate()
    }
}
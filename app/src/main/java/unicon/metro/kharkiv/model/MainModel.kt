package unicon.metro.kharkiv.model

import java.util.*

class MainModel : Observable() {

    fun updateMap() {
        update()
    }

    /* обновить view */
    private fun update() {
        setChanged()
        notifyObservers()
    }
}
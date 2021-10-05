package unicon.metro.kharkiv.dialog

import android.content.Context
import unicon.metro.kharkiv.R

class StationDialog(_ctx: Context, var nameId: Int, var aboutId: Int) : BaseDialog(_ctx) {

    override fun show() {
        getDialog().apply {
            icon(R.mipmap.ic_launcher)
            title(nameId)
            message(aboutId)
        }

        super.show()
    }

    override fun cancel() {
        super.cancel()
    }
}
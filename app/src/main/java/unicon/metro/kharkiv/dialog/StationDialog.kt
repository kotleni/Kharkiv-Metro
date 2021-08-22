package unicon.metro.kharkiv.dialog

import android.content.Context

class StationDialog(_ctx: Context, var name: Int, var about: Int) : BaseDialog(_ctx) {

    override fun show() {
        getDialog().title(-1, ctx.resources.getString(name))
        getDialog().message(-1, ctx.resources.getString(about))

        super.show()
    }

    override fun cancel() {
        super.cancel()
    }
}
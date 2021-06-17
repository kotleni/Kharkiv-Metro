package unicon.metro.kharkiv.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import unicon.metro.kharkiv.R

class SplashDialog(var ctx: Context) {
    private var dialog = Dialog(ctx, android.R.style.Theme_Material_Light_NoActionBar)

    init {
        dialog.setContentView(R.layout.splash)
    }

    fun show() {
        dialog.show()
    }

    fun cancel() {
        dialog.dismiss()
    }
}
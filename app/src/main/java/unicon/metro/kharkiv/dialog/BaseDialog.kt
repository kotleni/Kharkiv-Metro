package unicon.metro.kharkiv.dialog

import android.content.Context
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet

open class BaseDialog(var ctx: Context) {
    private val dialog = MaterialDialog(ctx, BottomSheet(LayoutMode.WRAP_CONTENT))

    open fun show() {
        dialog.apply {
            cornerRadius(16f)
            show()
        }
    }

    open fun cancel() {
        dialog.cancel()
    }

    fun getDialog() : MaterialDialog {
        return dialog
    }
}
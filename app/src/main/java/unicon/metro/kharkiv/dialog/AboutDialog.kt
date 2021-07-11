package unicon.metro.kharkiv.dialog

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.ImageView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.google.android.material.snackbar.Snackbar
import unicon.metro.kharkiv.EGG_TEXT
import unicon.metro.kharkiv.MARKET_URL
import unicon.metro.kharkiv.R
import unicon.metro.kharkiv.SUPPORT_URL

class AboutDialog(var _ctx: Context) : BaseDialog(_ctx) {

    // views
    private lateinit var icon: ImageView
    private lateinit var marketBtn: ImageView
    private lateinit var supportBtn: ImageView
    private lateinit var coordinator: CoordinatorLayout

    init {
        getDialog().customView(R.layout.about)
        val root = getDialog().getCustomView()

        // views
        icon = root.findViewById(R.id.icon)
        marketBtn = root.findViewById(R.id.market)
        supportBtn = root.findViewById(R.id.support)
        coordinator = root.findViewById(R.id.coordinator)

        // listeners
        icon.setOnLongClickListener {
            cancel()
            Snackbar.make(coordinator, EGG_TEXT, Snackbar.LENGTH_LONG)
                .show()

            true
        }

        marketBtn.setOnClickListener {
            openUrl(MARKET_URL)
        }

        supportBtn.setOnClickListener {
            openUrl(SUPPORT_URL)
        }
    }

    override fun show() = getDialog().show()
    override fun cancel() = getDialog().cancel()

    /* открыть ссылку в браузере */
    private fun openUrl(url: String) {
        ctx.startActivity(
            Intent(Intent.ACTION_VIEW, Uri.parse(url))
        )
    }
}
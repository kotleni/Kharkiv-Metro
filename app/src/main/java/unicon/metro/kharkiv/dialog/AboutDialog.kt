package unicon.metro.kharkiv.dialog

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.ImageView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.google.android.material.snackbar.Snackbar
import unicon.metro.kharkiv.MARKET_URL
import unicon.metro.kharkiv.R
import unicon.metro.kharkiv.SUPPORT_URL

class AboutDialog(var _ctx: Context) : BaseDialog(_ctx) {

    // views
    private var marketBtn: ImageView
    private var supportBtn: ImageView
    private var coordinator: CoordinatorLayout

    init {
        getDialog().apply {
            customView(R.layout.about)
        }
        val root = getDialog().getCustomView()

        // views
        marketBtn = root.findViewById(R.id.market)
        supportBtn = root.findViewById(R.id.support)
        coordinator = root.findViewById(R.id.coordinator)

        // listeners
        marketBtn.setOnClickListener {
            openUrl(MARKET_URL)
        }

        supportBtn.setOnClickListener {
            openUrl(SUPPORT_URL)
        }
    }

    /* открыть ссылку в браузере */
    private fun openUrl(url: String) {
        ctx.startActivity(
            Intent(Intent.ACTION_VIEW, Uri.parse(url))
        )
    }
}
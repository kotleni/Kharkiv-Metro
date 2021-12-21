package unicon.metro.kharkiv.dialog

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.TextView
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import unicon.metro.kharkiv.BuildConfig
import unicon.metro.kharkiv.MARKET_URL
import unicon.metro.kharkiv.R

class AboutDialog(var _ctx: Context) : BaseDialog(_ctx) {
    init {
        getDialog().apply { customView(R.layout.about) }

        val root = getDialog().getCustomView()
        val verStr: TextView = root.findViewById(R.id.version_str)

        verStr.text = verStr.text.toString()
            .replace("{}", BuildConfig.VERSION_NAME)
    }

    // открыть ссылку в браузере
    private fun openUrl(url: String) {
        ctx.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    }
}
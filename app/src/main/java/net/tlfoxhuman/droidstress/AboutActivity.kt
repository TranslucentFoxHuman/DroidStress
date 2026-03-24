package net.tlfoxhuman.droidstress

import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class AboutActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()
        setContentView(R.layout.activity_about)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<TextView>(R.id.versionView).setText(packageManager.getPackageInfo(packageName,0).versionName)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            findViewById<TextView>(R.id.copyrightView).setText(Html.fromHtml(getString(R.string.copyright_html),Html.FROM_HTML_MODE_LEGACY))
            findViewById<TextView>(R.id.gplTextView).setText(Html.fromHtml(getString(R.string.gpl_html),Html.FROM_HTML_MODE_LEGACY))
        } else {
            findViewById<TextView>(R.id.copyrightView).setText(Html.fromHtml(getString(R.string.copyright_html)))
            findViewById<TextView>(R.id.gplTextView).setText(Html.fromHtml(getString(R.string.gpl_html)))
        }
        findViewById<TextView>(R.id.copyrightView).movementMethod = LinkMovementMethod.getInstance();
        findViewById<TextView>(R.id.gplTextView).movementMethod = LinkMovementMethod.getInstance();
    }
}
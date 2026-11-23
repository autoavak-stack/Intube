package com.intube.app

import android.os.Bundle
import android.graphics.Color
import android.view.Gravity
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val root = LinearLayout(this)
        root.orientation = LinearLayout.VERTICAL
        root.setBackgroundColor(Color.rgb(15, 15, 15))

        val header = TextView(this)
        header.text = "▶ INTUBE"
        header.textSize = 27f
        header.setTextColor(Color.RED)
        header.setPadding(18, 22, 18, 22)
        header.setTypeface(null, 1)
        root.addView(header)

        val search = EditText(this)
        search.hint = "Search videos..."
        search.setSingleLine(true)
        search.setTextColor(Color.WHITE)
        search.setHintTextColor(Color.LTGRAY)
        search.setPadding(18, 12, 18, 12)
        root.addView(search)

        val categories = TextView(this)
        categories.text = "  All     Music     Gaming     Education     Travel"
        categories.textSize = 15f
        categories.setTextColor(Color.WHITE)
        categories.setPadding(10, 20, 10, 20)
        root.addView(categories)

        val scroll = ScrollView(this)
        val videos = LinearLayout(this)
        videos.orientation = LinearLayout.VERTICAL
        videos.setPadding(12, 10, 12, 10)

        val titles = listOf(
            "Welcome to Intube",
            "Explore New Videos",
            "Upload Your First Video",
            "Discover Creators"
        )

        for (title in titles) {
            val card = TextView(this)
            card.text = "▶\n\n$title\n\nVideo thumbnail"
            card.textSize = 19f
            card.gravity = Gravity.CENTER
            card.setTextColor(Color.WHITE)
            card.setBackgroundColor(Color.rgb(38, 38, 38))
            card.setPadding(10, 35, 10, 35)

            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 220
            )
            params.bottomMargin = 18
            videos.addView(card, params)
        }

        scroll.addView(videos)
        root.addView(scroll, LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f
        ))

        val bottom = TextView(this)
        bottom.text = "  Home          Shorts          Upload          Profile"
        bottom.textSize = 15f
        bottom.setTextColor(Color.WHITE)
        bottom.setBackgroundColor(Color.rgb(30, 30, 30))
        bottom.setPadding(8, 20, 8, 20)
        bottom.gravity = Gravity.CENTER
        root.addView(bottom)

        setContentView(root)
    }
}

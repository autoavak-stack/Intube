package com.intube.app

import android.os.Bundle
import android.graphics.Color
import android.content.Intent
import android.net.Uri
import android.view.Gravity
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {

    private val executor = Executors.newSingleThreadExecutor()
    private lateinit var results: LinearLayout
    private lateinit var search: EditText
    private lateinit var status: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val root = LinearLayout(this)
        root.orientation = LinearLayout.VERTICAL
        root.setPadding(14, 12, 14, 8)
        root.setBackgroundColor(Color.rgb(15, 15, 15))

        val header = TextView(this)
        header.text = "▶ INTUBE"
        header.textSize = 28f
        header.setTextColor(Color.RED)
        header.setTypeface(null, 1)
        root.addView(header)

        val searchRow = LinearLayout(this)
        searchRow.orientation = LinearLayout.HORIZONTAL

        search = EditText(this)
        search.hint = "Search online videos..."
        search.setSingleLine(true)
        search.setTextColor(Color.WHITE)
        search.setHintTextColor(Color.LTGRAY)
        search.setBackgroundColor(Color.rgb(45, 45, 45))
        search.setPadding(12, 8, 12, 8)
        search.imeOptions = EditorInfo.IME_ACTION_SEARCH

        searchRow.addView(search, LinearLayout.LayoutParams(0, 55, 1f))

        val button = Button(this)
        button.text = "Search"
        searchRow.addView(button)
        root.addView(searchRow)

        status = TextView(this)
        status.text = "Free online video search"
        status.setTextColor(Color.LTGRAY)
        status.setPadding(4, 12, 4, 12)
        root.addView(status)

        val categories = LinearLayout(this)
        categories.orientation = LinearLayout.HORIZONTAL

        listOf("Music", "Gaming", "Education", "Travel").forEach { category ->
            val chip = Button(this)
            chip.text = category
            chip.textSize = 10f
            chip.setOnClickListener {
                search.setText(category)
                searchVideos(category)
            }
            categories.addView(chip)
        }

        root.addView(categories)

        val scroll = ScrollView(this)
        results = LinearLayout(this)
        results.orientation = LinearLayout.VERTICAL
        scroll.addView(results)

        root.addView(scroll, LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f
        ))

        val bottom = TextView(this)
        bottom.text = "Home       Shorts       Upload       Profile"
        bottom.gravity = Gravity.CENTER
        bottom.textSize = 15f
        bottom.setTextColor(Color.WHITE)
        bottom.setBackgroundColor(Color.rgb(35, 35, 35))
        bottom.setPadding(8, 18, 8, 18)
        root.addView(bottom)

        setContentView(root)

        button.setOnClickListener {
            searchVideos(search.text.toString())
        }

        search.setOnEditorActionListener { _, action, _ ->
            if (action == EditorInfo.IME_ACTION_SEARCH) {
                searchVideos(search.text.toString())
                true
            } else false
        }

        searchVideos("music")
    }

    private fun searchVideos(query: String) {
        if (query.isBlank()) {
            status.text = "Please enter a search word"
            return
        }

        results.removeAllViews()
        status.text = "Searching online..."

        executor.execute {
            try {
                val encoded = URLEncoder.encode(query, "UTF-8")
                val address =
                    "https://peertube.cpy.re/api/v1/search/videos?search=$encoded&count=20"

                val connection = URL(address).openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connectTimeout = 15000
                connection.readTimeout = 15000

                val response = connection.inputStream.bufferedReader().use {
                    it.readText()
                }
                connection.disconnect()

                val data = JSONObject(response).optJSONArray("data")

                runOnUiThread {
                    results.removeAllViews()

                    if (data == null || data.length() == 0) {
                        status.text = "No videos found. Try another search."
                        return@runOnUiThread
                    }

                    status.text = "${data.length()} online videos found"

                    for (i in 0 until data.length()) {
                        val video = data.getJSONObject(i)
                        val title = video.optString("name", "Untitled video")
                        val uuid = video.optString("uuid", "")
                        val videoUrl = video.optString(
                            "url",
                            "https://peertube.cpy.re/videos/watch/$uuid"
                        )

                        val card = TextView(this)
                        card.text = "▶  $title\n\nWatch online  →"
                        card.textSize = 17f
                        card.setTextColor(Color.WHITE)
                        card.setBackgroundColor(Color.rgb(42, 42, 42))
                        card.setPadding(18, 25, 18, 25)

                        val params = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        )
                        params.bottomMargin = 12

                        card.setOnClickListener {
                            try {
                                startActivity(
                                    Intent(Intent.ACTION_VIEW, Uri.parse(videoUrl))
                                )
                            } catch (e: Exception) {
                                Toast.makeText(
                                    this,
                                    "Cannot open this video",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                        results.addView(card, params)
                    }
                }
            } catch (e: Exception) {
                runOnUiThread {
                    status.text = "Internet/search error: ${e.message}"
                }
            }
        }
    }

    override fun onDestroy() {
        executor.shutdown()
        super.onDestroy()
    }
}

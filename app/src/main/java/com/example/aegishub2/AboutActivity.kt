package com.example.aegishub2

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class AboutActivity : AppCompatActivity() {

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        val photoIkhwan: ImageView = findViewById(R.id.photo_ikhwan)
        val photoIqma: ImageView = findViewById(R.id.photo_iqma)
        val photoHarith: ImageView = findViewById(R.id.photo_harith)
        val photoIskandar: ImageView = findViewById(R.id.photo_iskandar)

        // GitHub Button
        val buttonGitHub: Button = findViewById(R.id.button_github)
        buttonGitHub.setOnClickListener {
            goToGitHub()
        }

        // Admin Button
        val buttonAdmin: Button = findViewById(R.id.button_admin)
        buttonAdmin.setOnClickListener {
            goToAdmin()
        }

        // YouTube Button
        val buttonYouTube: Button = findViewById(R.id.button_youtube)
        buttonYouTube.setOnClickListener {
            goToYouTube()
        }

        // Initialize and setup BottomNavigationView
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        // Set the nav_about item as checked
        bottomNavigationView.menu.findItem(R.id.nav_about)?.isChecked = true

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            // Handle navigation actions
            when (item.itemId) {
                // Handle other menu items as needed
                R.id.nav_navigation -> {
                    startActivity(Intent(this, SecondActivity::class.java))
                    true
                }
                R.id.nav_map -> {
                    startActivity(Intent(this, MapActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }

    private fun goToGitHub() {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/"))
        startActivity(browserIntent)
    }

    private fun goToAdmin() {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("http://localhost/mobile/index.php"))
        startActivity(browserIntent)
    }

    private fun goToYouTube() {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/"))
        startActivity(browserIntent)
    }
}

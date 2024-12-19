@file:Suppress("DEPRECATION")

package com.example.aegishub2

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView

class NavigationActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TruckListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigation)

        // Initialize RecyclerView and adapter
        recyclerView = findViewById(R.id.recycler_view_trucks)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = TruckListAdapter(getTruckData()) // Pass your truck data here
        recyclerView.adapter = adapter

        // Optional: Setup origin and destination EditText and navigate button
        val originInput = findViewById<EditText>(R.id.origin_input)
        val destinationInput = findViewById<EditText>(R.id.destination_input)
        val navigateButton = findViewById<Button>(R.id.navigate_button)

        navigateButton.setOnClickListener {
            val origin = originInput.text.toString()
            val destination = destinationInput.text.toString()

            // Handle navigation logic here
            val intent = Intent(this, MapActivity::class.java).apply {
                putExtra("origin", origin)
                putExtra("destination", destination)
            }
            startActivity(intent)
        }

        // Initialize bottom navigation view
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_map -> {
                    // Navigate to MapActivity
                    startActivity(Intent(this, MapActivity::class.java))
                    true
                }
                R.id.nav_navigation -> {
                    // Optional: Handle reselect logic if needed
                    true
                }
                else -> false
            }
        }
    }

    private fun getTruckData(): List<Truck> {
        // Sample data for trucks
        return listOf(
            Truck("Bugis com.example.aegishub2.Truck", "Plaza Mara Beseri", "https://maps.app.goo.gl/oKTM5tSrbJcMeZvY9", "Available", R.drawable.bugiscoffee),
            Truck("Gorpis com.example.aegishub2.Truck", "No 24, Jalan Kapitol", "https://maps.app.goo.gl/YpgC1rUhJLYJpkQSA", "Unavailable", R.drawable.gorpiscoffee),
            Truck("Ngopi com.example.aegishub2.Truck", "Mutiara Damansara, Petaling Jaya", "https://maps.app.goo.gl/N4ccLwuEvZgGfQN98", "Available", R.drawable.ngopicoffee)
            // Add more trucks as needed
        )
    }
}

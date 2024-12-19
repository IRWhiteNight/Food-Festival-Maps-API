package com.example.aegishub2

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FetchPlaceResponse
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.json.JSONArray
import org.json.JSONException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var placesClient: PlacesClient
    private lateinit var autocompleteFragment: AutocompleteSupportFragment
    private val URL_PLACES = "http://192.168.0.119/mobile/places"
    private var selectedMarker: Marker? = null
    private var placeName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)


        // Initialize Places SDK
        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, getString(R.string.google_maps_key))
        }
        placesClient = Places.createClient(this)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Set up autocomplete fragment
        autocompleteFragment = supportFragmentManager.findFragmentById(R.id.autocomplete_fragment) as AutocompleteSupportFragment
        autocompleteFragment.setPlaceFields(listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS))

        autocompleteFragment.setOnPlaceSelectedListener(object : com.google.android.libraries.places.widget.listener.PlaceSelectionListener {
            override fun onPlaceSelected(place: com.google.android.libraries.places.api.model.Place) {
                placeName = place.name
                place.latLng?.let { latLng ->
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
                    selectedMarker?.remove()
                    selectedMarker = mMap.addMarker(MarkerOptions().position(latLng).title(place.name))
                    selectedMarker?.showInfoWindow()

                    // Accessing latitude and longitude
                    val latitude = latLng.latitude
                    val longitude = latLng.longitude
                    Toast.makeText(this@MapActivity, "Selected place: ${place.name}, Lat: $latitude, Lng: $longitude", Toast.LENGTH_SHORT).show()
                } ?: run {
                    Toast.makeText(this@MapActivity, "Failed to retrieve location for selected place", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onError(status: com.google.android.gms.common.api.Status) {
                Toast.makeText(this@MapActivity, "Error: ${status.statusMessage}", Toast.LENGTH_SHORT).show()
            }
        })

        // Initialize bottom navigation view
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_about -> {
                    // Replace with your AboutActivity or AboutFragment class
                    startActivity(Intent(this, AboutActivity::class.java))
                    true
                }
                R.id.nav_navigation -> {
                    startActivity(Intent(this, SecondActivity::class.java))
                    true
                }
                R.id.nav_map -> {
                    // Already in MapActivity, do nothing or handle as needed
                    true
                }
                else -> false
            }
        }


        // Inside onCreate method of MapActivity

// Find the details_layout ConstraintLayout
        val detailsLayout = findViewById<ConstraintLayout>(R.id.details_layout)

// Retrieve intent extras from SecondActivity
        if (intent != null && intent.hasExtra("place_name") && intent.hasExtra("longitude_altitude")) {
            placeName = intent.getStringExtra("place_name")
            val longitudeAltitude = intent.getStringExtra("longitude_altitude")

            val placeName = intent.getStringExtra("place_name")
            val placeAddress = intent.getStringExtra("place_address")
            val placeImgUrl = intent.getStringExtra("place_imgUrl")
            val placeOpen = intent.getStringExtra("place_open")
            val placeMenu = intent.getStringExtra("place_menu")

            if (placeName != null && longitudeAltitude != null) {
                autocompleteFragment.setText(longitudeAltitude)
                performPlaceSearch(longitudeAltitude)

                // Display selected place details if available
                findViewById<TextView>(R.id.place_name_textview)?.text = placeName
                findViewById<TextView>(R.id.place_longitude_latitude_textview)?.text = longitudeAltitude

                val placeImageView = findViewById<ImageView>(R.id.place_imageview)
                ImageLoadTask(placeImgUrl, placeImageView).execute()

                findViewById<TextView>(R.id.place_open_textview)?.text = placeOpen
                findViewById<TextView>(R.id.place_menu_textview)?.text = placeMenu

                // Show details_layout
                detailsLayout.visibility = View.VISIBLE
            } else {
                Toast.makeText(this, "Extras are null", Toast.LENGTH_SHORT).show()
            }
        }

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Center map on Malaysia as default
        val malaysia = LatLng(4.2105, 101.9758)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(malaysia, 7.0f))

        // Fetch places
        fetchPlaces()

        // Display selected place if provided
        if (placeName != null && intent.hasExtra("longitude_altitude")) {
            val longitudeAltitude = intent.getStringExtra("longitude_altitude")
            val placeLatLng = longitudeAltitude?.let { getLocationFromLongitudeAltitude(it) }
            placeLatLng?.let {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(it, 15f))
                selectedMarker?.remove()
                selectedMarker = mMap.addMarker(MarkerOptions().position(it).title(placeName))
                selectedMarker?.showInfoWindow()
            }
        }

        // Display selected place details if available
        if (placeName != null && intent.hasExtra("longitude_altitude")) {
            findViewById<TextView>(R.id.place_name_textview)?.text = placeName
            findViewById<TextView>(R.id.place_longitude_latitude_textview)?.text = intent.getStringExtra("longitude_altitude")
        }
    }

    private fun fetchPlaces() {
        val requestQueue = Volley.newRequestQueue(this)
        val jsonArrayRequest = JsonArrayRequest(Request.Method.GET, URL_PLACES, null,
            Response.Listener<JSONArray> { response ->
                try {
                    for (i in 0 until response.length()) {
                        val jsonObject = response.getJSONObject(i)
                        val placeName = jsonObject.getString("name")
                        val placeAddress = jsonObject.getString("longitude_altitude")
                        val placeOpen = jsonObject.getString("open")
                        //val placeImgUrl = jsonObject.getString("img")
                        val placeMenu = jsonObject.getString("menu")
                        val latLng = getLocationFromLongitudeAltitude(placeAddress)
                        if (latLng != null) {
                            mMap.addMarker(MarkerOptions().position(latLng).title(placeName).snippet("Open: $placeOpen\nMenu: $placeMenu"))
                        } else {
                            Toast.makeText(this@MapActivity, "Failed to find location for address: $placeAddress", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(this@MapActivity, "Error fetching places: ${error.message}", Toast.LENGTH_SHORT).show()
            })

        requestQueue.add(jsonArrayRequest)

        mMap.setInfoWindowAdapter(object : GoogleMap.InfoWindowAdapter {
            override fun getInfoWindow(marker: Marker): View? {
                return null
            }

            override fun getInfoContents(marker: Marker): View {
                val view = layoutInflater.inflate(R.layout.custom_info_window, null)

                val title = view.findViewById<TextView>(R.id.info_title)
                title.text = marker.title

                val snippet = view.findViewById<TextView>(R.id.info_snippet)
                snippet.text = marker.snippet

                return view
            }
        })

    }

    private fun getLocationFromLongitudeAltitude(longitude_altitude: String): LatLng? {
        val parts = longitude_altitude.split(",")
        if (parts.size == 2) {
            val latitude = parts[0].toDoubleOrNull()
            val longitude = parts[1].toDoubleOrNull()
            if (latitude != null && longitude != null) {
                return LatLng(latitude, longitude)
            }
        }
        return null
    }

    private fun performPlaceSearch(query: String?) {
        query?.let {
            val request = FindAutocompletePredictionsRequest.builder()
                .setQuery(query)
                .build()

            placesClient.findAutocompletePredictions(request).addOnSuccessListener { response ->
                if (response.autocompletePredictions.isNotEmpty()) {
                    val prediction = response.autocompletePredictions[0]
                    val placeId = prediction.placeId
                    fetchPlaceDetails(placeId)
                } else {
                    Toast.makeText(this, "$query", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener { exception ->
                Toast.makeText(this, "Place search failed: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
        } ?: run {
            Toast.makeText(this, "Query is null or empty", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchPlaceDetails(placeId: String?) {
        if (placeId != null) {
            val placeFields = listOf(
                Place.Field.ID,
                Place.Field.NAME,
                Place.Field.LAT_LNG,
                Place.Field.ADDRESS
            )
            val request = FetchPlaceRequest.builder(placeId, placeFields).build()
            placesClient!!.fetchPlace(request)
                .addOnSuccessListener { response: FetchPlaceResponse ->
                    val place = response.place
                    if (place.latLng != null) {
                        mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(place.latLng, 15f))
                        if (selectedMarker != null) {
                            selectedMarker!!.remove()
                        }
                        selectedMarker = mMap!!.addMarker(
                            MarkerOptions().position(place.latLng).title(place.name)
                        )
                        selectedMarker!!.showInfoWindow()
                    }
                }.addOnFailureListener { e: Exception ->
                    Toast.makeText(
                        this@MapActivity,
                        "Place details request failed: " + e.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }

    private class ImageLoadTask(url: String?, private val imageView: ImageView) :
        AsyncTask<Void, Void, Bitmap?>() {

        private val imageUrl: String? = url

        override fun doInBackground(vararg params: Void?): Bitmap? {
            var bitmap: Bitmap? = null
            try {
                val urlConnection = URL(imageUrl)
                val connection = urlConnection.openConnection() as HttpURLConnection
                connection.doInput = true
                connection.connect()
                val input: InputStream = connection.inputStream
                bitmap = BitmapFactory.decodeStream(input)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return bitmap
        }

        override fun onPostExecute(result: Bitmap?) {
            if (result != null) {
                imageView.setImageBitmap(result)
            } else {
                // Handle image load error, set a placeholder or handle accordingly
                imageView.setImageResource(R.drawable.placeholder_image)
            }
        }
    }
}

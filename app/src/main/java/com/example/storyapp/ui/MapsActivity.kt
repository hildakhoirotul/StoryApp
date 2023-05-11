package com.example.storyapp.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.storyapp.R
import com.example.storyapp.databinding.ActivityMapsBinding
import com.example.storyapp.databinding.TooltipMapsBinding
import com.example.storyapp.model.ListStoryItem
import com.example.storyapp.ui.viewmodel.MainViewModel
import com.example.storyapp.utils.Constanta.EXTRA_STORY
import com.example.storyapp.utils.Preferences
import com.example.storyapp.utils.bitmapFromURL
import com.example.storyapp.utils.getAddress
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.InfoWindowAdapter {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private val viewModel: MainViewModel by viewModels()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var pref: Preferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        pref = Preferences(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        val token = pref.token.toString()
        mMap = googleMap

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true

        val indonesia = LatLng(-2.300000, 117.540000)
        mMap.addMarker(
            MarkerOptions().position(indonesia).title("Indonesia")
        )
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(indonesia, 4f))
        viewModel.getStoryLocation("Bearer $token").observe(this) {
            for (i in it) {
                mMap.addMarker(
                    MarkerOptions().position(
                        LatLng(
                            i.lat?.toDouble() ?: 0.0, i.lon?.toDouble() ?: 0.0
                        )
                    )
                )?.tag = i
            }
        }

        mMap.setInfoWindowAdapter(this)
        mMap.setOnInfoWindowClickListener {
            val data: ListStoryItem = it.tag as ListStoryItem
            routeToDetail(data)
        }

        setMapStyle()
        getLastLocation()

    }

    private fun getLastLocation() {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) && checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    mMap.addMarker(
                        MarkerOptions().position(LatLng(it.latitude, it.longitude))
                            .title(getAddress(this, it.latitude, it.longitude))
                    )
                    mMap.animateCamera(
                        CameraUpdateFactory.newLatLngZoom(LatLng(it.latitude, it.longitude), 15f)
                    )
                }
            }
        } else {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                // Precise location access granted.
                getLastLocation()
            }
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                // Only approximate location access granted.
                getLastLocation()
            }
            else -> {
                // No location access granted.
            }
        }
    }

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this, permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun setMapStyle() {
        try {
            val success =
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
            if (!success) {
                Log.e(TAG, "Style parsing failed.")
            }
        } catch (exception: Resources.NotFoundException) {
            Log.e(TAG, "Can't find style. Error: ", exception)
        }
    }

    private fun routeToDetail(data: ListStoryItem) {
        val intent = Intent(this, DetailStoryActivity::class.java)
        intent.putExtra(EXTRA_STORY, data)
        startActivity(intent)
    }

    override fun getInfoContents(p0: Marker): View? {
        return null
    }

    override fun getInfoWindow(marker: Marker): View {
        val tooltips = TooltipMapsBinding.inflate(LayoutInflater.from(this))
        val data: ListStoryItem = marker.tag as ListStoryItem
        tooltips.username.text = data.name
        tooltips.storyImage.setImageBitmap(bitmapFromURL(this, data.photoUrl))
        return tooltips.root
    }

    companion object {
        private const val TAG = "MapsActivity"
    }
}
package com.example.storyapp.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.storyapp.R
import com.example.storyapp.databinding.ActivitySetLocationBinding
import com.example.storyapp.ui.viewmodel.UploadViewModel
import com.example.storyapp.utils.Constanta.LATITUDE
import com.example.storyapp.utils.Constanta.LONGITUDE
import com.example.storyapp.utils.getAddress
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions

@Suppress("DEPRECATION")
class SetLocationActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivitySetLocationBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val viewModel: UploadViewModel by viewModels()
    private var lat: Double = 0.0
    private var lon: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySetLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        binding.cancelButton.setOnClickListener {
            viewModel.isLocation.postValue(false)
            finish()
        }

        binding.saveButton.setOnClickListener {
            if (viewModel.isLocation.value == true) {
                val intent = Intent()
                intent.putExtra(LATITUDE, lat)
                intent.putExtra(LONGITUDE, lon)
                setResult(RESULT_OK, intent)
                finish()
            } else {
                Toast.makeText(this, getString(R.string.input_location), Toast.LENGTH_SHORT).show()
            }
        }

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true

        val indonesia = LatLng(-2.3932797, 108.8507139)
        mMap.addMarker(
            MarkerOptions().position(indonesia).title("Indonesia")
        )
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(indonesia, 4f))

        mMap.setOnMapClickListener {
            mMap.clear()
            mMap.addMarker(
                MarkerOptions().position(
                    LatLng(
                        it.latitude, it.longitude
                    )
                ).title(getAddress(this, it.latitude, it.longitude))
            )?.showInfoWindow()
            uploadLocation(it.latitude, it.longitude)
        }
        mMap.setOnPoiClickListener {
            Toast.makeText(this, it.name, Toast.LENGTH_SHORT).show()
            mMap.clear()
            mMap.addMarker(
                MarkerOptions().position(
                    LatLng(
                        it.latLng.latitude, it.latLng.longitude
                    )
                ).title(getAddress(this, it.latLng.latitude, it.latLng.longitude))
            )?.showInfoWindow()
            uploadLocation(it.latLng.latitude, it.latLng.longitude)
        }

        getLastLocation()
        setMapStyle()
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
                    uploadLocation(it.latitude, it.longitude)
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

    private fun uploadLocation(lat: Double, lon: Double) {
        val address = getAddress(this, lat, lon)
        binding.tvLoc.text = address.toString()
        viewModel.isLocation.postValue(true)
        this.lat = lat
        this.lon = lon
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

    companion object {
        private const val TAG = "SetLocationActivity"
    }
}
package com.example.storyapp.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.example.storyapp.R
import com.example.storyapp.databinding.ActivityUploadBinding
import com.example.storyapp.ui.viewmodel.UploadViewModel
import com.example.storyapp.utils.*
import com.example.storyapp.utils.Constanta.CAMERA_X_RESULT
import com.example.storyapp.utils.Constanta.LATITUDE
import com.example.storyapp.utils.Constanta.LONGITUDE
import com.example.storyapp.utils.Constanta.REQUEST_CODE_PERMISSIONS
import com.example.storyapp.utils.Constanta.REQUIRED_PERMISSIONS
import java.io.*

@Suppress("DEPRECATION")
class UploadActivity : AppCompatActivity() {

    private val viewModel: UploadViewModel by viewModels()
    private lateinit var binding: ActivityUploadBinding
    private var getFile: File? = null
    private lateinit var pref: Preferences
    private var token: String? = null
    private var resultLauncher: ActivityResultLauncher<Intent>? = null

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(
                    this, resources.getString(R.string.camera_permission), Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        pref = Preferences(this)
        token = "Bearer " + pref.token.toString()

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }

        binding.btnCameraX.setOnClickListener { startCameraX() }
        binding.btnGallery.setOnClickListener { startGallery() }
        binding.btnUpload.setOnClickListener {
            if ((binding.edtDesc?.text?.length) != 0) {
                uploadImage(getFile, binding.edtDesc?.text.toString())
            } else {
                Toast.makeText(
                    this, resources.getString(R.string.required_field), Toast.LENGTH_SHORT
                ).show()
            }
        }

        binding.setLocation.setOnClickListener {
            if (permissionGranted(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                val intent = Intent(this, SetLocationActivity::class.java)
                resultLauncher?.launch(intent)
            } else {
                ActivityCompat.requestPermissions(
                    this, arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ), Constanta.REQUEST_LOCATION_PERMISSIONS
                )
            }
        }

        viewModel.message.observe(this) {
            if (it.isNotEmpty()) {
                Toast.makeText(
                    this,
                    resources.getString(R.string.local) + viewModel.isLocation.value.toString(),
                    Toast.LENGTH_SHORT
                ).show()
                val intent = Intent(this, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
        }

        viewModel.isLoading.observe(this) {
            showLoading(it)
        }
        resultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { it ->
            if (it.resultCode == Activity.RESULT_OK) {
                it.data?.let {
                    val lat = it.getDoubleExtra(LATITUDE, 0.0)
                    val lon = it.getDoubleExtra(LONGITUDE, 0.0)
                    viewModel.isLocation.postValue(true)
                    viewModel.latitude.postValue(lat)
                    viewModel.longitude.postValue(lon)
                    binding.storyLocation.text = getAddress(this, lat, lon)
                }
            }
        }
        viewModel.isLocation.observe(this) {
            binding.storyLocation.isVisible = it
            binding.setLocation.isVisible = !it
        }
        binding.clearLocation.setOnClickListener {
            viewModel.isLocation.postValue(false)
        }
    }

    private fun startCameraX() {
        val intent = Intent(this, CameraActivity::class.java)
        launcherIntentCameraX.launch(intent)
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }

    private fun uploadImage(image: File?, desc: String) {
        if (token != null) {
            if (viewModel.isLocation.value != true) {
                viewModel.uploadStory(this, token!!, image, desc)
            } else {
                viewModel.uploadStoryWithLocation(
                    this,
                    token!!,
                    image,
                    desc,
                    viewModel.latitude.value.toString(),
                    viewModel.longitude.value.toString()
                )
            }
        } else {
            Toast.makeText(this, resources.getString(R.string.unauthorized), Toast.LENGTH_SHORT)
                .show()
        }
    }

    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CAMERA_X_RESULT) {
            val myFile = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                it.data?.getSerializableExtra("picture", File::class.java)
            } else {
                @Suppress("DEPRECATION") it.data?.getSerializableExtra("picture")
            } as? File
            val isBackCamera = it.data?.getBooleanExtra("isBackCamera", true) as Boolean
            myFile?.let { file ->
                rotateFile(file, isBackCamera)
                getFile = file
                binding.preview.setImageBitmap(BitmapFactory.decodeFile(file.path))
            }
        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg = result.data?.data as Uri
            selectedImg.let { uri ->
                val myFile = uriToFile(uri, this@UploadActivity)
                getFile = myFile
                binding.preview.setImageURI(uri)
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.loading.root.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}
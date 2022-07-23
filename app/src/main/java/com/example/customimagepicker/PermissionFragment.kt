package com.example.customimagepicker

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.example.customimagepicker.databinding.FragmentPermissionBinding

class PermissionFragment : Fragment() {
    private var _binding: FragmentPermissionBinding? = null
    private val binding get() = _binding!!

    private val requiredPermissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
    private val onSuccess: () -> Unit = {
        val directions = PermissionFragmentDirections.actionPermissionToImagepicker()
        findNavController().navigate(directions)
    }

    private val requestPermissionsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { isGranted ->
        if(isGranted.all{ it.value }) {
            onSuccess.invoke()
        }else{
            binding.button.visibility = View.VISIBLE
            Toast.makeText(requireContext(), "Accept permissions to use this application", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPermissionBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.buttonListener = View.OnClickListener {
            val intent = Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.parse("package:${activity?.packageName}")
            )
            startActivity(intent)
        }

        return binding.root
    }

    override fun onStart() {
        super.onStart()

        if(hasAllPermissions(requireContext(), requiredPermissions)) {
            onSuccess.invoke()
        }else{
            requestPermissionsLauncher.launch(requiredPermissions)
        }
    }

    private fun hasAllPermissions(context: Context, requiredPermissions: Array<String>) = requiredPermissions.all {
        ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
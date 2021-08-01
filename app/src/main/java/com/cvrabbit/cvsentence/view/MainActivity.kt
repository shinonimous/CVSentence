/*
 * Copyright (c) 2020 shinonistone
 *
 * This software is released under the MIT License.
 * https://opensource.org/licenses/mit-license.php
 *
 */

package com.cvrabbit.cvsentence.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import com.cvrabbit.cvsentence.R
import com.cvrabbit.cvsentence.databinding.ActivityMainBinding
import com.cvrabbit.cvsentence.service.OverlayService
import com.cvrabbit.cvsentence.service.OverlayView
import com.cvrabbit.cvsentence.util.constant.Constants.OVERLAY_PERMISSION_REQUEST_CODE
import com.cvrabbit.cvsentence.viewmodel.MainActivityViewModel
import com.cvrabbit.cvsentence.viewmodel.OverlayViewModelLikeObject
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "MainActivity"

@AndroidEntryPoint
class MainActivity : AppCompatActivity(){

    companion object {
        lateinit var activity: MainActivity
    }

    lateinit var binding: ActivityMainBinding
    private val mainActivityViewModel: MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.viewmodel = mainActivityViewModel
        activity = this

        // Observe FragmentNavigationRequest data
        mainActivityViewModel.navigateToFragment.observe(this, {
            it?.getContentIfNotHandled()?.let { fragmentRequest ->
                Log.d(TAG, "Fragment Replacing Method is Running")
                val transaction = supportFragmentManager.beginTransaction()
                transaction.replace(
                    R.id.container, fragmentRequest.fragment, fragmentRequest.tag
                )
                if (fragmentRequest.backStack) transaction.addToBackStack(null)
                transaction.commit()
            }
        })

        // Observe DialogFragmentNavigationRequest data
        mainActivityViewModel.navigateToDialogFragment.observe(this, {
            it?.getContentIfNotHandled()?.let { dialogFragmentRequest ->
                dialogFragmentRequest.fragment.show(
                    supportFragmentManager,
                    dialogFragmentRequest.tag
                )
            }
        })

        // Observe the OverlayView
        OverlayService.overlayView.observe(this,{
            Log.d(TAG, "overlayView observer is running")
            binding.recordButtonOn.isVisible = (it != null)
            binding.recordButtonOff.isVisible = (it == null)
        })

    }

    override fun onResume() {
        super.onResume()
        setListeners()
        mainActivityViewModel.openFirstTimeGuidance()
    }

    private fun setListeners() {
        binding.sortButton.setOnClickListener {
            mainActivityViewModel.openSortSetting()
        }
        binding.baseButton.setOnClickListener {
            mainActivityViewModel.openBaseSetting()
        }
        binding.recordButtonOn.setOnClickListener {
            OverlayService.stop(this)
        }
        binding.recordButtonOff.setOnClickListener {
            showOverlayPermissionDialog()
        }
    }

    private fun showOverlayPermissionDialog() {
        val title = getString(R.string.ma_overlaypermission_title)
        val message = getString(R.string.ma_overlaypermission_content)
        val dialog = AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("OK") { _, _ ->
                requestOverlayPermission()
            }
        if (!isOverlayGranted()){
            dialog.show()
        } else {
            showReferenceSelectionDialog()
        }
    }

    private fun showReferenceSelectionDialog() {
        if(mainActivityViewModel.ifReferenceEmpty()) {
            OverlayService.start(this)
            return
        }
        val title = getString(R.string.ma_reference_designation_title)
        val message = getString(R.string.ma_reference_designation_description)
        val spinner = initSpinner(Spinner(this))
        val dialog = AlertDialog.Builder(this)
        dialog.setTitle(title)
            .setMessage(message)
            .setView(spinner)
            .setPositiveButton(R.string.ma_reference_ok)
            { _, _ ->
                mainActivityViewModel.focusReference = spinner.selectedItem.toString()
                OverlayViewModelLikeObject.focusReference = spinner.selectedItem.toString()
                OverlayService.start(this)
            }
            .setNegativeButton(R.string.ma_reference_cancel)
            { _, _ ->
                mainActivityViewModel.focusReference = ""
                OverlayViewModelLikeObject.focusReference = ""
                OverlayService.start(this)
            }
            .show()
    }

    private fun initSpinner(spinner: Spinner): Spinner {
        // referenceSpinner
        val refArray = mainActivityViewModel.getAllReferencesAsArrayString()
        val refAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, refArray)
        spinner.adapter = refAdapter
        return spinner
    }

    /** Checks if the overlay is permitted. */
    private fun isOverlayGranted() =
        Settings.canDrawOverlays(this)

    /** Requests an overlay permission to the user if needed. */
    private fun requestOverlayPermission() {
        val intent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:$packageName")
        )
        startActivityForResult(intent, OVERLAY_PERMISSION_REQUEST_CODE)
    }

    /** Terminates the app if the user does not accept an overlay. */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == OVERLAY_PERMISSION_REQUEST_CODE) {
            if (isOverlayGranted()) {
                showReferenceSelectionDialog()
            }
        }
    }
}
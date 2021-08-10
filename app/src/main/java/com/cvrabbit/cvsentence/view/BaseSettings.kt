/*
 * Copyright (c) 2020 shinonistone
 *
 * This software is released under the MIT License.
 * https://opensource.org/licenses/mit-license.php
 *
 */

package com.cvrabbit.cvsentence.view

import android.content.Context
import android.media.AudioManager
import android.os.Bundle
import android.text.InputFilter
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.cvrabbit.cvsentence.R
import com.cvrabbit.cvsentence.databinding.FragmentBaseSettingsBinding
import com.cvrabbit.cvsentence.model.db.ReferenceEntity
import com.cvrabbit.cvsentence.util.constant.FloatingPosition
import com.cvrabbit.cvsentence.util.device.CSVExport
import com.cvrabbit.cvsentence.viewmodel.BaseSettingsViewModel
import com.cvrabbit.cvsentence.viewmodel.MainActivityViewModel
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "BaseSettings"

@AndroidEntryPoint
class BaseSettings : Fragment(R.layout.fragment_base_settings) {
    private lateinit var binding: FragmentBaseSettingsBinding
    private val mainActivityViewModel: MainActivityViewModel by activityViewModels()
    private val baseSettingsViewModel: BaseSettingsViewModel by viewModels()

    companion object {
        fun newInstance() = BaseSettings()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentBaseSettingsBinding.bind(view).apply {
            viewmodel = baseSettingsViewModel
        }
        binding.lifecycleOwner = this.viewLifecycleOwner
        initVisibility()
        initListeners()
    }

    private fun initVisibility() {
        binding.selectedWordSound.isChecked = baseSettingsViewModel.getOnSelectWordSoundSetting()
        binding.selectedMeaningSound.isChecked = baseSettingsViewModel.getOnSelectMeaningSoundSetting()
        binding.onDemandWordSound.isChecked = baseSettingsViewModel.getOnDemandWordSoundSetting()
        binding.onDemandMeaningSound.isChecked = baseSettingsViewModel.getOnDemandMeaningSoundSetting()
        binding.floatSpinner.setSelection(
            if(baseSettingsViewModel.getFloatingPosition() == FloatingPosition.RIGHT){0} else {1}
        )
    }

    private fun initListeners() {
        // Back Button Listener
        binding.baseSettingsTitle.setOnClickListener {
            mainActivityViewModel.backToList()
        }

        // Float Spinner Listener
        val floatArray = requireContext().resources.getStringArray(R.array.floating_type)
        val floatAdapter = ArrayAdapter(requireContext(), R.layout.spinner_layout, floatArray)
        floatAdapter.setDropDownViewResource(R.layout.spinner_layout_item)
        binding.floatSpinner.adapter = floatAdapter
        binding.floatSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long)
            {
                if(position == 0) { baseSettingsViewModel.saveFloatingPosition(FloatingPosition.RIGHT)
                }
                else { baseSettingsViewModel.saveFloatingPosition(FloatingPosition.LEFT)
                }
                baseSettingsViewModel.changeFloatingVisibility()
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }

        // Register Reference Button
        binding.referenceRegister.setOnClickListener {
            val reference = EditText(context)
            reference.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(50))
            val dialog = AlertDialog.Builder(requireContext())
            dialog.setTitle(requireContext().resources.getText(R.string.bsc_reference_length))
                .setView(reference)
                .setPositiveButton(R.string.bsc_reference_positive)
                { _, _ ->
                    baseSettingsViewModel.createNewReference(ReferenceEntity(reference.text.toString()))
                    Toast.makeText(activity, reference.text.toString() + requireContext().resources.getString(
                        R.string.bsc_reference_done
                    ), Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton(R.string.bsc_reference_cancel, null)
                .show()
        }

        // CSV Export Button
        binding.csvExport.setOnClickListener {
            val savedWord = CSVExport(requireContext()).saveWordsAsCSV(baseSettingsViewModel.getAllWords())
            binding.csvExportSaveDir.text = savedWord
        }

        // SoundSettings
        binding.selectedWordSound.setOnCheckedChangeListener { _, isChecked ->
            baseSettingsViewModel.saveOnSelectWordSoundSetting(isChecked)
        }
        binding.selectedMeaningSound.setOnCheckedChangeListener { _, isChecked ->
            baseSettingsViewModel.saveOnSelectMeaningSoundSetting(isChecked)
        }
        binding.onDemandWordSound.setOnCheckedChangeListener { _, isChecked ->
            baseSettingsViewModel.saveOnDemandWordSoundSetting(isChecked)
        }
        binding.onDemandMeaningSound.setOnCheckedChangeListener { _, isChecked ->
            baseSettingsViewModel.saveOnDemandMeaningSoundSetting(isChecked)
        }

        // AudioVolume Listener
        val audioManager = requireContext().getSystemService(Context.AUDIO_SERVICE) as AudioManager
        try {
            binding.volumeSeekBar.max = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
            binding.volumeSeekBar.progress = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)

            binding.volumeSeekBar.setOnSeekBarChangeListener(object :
                SeekBar.OnSeekBarChangeListener {
                override fun onStopTrackingTouch(arg0: SeekBar) {}
                override fun onStartTrackingTouch(arg0: SeekBar) {}
                override fun onProgressChanged(arg0: SeekBar, progress: Int, arg2: Boolean) {
                    audioManager.setStreamVolume(
                        AudioManager.STREAM_MUSIC,
                        progress, 0
                    )
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
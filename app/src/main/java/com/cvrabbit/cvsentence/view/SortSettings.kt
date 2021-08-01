/*
 * Copyright (c) 2020 shinonistone
 *
 * This software is released under the MIT License.
 * https://opensource.org/licenses/mit-license.php
 *
 */

package com.cvrabbit.cvsentence.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.cvrabbit.cvsentence.R
import com.cvrabbit.cvsentence.databinding.FragmentSortSettingsBinding
import com.cvrabbit.cvsentence.model.db.Word
import com.cvrabbit.cvsentence.util.calendar.CalendarOperation.getFirstMillisOfMonth
import com.cvrabbit.cvsentence.util.calendar.CalendarOperation.getLastMillisOfMonth
import com.cvrabbit.cvsentence.viewmodel.FilterPattern
import com.cvrabbit.cvsentence.viewmodel.MainActivityViewModel
import com.cvrabbit.cvsentence.viewmodel.SortPattern
import com.cvrabbit.cvsentence.viewmodel.SortSettingsViewModel
import dagger.hilt.android.AndroidEntryPoint
import io.realm.OrderedRealmCollection
import java.text.SimpleDateFormat

@AndroidEntryPoint
class SortSettings : Fragment(R.layout.fragment_sort_settings) {
    private lateinit var binding: FragmentSortSettingsBinding
    private val mainActivityViewModel: MainActivityViewModel by activityViewModels()
    private val sortSettingsViewModel: SortSettingsViewModel by viewModels()

    companion object {
        fun newInstance() = SortSettings()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_sort_settings, container, false)
        binding = FragmentSortSettingsBinding.bind(view).apply {
            viewmodel = sortSettingsViewModel
        }
        binding.lifecycleOwner = this.viewLifecycleOwner
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initVisibility()
        initListeners()
    }

    private fun initVisibility() {
        binding.dsLinearLayout.isVisible = false
        binding.registeredDateLinearLayout.isVisible = false
        binding.referenceSortSpinner.isVisible = false
    }

    private fun initListeners() {

        // When back button is clicked
        binding.sortSettingsTitle.setOnClickListener {
            //TODO applySortFilter()
            mainActivityViewModel.backToList()
        }

        // difficulty score checkbox
        binding.dsRange.setOnCheckedChangeListener { _, isChecked ->
            when(isChecked) {
                true -> {
                    binding.dsLinearLayout.isVisible = true
                }
                false -> {
                    binding.dsLinearLayout.isVisible = false
                }
            }
        }

        // registered date checkbox
        binding.registeredDateRange.setOnCheckedChangeListener { _, isChecked ->
            when(isChecked) {
                true -> {
                    binding.registeredDateLinearLayout.isVisible = true
                }
                false -> {
                    binding.registeredDateLinearLayout.isVisible = false
                }
            }
        }

        // reference checkbox
        binding.referenceSort.setOnCheckedChangeListener { _, isChecked ->
            when(isChecked) {
                true -> {
                    binding.referenceSortSpinner.isVisible = true
                }
                false -> {
                    binding.referenceSortSpinner.isVisible = false
                }
            }
        }

        // Sort Spinner
        SortPattern.setSortTypeStrArray(requireContext().resources.getStringArray(R.array.sort_type))
        val sortArray = requireContext().resources.getStringArray(R.array.sort_type)
        val sortAdapter = ArrayAdapter(requireContext(), R.layout.spinner_layout, sortArray)
        sortAdapter.setDropDownViewResource(R.layout.spinner_layout_item)
        binding.sortSpinner.adapter = sortAdapter

        // dsSpinner
        val dsArray = requireContext().resources.getStringArray(R.array.ds_type)
        val dsAdapter = ArrayAdapter(requireContext(), R.layout.spinner_layout, dsArray)
        dsAdapter.setDropDownViewResource(R.layout.spinner_layout_item)
        binding.dsRangeStartSpinner.adapter = dsAdapter
        binding.dsRangeEndSpinner.adapter = dsAdapter
        binding.dsRangeStartSpinner.setSelection(0) // Initial Set
        binding.dsRangeEndSpinner.setSelection(10) // Initial Set
        binding.dsRangeStartSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long)
            {
                val selectedStr = parent.getItemAtPosition(position)
                val startInt = selectedStr.toString().toFloat()
                val endInt = binding.dsRangeEndSpinner.selectedItem.toString().toFloat()
                if (endInt < startInt) {
                    binding.dsRangeEndSpinner.setSelection(dsArray.indexOf(selectedStr))
                }
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }
        binding.dsRangeEndSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long)
            {
                val selectedStr = parent.getItemAtPosition(position)
                val startInt = binding.dsRangeStartSpinner.selectedItem.toString().toFloat()
                val endInt = selectedStr.toString().toFloat()
                if (endInt < startInt) {
                    binding.dsRangeStartSpinner.setSelection(dsArray.indexOf(selectedStr))
                }
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }

        // regSpinner
        val regArray = sortSettingsViewModel.getRegArray()
        val regAdapter = ArrayAdapter(requireContext(), R.layout.spinner_layout, regArray)
        regAdapter.setDropDownViewResource(R.layout.spinner_layout_item)
        binding.regDateStartSpinner.adapter = regAdapter
        binding.regDateEndSpinner.adapter = regAdapter
        binding.regDateStartSpinner.setSelection(0) // Initial Set
        binding.regDateEndSpinner.setSelection(0) // Initial Set
        binding.regDateStartSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long)
            {
                val selectedStr = parent.getItemAtPosition(position).toString()
                if (selectedStr == "") {
                    binding.regDateEndSpinner.setSelection(regArray.indexOf(selectedStr))
                } else {
                    val endStrDate = binding.regDateEndSpinner.selectedItem.toString()
                    if (endStrDate == "") {
                        binding.regDateEndSpinner.setSelection(regArray.indexOf(selectedStr))
                    } else {
                        val startDate = SimpleDateFormat("yyyy/MM").parse(selectedStr)
                        val endDate = SimpleDateFormat("yyyy/MM").parse(endStrDate)
                        if (endDate < startDate) {
                            binding.regDateEndSpinner.setSelection(regArray.indexOf(selectedStr))
                        }
                    }
                }
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }
        binding.regDateEndSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long)
            {
                val selectedStr = parent.getItemAtPosition(position).toString()
                if (selectedStr == "") {
                    binding.regDateStartSpinner.setSelection(regArray.indexOf(selectedStr))
                } else {
                    val startStrDate = binding.regDateStartSpinner.selectedItem.toString()
                    if (startStrDate == "") {
                        binding.regDateStartSpinner.setSelection(regArray.indexOf(selectedStr))
                    } else {
                        val startDate = SimpleDateFormat("yyyy/MM").parse(startStrDate)
                        val endDate = SimpleDateFormat("yyyy/MM").parse(selectedStr)
                        if (endDate < startDate) {
                            binding.regDateStartSpinner.setSelection(regArray.indexOf(selectedStr))
                        }
                    }
                }
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }

        //referenceSpinner
        val refArray = sortSettingsViewModel.getAllReferencesAsArrayString()
        val refAdapter = ArrayAdapter(requireContext(), R.layout.spinner_layout, refArray)
        refAdapter.setDropDownViewResource(R.layout.spinner_layout_item)
        binding.referenceSortSpinner.adapter = refAdapter

    }

    private fun applySortFilter(): OrderedRealmCollection<Word> {
        val filterList = mutableListOf<FilterPattern>()
        if(binding.green.isChecked) {
            val filter = FilterPattern.GREEN
            filter.greenFilter = true
            filterList.add(filter)
        }
        if(binding.dsRange.isChecked) {
            val filter = FilterPattern.DS_RANGE
            filter.dsRangeFilter =
                Pair(binding.dsRangeStartSpinner.selectedItem.toString().toFloat(),
                    binding.dsRangeEndSpinner.selectedItem.toString().toFloat())
            filterList.add(filter)
        }
        if(binding.registeredDateRange.isChecked) {
            if(binding.regDateStartSpinner.selectedItem.toString() != "") {
                val filter = FilterPattern.REG_DATE_RANGE
                filter.regDateRangeFilter = Pair(getFirstMillisOfMonth(binding.regDateStartSpinner.selectedItem.toString()),
                    getLastMillisOfMonth(binding.regDateEndSpinner.selectedItem.toString()))
                filterList.add(filter)
            }
        }
        if(binding.referenceSort.isChecked) {
            val filter = FilterPattern.REFERENCE
            filter.reference = binding.referenceSortSpinner.selectedItem.toString()
            filterList.add(filter)
        }
        val filteredWords = sortSettingsViewModel.filterByFilterPattern(filterList)
        return sortSettingsViewModel.sortBySortPattern(
            SortPattern.getSortPatternByStrValue(binding.sortSpinner.selectedItem.toString()),filteredWords
        )
    }
}
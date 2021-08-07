/*
 * Copyright (c) 2020 shinonistone
 *
 * This software is released under the MIT License.
 * https://opensource.org/licenses/mit-license.php
 *
 */

package com.cvrabbit.cvsentence.view

import android.graphics.Canvas
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.cvrabbit.cvsentence.R
import com.cvrabbit.cvsentence.adapter.WordInListAdapter
import com.cvrabbit.cvsentence.viewmodel.MainActivityViewModel
import com.cvrabbit.cvsentence.viewmodel.WordsListViewModel
import com.cvrabbit.cvsentence.databinding.FragmentWordsListBinding
import com.cvrabbit.cvsentence.model.db.Word
import com.cvrabbit.cvsentence.util.device.SizeMetrics
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "WordsList"

@AndroidEntryPoint
class WordsList : Fragment(R.layout.fragment_words_list) {

    private lateinit var binding: FragmentWordsListBinding
    private val mainActivityViewModel: MainActivityViewModel by activityViewModels()
    private val wordsListViewModel: WordsListViewModel by viewModels()

    companion object {
        fun newInstance() = WordsList()
    }

    /**
     * Show fragment and initialize data binding
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_words_list, container, false)
        binding = FragmentWordsListBinding.bind(view).apply {
            viewmodel = wordsListViewModel // "viewmodel" is defined in layout xml.
        }
        binding.lifecycleOwner = this.viewLifecycleOwner
        return view
    }

    /**
     * Settings of binding and listeners
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // set firstGuidance visibility
        binding.firstGuidance.isVisible = wordsListViewModel.ifAllWordsDeleted()

        // recycler view adapter binding
        val adapter = WordInListAdapter(wordsListViewModel)
        binding.wordsList.adapter = adapter

        // set card click listener
        adapter.setOnItemClickListener(object: WordInListAdapter.OnItemClickListener {
            override fun onItemClickListener(word: Word, position: Int) {
                wordsListViewModel.updateLookUp(word.id)
                mainActivityViewModel.wordCardClicked(word)
            }
        })

        // set listener for delete
        getSwipeToDismissTouchHelper(adapter, view).attachToRecyclerView(binding.wordsList)

        // sort list
        wordsListViewModel.updateUI()
    }

    private fun getSwipeToDismissTouchHelper(adapter: WordInListAdapter, listView: View) =
        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.ACTION_STATE_IDLE,
            ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val word = adapter.getItem(viewHolder.adapterPosition)
                wordsListViewModel.deleteSpecificWord(word!!.id)
                binding.firstGuidance.isVisible = wordsListViewModel.ifAllWordsDeleted()
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                super.onChildDraw(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
                val itemView = viewHolder.itemView
                val radius = listView.context.resources.getDimensionPixelSize(R.dimen.radius_5dp).toFloat()
                val drawable = GradientDrawable()
                drawable.cornerRadius = radius
                drawable.setColor(resources.getColor(R.color.theme_color_complement, requireActivity().theme))

                val deleteIcon = AppCompatResources.getDrawable(
                    listView.context,
                    R.drawable.vector_delete
                )
                val iconMarginVertical =
                    (viewHolder.itemView.height - deleteIcon!!.intrinsicHeight) / 2

                deleteIcon.setBounds(
                    itemView.left + iconMarginVertical,
                    itemView.top + iconMarginVertical + SizeMetrics.newInstance(context!!).dpToPx(5).toInt(),
                    itemView.left + iconMarginVertical + deleteIcon.intrinsicWidth,
                    itemView.bottom - iconMarginVertical
                )
                drawable.setBounds(
                    itemView.left + SizeMetrics.newInstance(context!!).dpToPx(5).toInt(),
                    itemView.top + SizeMetrics.newInstance(context!!).dpToPx(5).toInt(),
                    itemView.right - SizeMetrics.newInstance(context!!).dpToPx(5).toInt(),
                    itemView.bottom
                )
                drawable.draw(c)
                deleteIcon.draw(c)
            }
        }
        )
}
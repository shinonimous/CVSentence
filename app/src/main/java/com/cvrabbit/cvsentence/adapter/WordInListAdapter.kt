/*
 * Copyright (c) 2020 shinonistone
 *
 * This software is released under the MIT License.
 * https://opensource.org/licenses/mit-license.php
 *
 */

package com.cvrabbit.cvsentence.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cvrabbit.cvsentence.databinding.FragmentWordInListBinding
import com.cvrabbit.cvsentence.model.db.Word
import com.cvrabbit.cvsentence.viewmodel.WordsListViewModel
import io.realm.RealmRecyclerViewAdapter

class WordInListAdapter(
    viewModel: WordsListViewModel
) :
    RealmRecyclerViewAdapter<Word, WordInListAdapter.WordInListViewHolder>(viewModel.data, true) {
    private lateinit var listener: OnItemClickListener

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    interface OnItemClickListener {
        fun onItemClickListener(word: Word, position: Int)
    }

    init {
        setHasStableIds(true)
    }

    class WordInListViewHolder(
        binding: FragmentWordInListBinding,
        private var listener: OnItemClickListener
    ): RecyclerView.ViewHolder(binding.root) {
        private var mBinding: FragmentWordInListBinding = binding
        fun bindTo(word: Word, position: Int) {
            mBinding.addedWord.text = word.word
            mBinding.root.setOnClickListener {
                listener.onItemClickListener(word, position)
            }
            mBinding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordInListViewHolder {
        setOnItemClickListener(listener) // setOnItemClickListener is run in WordsList class. So it's maybe useless.
        val inflater = LayoutInflater.from(parent.context)
        val binding = FragmentWordInListBinding.inflate(inflater, parent, false)
        return WordInListViewHolder(binding, listener)
    }

    override fun onBindViewHolder(holder: WordInListViewHolder, position: Int) {
        val word: Word? = getItem(position)
        if (word != null) {
            holder.bindTo(word, position)
        }
    }

    override fun getItemId(position: Int): Long {
        return getItem(position)?.id ?: 0
    }
}
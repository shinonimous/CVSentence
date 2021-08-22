/*
 * Copyright (c) 2020 shinonistone
 *
 * This software is released under the MIT License.
 * https://opensource.org/licenses/mit-license.php
 *
 */

package com.cvrabbit.cvsentence.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.cvrabbit.cvsentence.R
import com.cvrabbit.cvsentence.databinding.FragmentWordInListBinding
import com.cvrabbit.cvsentence.model.db.WordEntity
import com.cvrabbit.cvsentence.util.color.ColorOperator

private const val TAG = "WordAdapter"

class WordAdapter: RecyclerView.Adapter<WordAdapter.WordViewHolder>() {

    /**
     * ** How to implement the registering listener to the adapter mechanism **
     *
     * 1. The goal is to make a mechanism with which you can register the listener
     *    to the recyclerview adapter (in this case, WordAdapter class instance) on demand
     *    in a view class (in this case, WordsList class), like below.
     *
     *   adapter.setOnItemClickListener(object: WordAdapter.OnItemClickListener {
     *       override fun onItemClickListener(word: Word, position: Int) {
     *           wordsListViewModel.updateLookUp(word.id)
     *           mainActivityViewModel.wordCardClicked(word)
     *       }
     *   })
     *
     * 2. To achieve the above goal, you have to implement 3 things in the recyclerview adapter class.
     *    (1) Make setOnItemClickListener method that can be used in other classes
     *        and a member variable to hold the OnItemClickListener interface.
     *    (2) Prepare OnItemClickListener interface.
     *    (3) Bind the event listener to the interface listener function
     *        (When the prospected event occurs, the interface listener function run).
     *        mBinding.root.setOnClickListener {
     *            listener.onItemClickListener(word, position)
     *        }
     */

    private lateinit var listener: OnItemClickListener

    // (1) This is a setter for the OnItemClickListener interface used in WordsList class.
    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    // (2) This is the definition of OnItemClickListener interface
    interface OnItemClickListener {
        fun onItemClickListener(word: WordEntity, position: Int)
    }

    init {
        setHasStableIds(true)
    }

    class WordViewHolder(
        binding: FragmentWordInListBinding,
        private var listener: OnItemClickListener
    ): RecyclerView.ViewHolder(binding.root) {
        private var mBinding: FragmentWordInListBinding = binding
        // (3) Bind the event listener to the interface listener function
        fun bindTo(word: WordEntity, position: Int) {

            mBinding.addedWord.text = word.word

            mBinding.root.setOnClickListener {
                listener.onItemClickListener(word, position)
            }

            if (word.green) {
                Log.d(TAG, "${ColorOperator(mBinding.root.context).fetchColor(R.attr.colorPrimary)}")
                mBinding.addedCard.setCardBackgroundColor(
                    ColorOperator(mBinding.root.context).fetchColor(R.attr.colorPrimary)
                )
                mBinding.addedWord.setTextColor(
                    mBinding.root.context.resources.getColor(R.color.white_100, mBinding.root.context.theme)
                )
            } else {
                mBinding.addedCard.setCardBackgroundColor(
                    mBinding.root.context.resources.getColor(R.color.white_100, mBinding.root.context.theme)
                )
                mBinding.addedWord.setTextColor(
                    mBinding.root.context.resources.getColor(R.color.black_100, mBinding.root.context.theme)
                )
            }

            mBinding.executePendingBindings()
        }
    }

    private val diffCallback = object: DiffUtil.ItemCallback<WordEntity>() {
        override fun areItemsTheSame(oldItem: WordEntity, newItem: WordEntity): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: WordEntity, newItem: WordEntity): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }

    private val differ = AsyncListDiffer(this, diffCallback)

    // Observe the live data and when any difference occurs, run this method.
    fun submitList(list: List<WordEntity>) = differ.submitList(list)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = FragmentWordInListBinding.inflate(inflater, parent, false)
        return WordViewHolder(binding, listener)
    }

    override fun onBindViewHolder(holder: WordViewHolder, position: Int) {
        val word = differ.currentList[position]
        if (word != null) {
            holder.bindTo(word, position)
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun getItemId(position: Int): Long {
        return differ.currentList[position]?.id?.toLong() ?: 0
    }

    fun getItem(position: Int) : WordEntity {
        return differ.currentList[position]
    }
}
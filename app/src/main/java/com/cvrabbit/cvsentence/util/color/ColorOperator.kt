package com.cvrabbit.cvsentence.util.color

import android.content.Context
import android.util.Log
import android.util.TypedValue

private const val TAG = "ColorOperator"

class ColorOperator(private val context: Context) {

    fun fetchColor(attrValue: Int): Int {
        val typedValue = TypedValue()
        Log.d(TAG, "attrValue: $attrValue")
        context.theme.resolveAttribute(attrValue, typedValue, true)
        return typedValue.data
    }
}
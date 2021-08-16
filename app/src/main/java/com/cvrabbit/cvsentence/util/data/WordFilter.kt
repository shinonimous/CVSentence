package com.cvrabbit.cvsentence.util.data

import com.cvrabbit.cvsentence.view.MainActivity

data class WordFilter(
    var green: Boolean = false,
    var minDS: Float,
    var maxDS: Float,
    var startDate: Long,
    var endDate: Long,
    var reference: String = ""
)


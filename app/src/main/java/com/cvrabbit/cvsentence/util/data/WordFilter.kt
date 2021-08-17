package com.cvrabbit.cvsentence.util.data

data class WordFilter(
    var green: Boolean = false,
    var minDS: Float,
    var maxDS: Float,
    var startDate: Long,
    var endDate: Long,
    var reference: String = ""
)

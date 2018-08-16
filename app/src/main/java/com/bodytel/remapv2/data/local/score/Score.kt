package com.bodytel.remapv2.data.local.score

import java.io.Serializable
import java.util.*

data class Score (val sl: Int, val result: HashMap<String, String>, val date: Long): Serializable
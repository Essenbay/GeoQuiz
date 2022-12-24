package com.bigneardranch.geoquiz

import androidx.annotation.StringRes

data class Question(@StringRes val textResId: Int, val answer: Boolean, var userAnswer: Boolean? = null, var isCheated: Boolean = false){

}
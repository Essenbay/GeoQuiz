package com.bigneardranch.geoquiz

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

const val CURRENT_INDEX_KEY = "CURRENT_INDEX_KEY"
const val IS_CHEATER_KEY = "IS_CHEATER_KEY"

class QuizViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {
    private val questionBank = listOf<Question>(
        Question(R.string.question_kazakhstan, true),
        Question(R.string.question_oceans, true),
        Question(R.string.question_mideast, false),
        Question(R.string.question_africa, false),
        Question(R.string.question_americas, true),
        Question(R.string.question_asia, true)
    )

    fun getQuestionNum() = questionBank.size

    private var currentIndex: Int
        get() = savedStateHandle[CURRENT_INDEX_KEY] ?: 0
        set(value) = savedStateHandle.set(CURRENT_INDEX_KEY, value)

    val currentQuestionText: String
        get() = questionBank[currentIndex].textResId.toString()

    val currentQuestionAnswer: Boolean
        get() = questionBank[currentIndex].answer

    val currentQuestionTextResId: Int
        get() = questionBank[currentIndex].textResId

    var currentQuestionUserAnswer: Boolean?
        get() = questionBank[currentIndex].userAnswer
        set(value) {
            questionBank[currentIndex].userAnswer = value
        }
    var currentQuestionIsCheated: Boolean
        get() = questionBank[currentIndex].isCheated
        set(value) {
            questionBank[currentIndex].isCheated = value
        }

    var isAllAnswered: Boolean = true
        get() {
            isAllAnswered = true
            for (question in questionBank) {
                if (question.userAnswer == null) {
                    isAllAnswered = false
                    break
                }
            }
            return field
        }

    var userCorrectAnsNum: Int = 0
        get() {
            field = 0
            for (question in questionBank) {
                if (question.userAnswer != null && question.answer == question.userAnswer) {
                    field++
                }
            }
            return field
        }

    fun moveToNext() {
        currentIndex = (currentIndex + 1) % questionBank.size
    }

    fun moveToPrev() {
        currentIndex = if (currentIndex == 0) {
            questionBank.size - 1
        } else {
            currentIndex - 1
        }
    }

}
package com.bigneardranch.geoquiz

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import com.bigneardranch.geoquiz.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar


class MainActivity : AppCompatActivity() {
    private val quizViewModel: QuizViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding
    private val cheatLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                quizViewModel.currentQuestionIsCheated =
                    result.data?.getBooleanExtra(EXTRA_ANSWER_SHOWN, false) ?: false
            }
        }

    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.falseButton.setOnClickListener { view: View ->
            val message = checkAnswer(false)
            Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show()
            nextQuestion()
            checkIfAllAnswered(view)
        }

        binding.trueButton.setOnClickListener { view: View ->
            val message = checkAnswer(true)
            Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show()
            nextQuestion()
            checkIfAllAnswered(view)
        }

        updateAnswerButtonAble();

        binding.questionTextView.setText(
            quizViewModel.currentQuestionTextResId
        )

        binding.previousButton.setOnClickListener {
            previousQuestion()
        }
        binding.nextButton.setOnClickListener {
            nextQuestion()
        }
        binding.questionTextView.setOnClickListener {
            nextQuestion()
        }

        binding.cheatButton?.setOnClickListener {
            val answerIsTrue = quizViewModel.currentQuestionAnswer
            val intent = CheatActivity.newIntent(this@MainActivity, answerIsTrue)
            cheatLauncher.launch(intent)
        }
    }

    private fun previousQuestion() {
        quizViewModel.moveToPrev()
        val questionIndexResId = quizViewModel.currentQuestionTextResId
        binding.questionTextView.setText(questionIndexResId);
        updateAnswerButtonAble()
    }

    private fun nextQuestion() {
        quizViewModel.moveToNext()
        val questionIndexResId = quizViewModel.currentQuestionTextResId
        binding.questionTextView.setText(questionIndexResId);
        updateAnswerButtonAble()
    }

    private fun checkAnswer(userAnswer: Boolean): Int {
        quizViewModel.currentQuestionUserAnswer = userAnswer
        Log.d("MainActivity", "${quizViewModel.currentQuestionIsCheated}")
        val messageResId = when {
            quizViewModel.currentQuestionIsCheated -> R.string.judgement_toast
            userAnswer == quizViewModel.currentQuestionUserAnswer -> R.string.correct_toast
            else -> R.string.incorrect_toast
        }
        return messageResId;
    }

    private fun updateAnswerButtonAble() {
        if (quizViewModel.currentQuestionUserAnswer != null) {
            binding.falseButton.isEnabled = false
            binding.trueButton.isEnabled = false
        } else {
            binding.falseButton.isEnabled = true
            binding.trueButton.isEnabled = true
        }
    }


    private fun checkIfAllAnswered(view: View) {
        if (quizViewModel.isAllAnswered) {
            val message =
                "Done! Your result: ${quizViewModel.userCorrectAnsNum} / ${quizViewModel.getQuestionNum()}"

            Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show()
        }
    }
}
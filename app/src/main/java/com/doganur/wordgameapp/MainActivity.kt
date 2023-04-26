package com.doganur.wordgameapp

import android.animation.LayoutTransition
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.AbsoluteLayout
import android.widget.Button
import android.widget.CompoundButton
import android.widget.Toast
import android.widget.ToggleButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import androidx.core.view.updateLayoutParams
import com.doganur.wordgameapp.databinding.ActivityMainBinding
import java.lang.Exception
import kotlin.random.Random


private const val TOTAL_ROWS = 10
private const val TOTAL_COLS = 8

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {

    private var duration: Int = 5
    private lateinit var gameLayout: AbsoluteLayout
    private lateinit var binding: ActivityMainBinding
    var word = ""
    var totalScore = 0
    val buttonRows = arrayOfNulls<Array<ToggleButton?>>(TOTAL_ROWS);
    private val handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        gameLayout = binding.glGameScreen;
        gameLayout.children.forEach {
            it.visibility = View.INVISIBLE;
            it.updateLayoutParams {
                var layoutParams = this as AbsoluteLayout.LayoutParams
                // animations will start from here
                layoutParams.y = -500;
            }
        }
        gameLayout.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
        gameLayout.layoutTransition.enableTransitionType(LayoutTransition.APPEARING)
        gameLayout.layoutTransition.enableTransitionType(LayoutTransition.CHANGE_APPEARING)
        gameLayout.layoutTransition.enableTransitionType(LayoutTransition.CHANGE_DISAPPEARING)
        gameLayout.layoutTransition.enableTransitionType(LayoutTransition.DISAPPEARING)
        gameLayout.post {
            startGame(gameLayout);
        }

//

        handler.postDelayed({
            dropALetter()
        }, duration * 1000L)
//
//        //onay düğmesi için listener
        binding.btnSave.setOnClickListener { saveWord() }
//
//        //silme düğmesi için listener
        binding.btnDelete.setOnClickListener {
            word = ""
            binding.tvCombiningText.text = word
            resetButtons(false)
        }
    }

    private fun dropALetter() {
        val availableBtns = mutableListOf<ToggleButton>()
        val availableBtnsRowIndecies = mutableListOf<Int>()
        val availableBtnsColIndecies = mutableListOf<Int>()
        for (colIndex in 0 until TOTAL_COLS) {
            for ((index, row) in buttonRows.withIndex()) {
                val btn = row!![colIndex]
                if (btn != null && (btn.layoutParams as AbsoluteLayout.LayoutParams).y <= 0) {
                    availableBtns.add(btn)
                    availableBtnsColIndecies.add(colIndex)
                    availableBtnsRowIndecies.add(index)
                    break
                }
            }
        }
        if (availableBtns.isEmpty())
            return
        val index = Random.nextInt(0, availableBtns.count())
        updateButtonLocation(
            availableBtnsRowIndecies[index], availableBtnsColIndecies[index], availableBtns[index]
        )
        handler.postDelayed({ dropALetter() }, duration * 1000L)
    }

    private fun saveWord() {
        if (word.isNotEmpty() && isWordValid(word)) {
            totalScore += calculateScore(word, letterList)
            binding.tvTotalScore.text = totalScore.toString();
            hideCheckedButtons();
            resetButtons(false);
            shiftButtons();
            checkScoreAndTiming();
        } else {
            val toast = Toast.makeText(this, "Geçersiz kelime.", Toast.LENGTH_SHORT)
            toast.setGravity(Gravity.CENTER, 0, 0)
            toast.show()
        }
    }

    private fun checkScoreAndTiming() {
        if (totalScore >= 400) {
            duration = 1;
        } else if (totalScore >= 300) {
            duration = 2;
        } else if (totalScore >= 200) {
            duration = 3;
        } else if (totalScore >= 100) {
            duration = 4;
        }
    }

    private fun shiftButtons() {
        try {
            for (colIndex in 0 until TOTAL_COLS) {
                val newColButtons = arrayOfNulls<ToggleButton>(TOTAL_ROWS);
                var addIndex = 0;
                buttonRows.forEach { row ->
                    val btn = row!![colIndex]
                    // it's still in the game
                    if (btn != null && (btn.visibility == View.VISIBLE || !btn.isChecked)) {
                        newColButtons[addIndex] = btn;
                        addIndex++
                    }
                }
                for (index in 0 until TOTAL_ROWS) {
                    val btn = newColButtons[index]
                    buttonRows[index]!![colIndex] = btn;
                    if (btn != null && (btn.layoutParams as AbsoluteLayout.LayoutParams).y >= 0) updateButtonLocation(
                        index,
                        colIndex,
                        btn
                    )
                }
                Log.e("TTTTTTTTTTTTTTTT", colIndex.toString())
            }
        } catch (e: Exception) {
            var x = 2;
            var y = x - 1;
        }
    }


    private fun hideCheckedButtons() {
        buttonRows.forEach { row ->
            row?.forEach { btn ->
                if (btn != null) {
                    if (btn.isChecked) {
                        btn.visibility = View.INVISIBLE;
                    }
                }
            }
        }
    }

    private fun startGame(layout: AbsoluteLayout) {
        layoutButtonsAndFillMatrix(layout)
        showButtons()
        fillButtonsWithRandomLetters()
        setClickHandlers()
    }

    private fun setClickHandlers() {
        buttonRows.forEach { row ->
            row!!.forEach {
                val button = it!!;
                button.setOnCheckedChangeListener { compoundButton: CompoundButton, checked: Boolean ->
                    if (checked) {
                        word += compoundButton.text.toString()
                        binding.tvCombiningText.text = word
                    } else {
                        word = word.replaceFirst(compoundButton.text.toString(), "")
                        binding.tvCombiningText.text = word
                    }
                }
            }
        }
    }

    private fun fillButtonsWithRandomLetters() {
        buttonRows.forEach { it ->
            it!!.forEach {
                val value = letterList.random().value
                it!!.text = value;
                it.textOn = value;
                it.textOff = value;
            }
        }
    }

    private fun showButtons() {
        var delay = 500L;
        for (row in 0 until TOTAL_ROWS) {
            buttonRows[row]!!.forEachIndexed { index, it ->
                it!!.visibility = View.VISIBLE
                // drop starting buttons
                if (row <= 2) gameLayout.postDelayed({
                    updateButtonLocation(row, index, it);
                }, delay);
            }
            delay += 300;
        }
    }

    private fun updateButtonLocation(row: Int, col: Int, btn: Button) {
        btn.postDelayed({
            btn.updateLayoutParams {
                val btnWidth = gameLayout.width / TOTAL_COLS;
                val btnHeight = btnWidth;
                val lp: AbsoluteLayout.LayoutParams = this as AbsoluteLayout.LayoutParams;
//            lp.width = btnWidth;
//            lp.height = btnHeight;
//            lp.x = col * btnWidth;
                lp.y =
                    gameLayout.height - btnHeight - (row * btnHeight); // put it above the game board/hide it
            }
        }, 300)
    }

    private fun layoutButtonsAndFillMatrix(gameLayout: AbsoluteLayout) {
        val btnWidth = gameLayout.width / 8;
        val btnHeight = btnWidth;
        val btnsList = gameLayout.children.toList()
        var btnIndex = 0;
        for (row in 0 until TOTAL_ROWS) {
            buttonRows[row] = arrayOfNulls<ToggleButton>(TOTAL_COLS);
            for (col in 0 until TOTAL_COLS) {
                val btn = btnsList[btnIndex];
                btn.updateLayoutParams {
                    val lp: AbsoluteLayout.LayoutParams = this as AbsoluteLayout.LayoutParams;
                    lp.width = btnWidth;
                    lp.height = btnHeight;
                    lp.x = col * btnWidth;
                    lp.y = -btnHeight; // put it above the game board
                }
                btnIndex++;
                val button = btn as ToggleButton
                buttonRows[row]!![col] = button
            }
        }
    }

    //geçerli bir kelime olup olmadığını kontrol eden isWordValid fonk.
    fun isWordValid(word: String): Boolean {
        val words = listOf("RO", "ME", "KA", "SE")
        //kelimenin geçerliliği kontrol edilir ve sonuç döndürülür
        return true // ya da false
    }

    // Oyuncunun oluşturduğu kelimenin puanını hesaplayan calculateScore fonksiyonu
    fun calculateScore(word: String, letterList: List<LetterData>): Int {
        var score = 0
        // Kelime harfleri üzerinde döngü oluşturulur ve harf puanları hesaplanır
        for (letter in word) {
            val letterObject = letterList.find { it.value == letter.toString() }
            if (letterObject != null) {
                score += letterObject.score
            }
        }
        return score //toplam puan döndürülüyor
    }


    //oluşturulan kelimeyi sıfırlayan resetButtons fonk.
    private fun resetButtons(randomize: Boolean) {
        //tüm butonlar etkinleştirilir ve kelime sıfırlanır

        buttonRows.forEach { buttons ->
            buttons!!.forEach { btn ->
                if (btn?.visibility == View.VISIBLE) {
                    // randomize letters
                    if (!randomize) {
                        btn.isChecked = false
                    } else {
                        btn.postDelayed({
                            btn.text = letterList.random().value
                            btn.isChecked = false
                        }, Random.nextLong(100, 1000))
                    }
                }
            }
        }
        word = ""
        //ekran metni değiştirilecek
        binding.tvCombiningText.text = ""
    }

    /* fun checkWord(word: String, allButtonList: List<ToggleButton>) {
         for (letter in word) {
             val button = allButtonList.find { it.text == letter.toString() }
             if (button != null) {
                 val index = allButtonList.indexOf(button)
                 allButtonList[index].text = ""
                 allButtonList[index].isEnabled = false
                 if (index < allButtonList.size - 1) {
                     val nextButton = allButtonList[index + 1]
                     val animation = ObjectAnimator.ofFloat(nextButton, "translationY", -1200f, 0f)
                     animation.duration = 1500
                     animation.start()
                     allButtonList[index + 1] = button
                 }
             }
         }
     } */

}
package com.doganur.wordgameapp

import android.animation.LayoutTransition
import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import androidx.core.view.updateLayoutParams
import com.doganur.wordgameapp.databinding.ActivityMainBinding
import java.io.InputStream
import java.io.InputStreamReader
import kotlin.random.Random


private const val TOTAL_ROWS = 13
private const val TOTAL_COLS = 8

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {

    private var gameover: Boolean = false
    private var wrongWords: Int = 0
    private var duration: Int = 5
    private lateinit var gameLayout: AbsoluteLayout
    private lateinit var binding: ActivityMainBinding
    var word = ""
    var totalScore = 0
    val buttonRows = arrayOfNulls<Array<ToggleButton?>>(TOTAL_ROWS);
    private val handler = Handler()


    lateinit var lines: List<String>;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val fs: InputStream = assets.open("kelimeler.txt");
        val r = InputStreamReader(fs);
        lines = r.readLines();
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


        //onay düğmesi için listener
        binding.btnSave.setOnClickListener { saveWord() }

        //silme düğmesi için listener
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
        val newYLocation = updateButtonLocation(
            availableBtnsRowIndecies[index], availableBtnsColIndecies[index], availableBtns[index]
        )
        val btn = availableBtns[index]
        if (newYLocation < -5 /* yada 75, 100 dene*/) {
            // game over göster
            showGameOver()
        } else if (!gameover)
            handler.postDelayed({ dropALetter() }, duration * 50L)
    }

    private fun showGameOver() {
        val b = AlertDialog.Builder(this)
        b.setTitle("Game over");
        b.setMessage("Score : ${binding.tvTotalScore.text}");
        b.setCancelable(false)
        b.setPositiveButton("Ok") { _: DialogInterface, _: Int ->
            resetGame();
            gameover = false;
        }
        b.create().show();
        gameover = true;
    }

    private fun resetGame() {
        startGame(gameLayout);
    }

    private fun saveWord() {
        if (word.isNotEmpty() && isWordValid(word)) {
            totalScore += calculateScore(word, letterList)
            binding.tvTotalScore.text = totalScore.toString();
            hideCheckedButtons();
            resetButtons(false);
            shiftButtons();
            checkScoreAndTiming();
            wrongWords = 0
        } else {
            val toast = Toast.makeText(this, "Geçersiz kelime.", Toast.LENGTH_SHORT)
            toast.setGravity(Gravity.CENTER, 0, 0)
            toast.show()
            wrongWords += 1;
        }
        if (wrongWords >= 3) {
            showGameOver()
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
        wrongWords = 0
        duration = 5;
        totalScore = 0;
        binding.tvTotalScore.setText("0")
        word = ""
        handler.postDelayed({
            dropALetter()
        }, duration * 1000L)
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

    private fun updateButtonLocation(row: Int, col: Int, btn: Button): Int {
        val btnWidth = gameLayout.width / TOTAL_COLS;
        val btnHeight = btnWidth;
        val yLocation = gameLayout.height - btnHeight - (row * btnHeight)
        btn.postDelayed({
            btn.updateLayoutParams {
                val lp: AbsoluteLayout.LayoutParams = this as AbsoluteLayout.LayoutParams;

                lp.y =
                    yLocation; // put it above the game board/hide it
            }
        }, 300)
        return yLocation;
    }

    private fun layoutButtonsAndFillMatrix(gameLayout: AbsoluteLayout) {
        val btnWidth = gameLayout.width / 8;
        val btnHeight = btnWidth;
        val btnsList = gameLayout.children.toList()
        var btnIndex = 0;
        for (row in 0 until TOTAL_ROWS) {
            buttonRows[row] = arrayOfNulls<ToggleButton>(TOTAL_COLS);
            for (col in 0 until TOTAL_COLS) {
                val btn: ToggleButton = btnsList[btnIndex] as ToggleButton;
                btn.updateLayoutParams {
                    val lp: AbsoluteLayout.LayoutParams = this as AbsoluteLayout.LayoutParams;
                    lp.width = btnWidth;
                    lp.height = btnHeight;
                    lp.x = col * btnWidth;
                    lp.y = -btnHeight; // put it above the game board
                }
                btn.isChecked = false
                btnIndex++;
                buttonRows[row]!![col] = btn
            }
        }
    }

    //geçerli bir kelime olup olmadığını kontrol eden isWordValid fonk.
    private fun isWordValid(word: String): Boolean {
        return lines.contains(word)
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

}
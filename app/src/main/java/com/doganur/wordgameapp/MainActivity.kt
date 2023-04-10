package com.doganur.wordgameapp

import android.animation.ObjectAnimator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.Gravity

import android.view.View

import android.widget.Button
import android.widget.Toast

import com.doganur.wordgameapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var startButtonList : MutableList<Button>
    private lateinit var unvisibleButtonList : MutableList<Button>
    private lateinit var allButtonList : MutableList<Button>

    var word = ""
    var totalScore = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        startButtonList = mutableListOf(binding.B57, binding.B58, binding.B59, binding.B60, binding.B61, binding.B62, binding.B63, binding.B64, binding.B65, binding.B66, binding.B66, binding.B67, binding.B68,
        binding.B69, binding.B70, binding.B71, binding.B72, binding.B73, binding.B74, binding.B75, binding.B76, binding.B77, binding.B78, binding.B79, binding.B80)

        unvisibleButtonList = mutableListOf(binding.B1, binding.B2, binding.B3, binding.B4, binding.B5, binding.B6, binding.B7, binding.B8, binding.B9, binding.B10, binding.B11, binding.B12,binding.B13,
        binding.B14, binding.B15, binding.B16, binding.B17, binding.B18, binding.B18, binding.B19, binding.B20, binding.B21, binding.B22, binding.B23, binding.B24, binding.B25, binding.B26, binding.B27,
        binding.B28, binding.B29, binding.B30, binding.B31, binding.B32, binding.B33, binding.B34, binding.B35, binding.B36, binding.B37, binding.B38, binding.B39, binding.B40, binding.B41, binding.B42,
        binding.B43, binding.B44, binding.B45, binding.B46, binding.B47, binding.B48, binding.B49, binding.B50, binding.B51, binding.B52, binding.B53, binding.B54, binding.B55, binding.B56)

        allButtonList = mutableListOf(binding.B1, binding.B2, binding.B3, binding.B4, binding.B5, binding.B6, binding.B7, binding.B8, binding.B9, binding.B10, binding.B11, binding.B12,binding.B13,
            binding.B14, binding.B15, binding.B16, binding.B17, binding.B18, binding.B18, binding.B19, binding.B20, binding.B21, binding.B22, binding.B23, binding.B24, binding.B25, binding.B26, binding.B27,
            binding.B28, binding.B29, binding.B30, binding.B31, binding.B32, binding.B33, binding.B34, binding.B35, binding.B36, binding.B37, binding.B38, binding.B39, binding.B40, binding.B41, binding.B42,
            binding.B43, binding.B44, binding.B45, binding.B46, binding.B47, binding.B48, binding.B49, binding.B50, binding.B51, binding.B52, binding.B53, binding.B54, binding.B55, binding.B56,binding.B57, binding.B58, binding.B59, binding.B60, binding.B61, binding.B62, binding.B63, binding.B64, binding.B65, binding.B66, binding.B66, binding.B67, binding.B68,
            binding.B69, binding.B70, binding.B71, binding.B72, binding.B73, binding.B74, binding.B75, binding.B76, binding.B77, binding.B78, binding.B79, binding.B80,)


        unvisibleButtonList.forEach { button ->
            button.visibility = View.INVISIBLE
        }

        /* Handler().postDelayed({
            unvisibleButtonList.forEach { button ->
                button.visibility = View.VISIBLE
            }
            for(button in unvisibleButtonList) {
                val animationTwo = ObjectAnimator.ofFloat(button, "translationY",-1200f,0f)
                animationTwo.duration = 1700
                animationTwo.start()
            }
        },5000) */

        //başlangıç düşme animasyon efekti
        for (button in startButtonList) {
            val animationOne = ObjectAnimator.ofFloat(button, "translationY",-1200f,0f)
            animationOne.duration = 1500
            animationOne.start()
        }


        //harflerin butonlara rastgele dağılması alg.
        startButtonList.shuffle()
        for (i in startButtonList.indices) {
            startButtonList[i].text = letterList[i].value
        }

        // Butonların etkinleştirilmesi ve iptal edilmesi
        for (button in startButtonList) {
            button.setOnClickListener {
                if (button.isEnabled) {
                    word += button.text.toString()
                    binding.tvCombiningText.text = word

                    button.isEnabled = false

                }
            }
            button.setOnClickListener {
                if (!button.isEnabled) {
                    word = word.replace(button.text.toString(), "")
                    binding.tvCombiningText.text = word

                    button.isEnabled = true

                }
            }
        }

        //butonlara tıkladığımda atanacak harfler alg.
        for (button in startButtonList) {
            button.setOnClickListener {
                word += button.text //tıklanan butonun harfini kelimeye ekle

                binding.tvCombiningText.text = word //kelimeyi ekranda göstermek için
            }
        }

        // Butonların etkinleştirilmesi ve iptal edilmesi
        for (button in startButtonList) {
            button.setOnClickListener {
                if (button.isEnabled) {
                    word += button.text.toString()
                    binding.tvCombiningText.text = word

                    button.isEnabled = false

                }
            }
            button.setOnLongClickListener {
                if (!button.isEnabled) {
                    word = word.replace(button.text.toString(), "")
                    binding.tvCombiningText.text = word

                    button.isEnabled = true

                    true
                } else {
                    false
                }
            }
        }

        //onay düğmesi için listener
        binding.btnSave.setOnClickListener {
            if ( word.isNotEmpty() && isWordValid(word)) {
                totalScore += calculateScore(word, letterList)

                resetButtons(allButtonList)
            } else {
                val toast = Toast.makeText(this, "Geçersiz kelime.", Toast.LENGTH_SHORT)
                toast.setGravity(Gravity.CENTER, 0, 0)
                toast.show()
            }
        }

        //silme düğmesi için listener
        binding.btnDelete.setOnClickListener {
            word = ""
            binding.tvCombiningText.text = word

            resetButtons(allButtonList)
        }
    }

    //geçerli bir kelime olup olmadığını kontrol eden isWordValid fonk.
    fun isWordValid(word : String) : Boolean {
        //kelimenin geçerliliği kontrol edilir ve sonuç döndürülür
        return true // ya da false
    }

    // Oyuncunun oluşturduğu kelimenin puanını hesaplayan calculateScore fonksiyonu
    fun calculateScore(word: String, letterList : List<LetterData>) : Int {
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
    fun resetButtons(startButtonList: List<Button>) {
        //tüm butonlar etkinleştirilir ve kelime sıfırlanır
        for (button in startButtonList) {
            button.isEnabled = true
        }
        word = ""
        //ekran metni değiştirilecek
        binding.tvCombiningText.text = ""
    }

   /* fun checkWord(word: String, allButtonList: List<Button>) {
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
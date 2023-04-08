package com.doganur.wordgameapp

import android.animation.ObjectAnimator
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.Button
import android.widget.GridLayout
import android.widget.TextView
import com.doganur.wordgameapp.databinding.ActivityMainBinding
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var buttonList : MutableList<Button>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


        buttonList = mutableListOf(binding.B57, binding.B58, binding.B59, binding.B60, binding.B61, binding.B62, binding.B63, binding.B64, binding.B65, binding.B66, binding.B66, binding.B67, binding.B68,
        binding.B69, binding.B70, binding.B71, binding.B72, binding.B73, binding.B74, binding.B75, binding.B76, binding.B77, binding.B78, binding.B79, binding.B80)

        for (button in buttonList) {

            val animationOne = ObjectAnimator.ofFloat(button, "translationY",-1200f,0f)
            animationOne.duration = 1700
            animationOne.start()
        }




    }
}
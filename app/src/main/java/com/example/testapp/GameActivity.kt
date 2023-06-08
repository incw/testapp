package com.example.testapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.viewpager2.widget.ViewPager2
import com.example.testapp.databinding.ActivityGameBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class GameActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGameBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityGameBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        bets()

        val images1 = listOf(
            R.drawable.chip1,
            R.drawable.chip10,
            R.drawable.chip100,
            R.drawable.chip25,
            R.drawable.chip5,
            R.drawable.chip500,
        )


        val imagePager: ViewPager2 = findViewById(R.id.image_first_spin)
        val imageAdapter = ImagePagerAdapter(images1.shuffled())
        imagePager.adapter = imageAdapter
        val initPos = Int.MAX_VALUE / 2 - Int.MAX_VALUE / 2 % images1.shuffled().size
        imagePager.setCurrentItem(initPos, true)
        imagePager.isUserInputEnabled = false

        val imagePager1: ViewPager2 = findViewById(R.id.image_second_spin)
        val imageAdapter1 = ImagePagerAdapter(images1.shuffled())
        imagePager1.adapter = imageAdapter1
        val initPos1 = Int.MAX_VALUE / 2 - Int.MAX_VALUE / 2 % images1.shuffled().size
        imagePager1.setCurrentItem(initPos1, true)
        imagePager1.isUserInputEnabled = false

        val imagePager2: ViewPager2 = findViewById(R.id.image_last_spin)
        val imageAdapter2 = ImagePagerAdapter(images1.shuffled())
        imagePager2.adapter = imageAdapter2
        val initPos2 = Int.MAX_VALUE / 2 - Int.MAX_VALUE / 2 % images1.shuffled().size
        imagePager2.setCurrentItem(initPos2, true)
        imagePager2.isUserInputEnabled = false

        binding.buttonStartSpin.setOnClickListener {
            val betValue = binding.countMoneyBet.text.toString().toInt()
            val totalValue = binding.countMoney.text.toString().toInt()
            if (betValue > totalValue) {
                Toast.makeText(this, "No coins", Toast.LENGTH_SHORT).show()
            } else if (betValue == 0) {
                Toast.makeText(this, "Min coins: 1", Toast.LENGTH_SHORT).show()
            } else {
                with(binding){
                    buttonStartSpin.isEnabled = false

                    imageButtonDown.isEnabled = false

                    imageButtonUp.isEnabled = false

                }

                val handler = Handler()
                val runnable = object : Runnable {
                    override fun run() {

                        val nextPosition = imagePager.currentItem + 1
                        imagePager.setCurrentItem(nextPosition, true)
                        handler.postDelayed(this, 50L)
                    }
                }
                handler.postDelayed(runnable, 50L)
                handler.postDelayed({
                    handler.removeCallbacks(runnable)
                }, 1400L)

                val handler2 = Handler()
                val runnable2 = object : Runnable {
                    override fun run() {
                        val nextPosition = imagePager2.currentItem + 1
                        imagePager2.setCurrentItem(nextPosition, true)
                        handler2.postDelayed(this, 50L)
                    }
                }
                handler2.postDelayed(runnable2, 50L)
                handler2.postDelayed({
                    handler2.removeCallbacks(runnable2)
                }, 2000L)

                val handler3 = Handler()
                val runnable3 = object : Runnable {
                    override fun run() {
                        val nextPosition = imagePager1.currentItem + 1
                        imagePager1.setCurrentItem(nextPosition, true)
                        handler3.postDelayed(this, 50L)
                    }
                }
                handler3.postDelayed(runnable3, 50L)
                handler3.postDelayed({
                    randomBet()
                    with(binding){
                        buttonStartSpin.isEnabled = true
                        imageButtonDown.isEnabled = true
                        imageButtonUp.isEnabled = true
                    }
                    handler3.removeCallbacks(runnable3)
                }, 1750L)
            }

        }
    }


    private fun bets() {
        val tv = binding.countMoneyBet
        val buttonPlus = binding.imageButtonUp
        val buttonMinus = binding.imageButtonDown

        buttonPlus.setOnClickListener {
            val animation = AnimationUtils.loadAnimation(this, R.anim.click_anim)
            it.startAnimation(animation)
            val currentValue = tv.text.toString().toInt()
            val newValue = currentValue + 1
            tv.text = String.format("%d", newValue)
        }
        buttonMinus.setOnClickListener {
            val animation = AnimationUtils.loadAnimation(this, R.anim.click_anim)
            it.startAnimation(animation)
            val currentValue = tv.text.toString().toInt()
            if (currentValue > 0) {
                val newValue = currentValue - 1
                tv.text = String.format("%d", newValue)
            }
        }
        binding.buttonStartSpin.setOnClickListener {
            val animation = AnimationUtils.loadAnimation(this, R.anim.click_anim)
            it.startAnimation(animation)
        }
    }

    private fun randomBet() {
        val betValue = binding.countMoneyBet.text.toString().toInt()
        val totalValue = binding.countMoney.text.toString().toInt()
        val randomInt = (2..4).random()
        val randomBoolean = listOf(true, false).random()

        if (randomBoolean) {
            val winning = randomInt * binding.countMoneyBet.text.toString().toInt()
            binding.countMoney.text = String.format("%d", totalValue + winning)
            Toast.makeText(this, "Win: + $winning coins", Toast.LENGTH_LONG).show()
        } else {
            binding.countMoney.text = String.format("%d", totalValue - betValue)
            Toast.makeText(this, "Loose: - $betValue coins", Toast.LENGTH_LONG).show()
        }
    }
}
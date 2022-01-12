package com.example.testcoroutines

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.testcoroutines.databinding.ActivityMainBinding
import com.example.testcoroutines.runBlocking_launch_withContext_async_doAsync.DiffMethodTestActivity
import com.example.testcoroutines.test.TestActivity

class MainActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.test.setOnClickListener(this)
        binding.runBlockingLaunchWithContextAsyncDoAsync.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.test -> {
                val intent =
                    Intent(this@MainActivity, TestActivity::class.java)
                startActivity(intent)
            }
            R.id.runBlocking_launch_withContext_async_doAsync -> {

                val intent =
                    Intent(this@MainActivity, DiffMethodTestActivity::class.java)
                startActivity(intent)
            }
        }
    }
}
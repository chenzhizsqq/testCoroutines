package com.example.testcoroutines

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.testcoroutines.databinding.ActivityTestViewModelFactoryBinding
import com.example.testcoroutines.test.TestViewModel
import com.example.testcoroutines.testViewModelFactory.TestViewModel2
import com.example.testcoroutines.testViewModelFactory.TestViewModel2Factory

class TestViewModelFactoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTestViewModelFactoryBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTestViewModelFactoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        this.title = "带上ViewModelFactory的Json获取"

        binding.normalTest.setOnClickListener {
            //viewModel连接ViewModelProvider，普通创建
            val viewModel = ViewModelProvider(this)[TestViewModel::class.java]
            Toast.makeText(baseContext, viewModel.postsDataList.toString(), Toast.LENGTH_SHORT)
                .show()
        }


        binding.factoryTest.setOnClickListener {
            //viewModel连接ViewModelProvider，带上ViewModelFactory的创建
            val viewModel2 =
                ViewModelProvider(
                    this, TestViewModel2Factory(1)
                )[TestViewModel2::class.java]
            Toast.makeText(baseContext, viewModel2.test.toString(), Toast.LENGTH_SHORT).show()
        }

    }
}
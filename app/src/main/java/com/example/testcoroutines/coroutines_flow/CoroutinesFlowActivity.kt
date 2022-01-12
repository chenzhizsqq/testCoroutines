package com.example.testcoroutines.coroutines_flow

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.testcoroutines.databinding.ActivityCoroutinesFlowBinding

class CoroutinesFlowActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCoroutinesFlowBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCoroutinesFlowBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}
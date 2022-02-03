package com.example.testcoroutines

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.testcoroutines.databinding.ActivityTestScopeBinding
import kotlinx.coroutines.*

//https://stackoverflow.com/questions/65008486/globalscope-vs-coroutinescope-vs-lifecyclescope
class TestScopeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTestScopeBinding

    private lateinit var jobGlobalScopeWithIO: Job
    private lateinit var jobGlobalScopeWithMain: Job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTestScopeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.e(TAG, "test start !!!!!!!!!!!!!!!!!!!!!!!")

        binding.printGlobalScopeWithIO.setOnClickListener { printGlobalScopeWithIO() }
        binding.printGlobalScopeWithMain.setOnClickListener { printGlobalScopeWithMain() }
        binding.stopGlobalScope.setOnClickListener {
            jobGlobalScopeWithIO.cancel()
            jobGlobalScopeWithMain.cancel()
        }

        binding.printCoroutineScope.setOnClickListener { printCoroutineScope() }
        binding.printCoroutineScopeWithMain.setOnClickListener { printCoroutineScopeWithMain() }
        binding.printLifecycleScope.setOnClickListener { printLifecycleScope() }
        binding.printLifecycleScopeWithIO.setOnClickListener { printLifecycleScopeWithIO() }
    }

    //返回到主目录上
    override fun onSupportNavigateUp(): Boolean {
        Log.e(TAG, "test over !!!!!!!!!!!!!!!!!!!!!!!")
        onBackPressed()
        finish()
        return true
    }

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    override fun onPause() {
        super.onPause()

        Log.e(TAG, "onPause: test onPause !!!!!!!!!!!!!!!!!!!!!!!")

        /**
         * coroutineScope 是可以取消
         * coroutineScope.cancel()
         */
        coroutineScope.cancel()

        /**
         * GlobalScope是不能取消的，一旦操作下面的的代码，就马上Error
         * GlobalScope.cancel()
         */
    }

    private fun printGlobalScopeWithIO() {
        jobGlobalScopeWithIO = GlobalScope.launch(Dispatchers.IO) {
            while (isActive) {
                delay(1000)
                Log.e("CoroutineDemo", "[GlobalScope-IO]  ${Thread.currentThread().name}!")
            }
        }
    }

    private fun printGlobalScopeWithMain() {
        jobGlobalScopeWithMain = GlobalScope.launch(Dispatchers.Main) {
            while (isActive) {
                delay(1000)
                Log.e("CoroutineDemo", "[GlobalScope-Main]  ${Thread.currentThread().name}!")
            }
        }
    }

    private fun printCoroutineScope() = coroutineScope.launch {
        while (isActive) {
            delay(1000)
            Log.e("CoroutineDemo", "[CoroutineScope]  ${Thread.currentThread().name}!")
        }
        Log.e("CoroutineDemo", "[CoroutineScope] I'm exiting!")
    }

    private fun printCoroutineScopeWithMain() = coroutineScope.launch(Dispatchers.Main) {
        while (isActive) {
            delay(1000)
            Log.e("CoroutineDemo", "[CoroutineScope-Main]  ${Thread.currentThread().name}!")
        }
        Log.e("CoroutineDemo", "[CoroutineScope-Main] I'm exiting!")
    }

    private fun printLifecycleScopeWithIO() = lifecycleScope.launch(Dispatchers.IO) {
        while (isActive) {
            delay(1000)
            Log.e("CoroutineDemo", "[LifecycleScope-IO]  ${Thread.currentThread().name}!")
        }
        Log.e("CoroutineDemo", "[LifecycleScope-IO]  I'm exiting!")
    }

    private fun printLifecycleScope() = lifecycleScope.launch {
        while (isActive) {
            delay(1000)
            Log.e("CoroutineDemo", "[LifecycleScope]  ${Thread.currentThread().name}!")
        }
        Log.e("CoroutineDemo", "[LifecycleScope] I'm exiting!")
    }


    companion object {
        private const val TAG = "TestScopeActivity"
    }
}
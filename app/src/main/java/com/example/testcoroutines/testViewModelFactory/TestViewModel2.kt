package com.example.testcoroutines.testViewModelFactory

import androidx.lifecycle.ViewModel

/**
 * 对应带上参数的ViewModel
 */
class TestViewModel2(testInput: Int) : ViewModel() {
    val test = testInput
}


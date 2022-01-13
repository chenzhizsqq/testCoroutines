package com.example.testcoroutines.test

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider


class TestViewModel2Factory(private val testInput: Int) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(TestViewModel2::class.java)) {
            TestViewModel2(this.testInput) as T
        } else {
            throw IllegalArgumentException("ViewModel Not Found")
        }
    }
}
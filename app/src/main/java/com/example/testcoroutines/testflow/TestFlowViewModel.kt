package com.example.testcoroutines.testflow

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * 对应的ViewModel
 */
class TestFlowViewModel : ViewModel() {
    //专门对应json数据中的TestFlowData数据List
    private val _testFlowDataList = MutableLiveData<List<TestFlowData>>()

    val testFlowDataList: LiveData<List<TestFlowData>> = _testFlowDataList


    fun setFlowData(listTestFlowData: List<TestFlowData>) {
        _testFlowDataList.value = listTestFlowData
    }
}
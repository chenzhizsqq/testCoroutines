package com.example.testcoroutines.test

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * 对应test的ViewModel
 */
class TestViewModel : ViewModel() {

    //专门对应json数据中的posts数据List
    val postsDataList = MutableLiveData<List<PostsData>>()


    //观察是否正在读取数据
    var isLoading = MutableLiveData<Boolean>()
}
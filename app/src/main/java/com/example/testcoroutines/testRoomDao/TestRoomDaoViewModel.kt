package com.example.testcoroutines.testRoomDao

import android.content.Context
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn

class TestRoomDaoViewModel(context: Context) : ViewModel() {

    private val mPostDao: PostDao by lazy {
        PostDatabase.getInstance(context).postDao()
    }

    fun getListFlow(): Flow<List<Post>> {
        return mPostDao
            .getAllFlow()
            .flowOn(Dispatchers.IO)
    }
}
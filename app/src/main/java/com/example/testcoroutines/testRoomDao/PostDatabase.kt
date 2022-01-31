package com.example.testcoroutines.testRoomDao

import androidx.room.Database
import androidx.room.RoomDatabase


//数据库的接口
@Database(entities = [Post::class], version = 1)
abstract class PostDatabase : RoomDatabase() {
    abstract fun postDao(): PostDao
}
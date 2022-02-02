package com.example.testcoroutines.testRoomDao

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


//数据库的接口
@Database(entities = [Post::class], version = 1)
abstract class PostDatabase : RoomDatabase() {
    abstract fun postDao(): PostDao

    companion object {
        private const val DATABASE_NAME = "Posts_db"
        private lateinit var mPostDatabase: PostDatabase

        //注意：如果您的应用在单个进程中运行，在实例化 AppDatabase 对象时应遵循单例设计模式。
        //每个 RoomDatabase 实例的成本相当高，而您几乎不需要在单个进程中访问多个实例
        fun getInstance(context: Context): PostDatabase {
            if (!this::mPostDatabase.isInitialized) {
                //创建的数据库的实例
                mPostDatabase = Room.databaseBuilder(
                    context.applicationContext,
                    PostDatabase::class.java,
                    DATABASE_NAME
                ).build()
            }
            return mPostDatabase
        }

    }
}
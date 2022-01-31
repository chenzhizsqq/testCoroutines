package com.example.testcoroutines.testRoomDao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query


//数据库各种操作
@Dao
interface PostDao {
    @Query("SELECT * FROM posts")
    fun getAll(): List<Post>

    @Insert
    fun insertAll(vararg post: Post)

    @Delete
    fun delete(post: Post)
}
package com.example.testcoroutines.testRoomDao

import androidx.room.*


//数据库各种操作
@Dao
interface PostDao {
    @Query("SELECT * FROM posts")
    suspend fun getAll(): List<Post>

    @Query("select * from posts where id = :id")
    suspend fun getSelect(id: Int): List<Post>

    @Insert(onConflict = OnConflictStrategy.REPLACE)    //找到相同的ID序列号，就直接替换
    suspend fun insert(post: Post)

    @Insert(onConflict = OnConflictStrategy.REPLACE)    //找到相同的ID序列号，就直接替换
    suspend fun insertAll(postList: List<Post>)

    @Query("SELECT COUNT(*) FROM posts")
    suspend fun count(): Long

    @Query("DELETE FROM posts")
    suspend fun deleteAll()

    @Delete
    suspend fun delete(post: Post)
}
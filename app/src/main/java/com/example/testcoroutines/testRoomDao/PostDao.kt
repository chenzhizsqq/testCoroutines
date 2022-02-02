package com.example.testcoroutines.testRoomDao

import androidx.room.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged


//数据库各种操作
@Dao
interface PostDao {
    @Query("SELECT * FROM posts")
    suspend fun getAll(): List<Post>

    @Query("SELECT * FROM posts")
    fun getAllFlow(): Flow<List<Post>>

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


    @ExperimentalCoroutinesApi
    suspend fun getAllDistinctUntilChanged() = getAllFlow().distinctUntilChanged()
}
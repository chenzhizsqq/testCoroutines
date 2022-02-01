package com.example.testcoroutines.testRoomDao

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.example.testcoroutines.databinding.ActivityTestRoomDaoBinding
import kotlinx.coroutines.*

class TestRoomDaoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTestRoomDaoBinding

    private lateinit var db: PostDatabase
    private lateinit var posts: List<Post>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTestRoomDaoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        this.title = "Room Dao 普通使用"

        //db初始化
        db = Room.databaseBuilder(
            applicationContext,
            PostDatabase::class.java, DATABASE_NAME
        )
            .build()

        binding.dataGet.setOnClickListener { dataGet() }
        binding.dataGetSelect.setOnClickListener { dataGetSelect() }
        binding.dataInsert.setOnClickListener { dataInsert() }
        binding.dataInsertArray.setOnClickListener { dataInsertList() }
        binding.dataDeleteAll.setOnClickListener { dataDeleteAll() }
        binding.dataDelete.setOnClickListener { dataDelete() }

    }


    private fun dataGetSelect() {
        CoroutineScope(Dispatchers.IO + exceptionHandler).launch {

            val postDao = db.postDao()

            posts = postDao.getSelect(1)
            Log.e(TAG, "onCreate: posts:$posts")

            withContext(Dispatchers.Main) {
                binding.resultsTextview.text = posts.toString()
            }
        }
    }


    private fun dataDelete() {
        CoroutineScope(Dispatchers.IO + exceptionHandler).launch {

            val postDao = db.postDao()

            val newPost = Post(1, "1", "1")
            postDao.delete(newPost)

            posts = postDao.getAll()
            Log.e(TAG, "onCreate: posts:$posts")

            withContext(Dispatchers.Main) {
                binding.resultsTextview.text = posts.toString()
            }
        }
    }


    private fun dataDeleteAll() {
        CoroutineScope(Dispatchers.IO + exceptionHandler).launch {

            val postDao = db.postDao()
            postDao.deleteAll()

            posts = postDao.getAll()
            Log.e(TAG, "onCreate: posts:$posts")

            withContext(Dispatchers.Main) {
                binding.resultsTextview.text = posts.toString()
            }
        }
    }


    private fun dataGet() {
        CoroutineScope(Dispatchers.IO + exceptionHandler).launch {

            val postDao = db.postDao()

            posts = postDao.getAll()
            Log.e(TAG, "onCreate: posts:$posts")

            withContext(Dispatchers.Main) {
                binding.resultsTextview.text = posts.toString()
            }
        }
    }


    private fun dataInsert() {

        CoroutineScope(Dispatchers.IO + exceptionHandler).launch {

            val postDao = db.postDao()

            var newPost = Post(1, "1", "1")
            postDao.insert(newPost)

            newPost = Post(2, "2", "2")
            postDao.insert(newPost)

            newPost = Post(3, "3", "3")
            postDao.insert(newPost)

            posts = postDao.getAll()
            Log.e(TAG, "onCreate: posts:$posts")

            withContext(Dispatchers.Main) {
                binding.resultsTextview.text = posts.toString()

            }
        }
    }


    private fun dataInsertList() {

        CoroutineScope(Dispatchers.IO + exceptionHandler).launch {

            val postDao = db.postDao()

            val postList = arrayListOf<Post>()

            postList.add(Post(4, "4", "4"))
            postList.add(Post(5, "5", "5"))

            postDao.insertAll(postList)

            posts = postDao.getAll()
            Log.e(TAG, "onCreate: posts:$posts")

            withContext(Dispatchers.Main) {
                binding.resultsTextview.text = posts.toString()

            }
        }
    }

    //有意外发生时的对应线程
    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Log.e(TAG, "throwable: $throwable")
        binding.error.text = throwable.toString()
    }


    companion object {

        private const val TAG = "TestRoomDaoActivity"
        private const val DATABASE_NAME = "Posts_db"
    }
}
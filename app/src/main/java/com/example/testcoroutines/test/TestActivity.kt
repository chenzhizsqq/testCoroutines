package com.example.testcoroutines.test

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.testcoroutines.databinding.ActivityTestBinding
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

//拉下刷新，拉上更新 mvvm例子
class TestActivity : AppCompatActivity() {
    private val TAG = "TestActivity"
    private lateinit var binding: ActivityTestBinding

    //Adapter被绑定的对象，用作被recyclerview的绑定
    private val testPostsAdapter = TestPostsAdapter()

    //private val viewModel = TestViewModel()       //不建议的创建模式
    lateinit var viewModel: TestViewModel          //建议的创建模式


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        this.title = "拉下刷新，拉上更新 mvvm例子"

        //viewModel连接ViewModelProvider，普通创建
        viewModel = ViewModelProvider(this).get(TestViewModel::class.java)


        binding.jsonGetPostsButton.setOnClickListener {
            testPostsAdapter.notifyDatClearData()
            viewModel.isLoading.value = true
            jsonGetAllPosts()
        }

        //绑定recyclerview的adapter
        binding.recyclerview.adapter = testPostsAdapter

        //observe观察。这里意思就是movieLiveData被观察中，一旦postsLiveData接收数据，就会做出相对应的操作
        viewModel.postsDataList.observe(this, {
            testPostsAdapter.notifyDataAddData(it as ArrayList<PostsData>)
        })

        //观察是否正在读取数据，做出不同的操作
        viewModel.isLoading.observe(this, {
            if (viewModel.isLoading.value == true) {
                binding.isLoading.visibility = View.VISIBLE
            } else {
                binding.isLoading.visibility = View.GONE
            }
        })

        //添加recyclerview行数的管理器
        val linearLayoutManager = LinearLayoutManager(this)
        binding.recyclerview.layoutManager = linearLayoutManager

        //右下的一个浮动添加图标
        val fab: ExtendedFloatingActionButton = binding.extendedFAB

        viewModel.isLoading.value = false
        //添加滚动监听，获取更多数据
        binding.recyclerview.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            //滚动完时
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                //检测当前数据，做出对应的操作
                if (viewModel.postsDataList.value?.isNotEmpty() == true) {
                    val totalItemCount = recyclerView.layoutManager?.itemCount
                    val lastVisibleItemPosition: Int =
                        linearLayoutManager.findLastVisibleItemPosition()
                    //向上滚动，滚到最后一个后，添加发信
                    if (totalItemCount != null) {
                        if (viewModel.isLoading.value == false
                            && totalItemCount == lastVisibleItemPosition + 1
                        ) {
                            viewModel.isLoading.value = true
                            Log.d(TAG, "onScrollStateChanged: 滚动，获取更多 : jsonGetPosts(1)")
                            jsonGetEachPosts(1)
                        }
                    }
                } else {

                    if (viewModel.isLoading.value == false) {
                        viewModel.isLoading.value = true
                        Log.d(TAG, "滚动，空数据时的状态")
                        jsonGetFirstPosts(5)
                    }
                }
            }

            //滚动时候
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                // Scrolling up　收缩
                if (dy > 10 && fab.isExtended) {
                    fab.shrink()
                }

                // Scrolling down　如果在上面，就扩张
                if (dy < -10 && !fab.isExtended) {
                    fab.extend()
                }

                // 如果时第一个，扩张
                if (!recyclerView.canScrollVertically(-1)) {
                    fab.extend()
                }
            }
        })

        //向下拉动，添加拉动监听，重新获取数据
        binding.swipeRefreshLayout.setOnRefreshListener {
            binding.swipeRefreshLayout.isRefreshing = false
            testPostsAdapter.notifyDatClearData()
            arrayIndex = 0
            Log.d(TAG, "onCreate: 重新获取数据 : jsonGetPosts(5)")
            viewModel.isLoading.value = true
            jsonGetFirstPosts(5)
        }
    }

    //有意外发生时的对应线程
    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Log.e(TAG, "throwable: $throwable")
    }

    var job: Job? = null

    //https://github.com/chenzhizsqq/testJson/blob/main/db.json，"posts"那组数
    private fun jsonGetAllPosts() {
        arrayIndex = 0
        Log.e(TAG, "jsonGetAllPosts: !!!")
        val retrofit = Retrofit.Builder()
            .baseUrl("https://my-json-server.typicode.com/")
            .addConverterFactory(GsonConverterFactory.create()) //把json转为gson，才可以直接用LiveData.postValue
            .build()

        val service = retrofit.create(GithubJsonService::class.java)

        viewModel.isLoading.value = true
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val response = service.getResponsePosts()
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {

                    try {

                        //因为上面用上了.addConverterFactory，才可以直接联系LiveData.postValue。发送数据给postsLiveData
                        viewModel.postsDataList.postValue(response.body())

                        viewModel.isLoading.value = false

                        arrayIndex = response.body()?.size ?: 0
                    } catch (e: Exception) {
                        Log.e(TAG, "jsonGetPosts: ", e)
                    }
                } else {
                    Log.e(TAG, "jsonGetPosts: error:" + response.message())
                }
            }
        }
    }

    var arrayIndex = 0

    //初始化时，获取的数据
    private fun jsonGetFirstPosts(add: Int) {
        arrayIndex = 0

        val retrofit = Retrofit.Builder()
            .baseUrl("https://my-json-server.typicode.com/")
            .addConverterFactory(GsonConverterFactory.create()) //把json转为gson，才可以直接用LiveData.postValue
            .build()

        val service = retrofit.create(GithubJsonService::class.java)

        CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val response = service.getResponsePosts()
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    var lastIndex = arrayIndex + add
                    if (lastIndex > response.body()!!.size) {
                        lastIndex = response.body()!!.size
                    }

                    Log.e(TAG, "jsonGetPosts: lastIndex :" + lastIndex)
                    val mArrayList = ArrayList(response.body()?.subList(arrayIndex, lastIndex))

                    //因为上面用上了.addConverterFactory，才可以直接联系LiveData.postValue。发送数据给postsLiveData
                    viewModel.postsDataList.postValue(mArrayList)

                    viewModel.isLoading.value = false
                    arrayIndex = lastIndex
                } else {
                    Log.e(TAG, "jsonGetPosts: error:" + response.message())
                }
            }
        }
    }

    //初始化后，获取的个别数据
    private fun jsonGetEachPosts(add: Int) {

        val retrofit = Retrofit.Builder()
            .baseUrl("https://my-json-server.typicode.com/")
            .addConverterFactory(GsonConverterFactory.create()) //把json转为gson，才可以直接用LiveData.postValue
            .build()

        val service = retrofit.create(GithubJsonService::class.java)

        CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val response = service.getResponsePosts()
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    if (arrayIndex >= response.body()!!.size) {
                        Toast.makeText(
                            this@TestActivity,
                            "arrayIndex >  response.body()!!.size",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        var lastIndex = arrayIndex + add
                        if (lastIndex > response.body()!!.size) {
                            lastIndex = response.body()!!.size
                        }

                        Log.e(TAG, "jsonGetPosts: lastIndex :" + lastIndex)
                        val mArrayList = ArrayList(response.body()?.subList(arrayIndex, lastIndex))

                        //因为上面用上了.addConverterFactory，才可以直接联系LiveData.postValue。发送数据给postsLiveData
                        viewModel.postsDataList.postValue(mArrayList)

                        arrayIndex = lastIndex
                    }
                    viewModel.isLoading.value = false
                } else {
                    Log.e(TAG, "jsonGetPosts: error:" + response.message())
                }
            }
        }
    }

}

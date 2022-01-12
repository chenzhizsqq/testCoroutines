package com.example.testcoroutines.runBlocking_launch_withContext_async_doAsync

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.testcoroutines.databinding.ActivityDiffMethodTestBinding
import kotlinx.coroutines.*

//https://blog.csdn.net/zhong_zihao/article/details/105145206 关于协程coroutines的用法
class DiffMethodTestActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDiffMethodTestBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDiffMethodTestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //lauch 是非阻塞的
        binding.testLauch.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                delay(500)     //延时500ms
                Log.e("TAG", "1.执行CoroutineScope.... [当前线程为：${Thread.currentThread().name}]")
            }
            Log.e("TAG", "2.BtnClick.... [当前线程为：${Thread.currentThread().name}]")
        }
        /*
        2.BtnClick.... [当前线程为：main]
        1.执行CoroutineScope.... [当前线程为：main]
         */


        //runBlocking 是阻塞的
        // runBlocking里的任务如果是非常耗时的操作时，会一直阻塞当前线程，在实际开发中很少会用到runBlocking。
        binding.runBlocking.setOnClickListener {
            runBlocking {
                delay(500)    //延时500ms
                Log.e("TAG", "1.runBlocking.... [当前线程为：${Thread.currentThread().name}]")
            }
            Log.e("TAG", "2.BtnClick.... [当前线程为：${Thread.currentThread().name}]")
        }
        /*
        1.runBlocking.... [当前线程为：main]
        2.BtnClick.... [当前线程为：main]
         */


        /**
         * 可返回结果的协程：withContext 与 async 的对比
         */

        //withContext 与 async 的对比:withContext
        //多个 withContext 任务是串行的
        binding.withContextAsyncWithContext.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                val time1 = System.currentTimeMillis()

                val task1 = withContext(Dispatchers.IO) {
                    delay(2000)
                    Log.e("TAG", "1.执行task1.... [当前线程为：${Thread.currentThread().name}]")
                    "one"  //返回结果赋值给task1
                }

                val task2 = withContext(Dispatchers.IO) {
                    delay(1000)
                    Log.e("TAG", "2.执行task2.... [当前线程为：${Thread.currentThread().name}]")
                    "two"  //返回结果赋值给task2
                }

                Log.e(
                    "TAG",
                    "task1 = $task1  , task2 = $task2 , 耗时 ${System.currentTimeMillis() - time1} ms  [当前线程为：${Thread.currentThread().name}]"
                )
            }
        }
        /*
        1.执行task1.... [当前线程为：DefaultDispatcher-worker-1]
        2.执行task2.... [当前线程为：DefaultDispatcher-worker-3]
        task1 = one  , task2 = two , 耗时 3020 ms  [当前线程为：main]
         */


        //withContext 与 async 的对比:async
        //多个 withContext 任务是并行的
        binding.withContextAsyncAsync.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                val time1 = System.currentTimeMillis()

                val task1 = async(Dispatchers.IO) {
                    delay(2000)
                    Log.e("TAG", "1.执行task1.... [当前线程为：${Thread.currentThread().name}]")
                    "one"  //返回结果赋值给task1
                }

                val task2 = async(Dispatchers.IO) {
                    delay(1000)
                    Log.e("TAG", "2.执行task2.... [当前线程为：${Thread.currentThread().name}]")
                    "two"  //返回结果赋值给task2
                }

                Log.e(
                    "TAG",
                    "task1 = ${task1.await()}  , task2 = ${task2.await()} , 耗时 ${System.currentTimeMillis() - time1} ms  [当前线程为：${Thread.currentThread().name}]"
                )
            }
        }
        /*
        2.执行task2.... [当前线程为：DefaultDispatcher-worker-2]
        1.执行task1.... [当前线程为：DefaultDispatcher-worker-2]
        task1 = one  , task2 = two , 耗时 2011 ms  [当前线程为：main]
         */


        //把await()方法的调用提前到 async 的后面
        /*
        当次async协程有问题的时候，才会挂起，运行其他的协程。
         */
        binding.awaitAfterAsync.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                val time1 = System.currentTimeMillis()

                val task1 = async(Dispatchers.IO) {
                    delay(2000)
                    Log.e("TAG", "1.执行task1.... [当前线程为：${Thread.currentThread().name}]")
                    "one"  //返回结果赋值给task1
                }.await()

                val task2 = async(Dispatchers.IO) {
                    delay(1000)
                    Log.e("TAG", "2.执行task2.... [当前线程为：${Thread.currentThread().name}]")
                    "two"  //返回结果赋值给task2
                }.await()

                Log.e(
                    "TAG", "task1 = $task1 " +
                            " , task2 = $task2 " +
                            ", 耗时 ${System.currentTimeMillis() - time1} ms  [当前线程为：${Thread.currentThread().name}]"
                )
            }
        }
        /*
        1.执行task1.... [当前线程为：DefaultDispatcher-worker-1]
        2.执行task2.... [当前线程为：DefaultDispatcher-worker-3]
        task1 = one  , task2 = two , 耗时 3017 ms  [当前线程为：main]
         */
        //此时的结果居然和使用withContext几乎差不多


    }


}
package com.example.testcoroutines.coroutines_flow

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.testcoroutines.databinding.ActivityCoroutinesFlowBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.system.measureTimeMillis

//https://www.jianshu.com/p/fe1293e8f15c
//Kotlin Coroutines Flow 系列
class CoroutinesFlowActivity : AppCompatActivity() {
    val TAG = "CoroutinesFlowActivity"
    private lateinit var binding: ActivityCoroutinesFlowBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCoroutinesFlowBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.flowColdStream.text = "flow 是 Cold Stream。在没有切换线程的情况下，生产者和消费者是同步非阻塞的。"
        binding.flowColdStream.setOnClickListener {
            flowColdStreamFun()
        }

        binding.channelHotStream.text = "channel 是 Hot Stream。而 channelFlow 实现了生产者和消费者异步非阻塞模型。"
        binding.channelHotStream.setOnClickListener {
            channelFlowBuilder()
        }

        binding.flowBuilderMap.text =
            "相比于 RxJava 需要使用 observeOn、subscribeOn 来切换线程，flow 会更加简单。只需使用 flowOn，下面的例子中，展示了 flow builder 和 map 操作符都会受到 flowOn 的影响。"
        binding.flowBuilderMap.setOnClickListener {
            flow_builder_map()
        }

        binding.flowCancel.text = "flow 取消:如果 flow 是在一个挂起函数内被挂起了，那么 flow 是可以被取消的，否则不能取消。"
        binding.flowCancel.setOnClickListener {
            flow_cancel()
        }


    }


    //flow 是 Cold Stream。在没有切换线程的情况下，生产者和消费者是同步非阻塞的。
    fun flowColdStreamFun() = runBlocking {
        val time = measureTimeMillis {
            flow {
                for (i in 1..5) {
                    delay(100)
                    emit(i)
                }
            }.collect {
                delay(100)
                Log.e(TAG, "flowColdStreamFun: $it")
            }
        }

        Log.e(TAG, "flowColdStreamFun: cost $time")
    }

    //channel 是 Hot Stream。而 channelFlow 实现了生产者和消费者异步非阻塞模型。
    fun channelFlowBuilder() = runBlocking {

        val time = measureTimeMillis {
            channelFlow {
                for (i in 1..5) {
                    delay(100)
                    send(i)
                }
            }.collect {
                delay(100)
                Log.e(TAG, "channelFlowBuilder: $it")
            }
        }
        Log.e(TAG, "channelFlowBuilder: channelFlowBuilder $time")
    }


    //相比于 RxJava 需要使用 observeOn、subscribeOn 来切换线程，flow 会更加简单。
    // 只需使用 flowOn，下面的例子中，展示了 flow builder 和 map 操作符都会受到 flowOn 的影响。
    fun flow_builder_map() = runBlocking {

        val time = measureTimeMillis {
            flow {
                for (i in 1..5) {
                    delay(100)
                    emit(i)
                }
            }.map {
                it * it
            }.flowOn(Dispatchers.IO)
                .collect {
                    Log.e(TAG, "flow_builder_map: ${Thread.currentThread().name}: $it")
                }
        }

        Log.e(TAG, "flow_builder_map: flow_builder_map $time")
    }


    //2.3 flow 取消
    //如果 flow 是在一个挂起函数内被挂起了，那么 flow 是可以被取消的，否则不能取消。
    fun flow_cancel() = runBlocking {
        withTimeoutOrNull(2500) {
            flow {
                for (i in 1..5) {
                    delay(1000)
                    emit(i)
                }
            }.collect {
                Log.e(TAG, "flow_cancel: $it")
            }
        }

        Log.e(TAG, "flow_cancel: Done")
    }
}

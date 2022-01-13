package com.example.testcoroutines.coroutines_flow

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.testcoroutines.databinding.ActivityCoroutinesFlowBinding
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.system.measureTimeMillis

//https://www.jianshu.com/p/fe1293e8f15c
//Kotlin Coroutines Flow 系列:是google用来替代Rxjava的协程升级。
// 功能已经宛如Rxjava一样
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


        binding.FlowVSSequences1.text = "Flow 不会阻塞主线程的运行"
        binding.FlowVSSequences1.setOnClickListener {
            FlowVSSequences1()
        }

        binding.FlowVSSequences2.text = "而 Sequences 会阻塞主线程的运行。"
        binding.FlowVSSequences2.setOnClickListener {
            FlowVSSequences2()
        }

        binding.tryFinally.text = "通过使用 try ... finally 实现"
        binding.tryFinally.setOnClickListener {
            tryFinally()
        }

        binding.onCompletionTest.text = " onCompletion 不能捕获异常，只能用于判断是否有异常。"
        binding.onCompletionTest.setOnClickListener {
            onCompletionTest()
        }

        binding.onCompletionCatch.text = "catch 操作符可以捕获来自上游的异常"
        binding.onCompletionCatch.setOnClickListener {
            onCompletionCatch()
        }

        binding.onCompletionCatchFirst.text = "catch比onCompletion更前，则 catch 操作符捕获到异常后，不会影响到下游。"
        binding.onCompletionCatchFirst.setOnClickListener {
            onCompletionCatchFirst()
        }

        binding.onCompletionCatchFirstEach.text = "Catch First Each，都加上了，比较齐全了。"
        binding.onCompletionCatchFirstEach.setOnClickListener {
            onCompletionCatchFirstEach()
        }


        binding.flowAllPart.text = "flow所有部分都加上去了。"
        binding.flowAllPart.setOnClickListener {
            flowAllPart()
        }


        binding.flowAllPartNoError.text = "flow所有部分都加上去了。（没有错误的状态下）"
        binding.flowAllPartNoError.setOnClickListener {
            flowAllPartNoError()
        }


        binding.flowAllPartCoroutineScope.text = "flow所有部分都加上去了。（没有错误的CoroutineScope状态下）"
        binding.flowAllPartCoroutineScope.setOnClickListener {
            flowAllPartCoroutineScope()
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
    /*
    flowColdStreamFun: 1
    flowColdStreamFun: 2
    flowColdStreamFun: 3
    flowColdStreamFun: 4
    flowColdStreamFun: 5
    flowColdStreamFun: cost 1014
     */

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
    /*
    channelFlowBuilder: 1
    channelFlowBuilder: 2
    channelFlowBuilder: 3
    channelFlowBuilder: 4
    channelFlowBuilder: 5
    channelFlowBuilder: channelFlowBuilder 620
     */


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
    /*
    flow_builder_map: main: 1
    flow_builder_map: main: 4
    flow_builder_map: main: 9
    flow_builder_map: main: 16
    flow_builder_map: main: 25
    flow_builder_map: flow_builder_map 518
     */


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
    /*
    flow_cancel: 1
    flow_cancel: 2
    flow_cancel: Done
     */


    /**
     * Flow VS Sequences
     * 每一个 Flow 其内部是按照顺序执行的，这一点跟 Sequences 很类似。
    Flow 跟 Sequences 之间的区别是 Flow 不会阻塞主线程的运行，而 Sequences 会阻塞主线程的运行。
     * */
    fun FlowVSSequences1() = runBlocking {

        launch {
            for (j in 1..5) {
                delay(100)
                Log.e(TAG, "FlowVSSequences1: I'm not blocked $j")
            }
        }

        flow {
            for (i in 1..5) {
                delay(100)
                emit(i)
            }
        }.collect { Log.e(TAG, "FlowVSSequences1: $it") }

        Log.e(TAG, "FlowVSSequences1: Done")
    }
    /*
    FlowVSSequences1: 1
    FlowVSSequences1: I'm not blocked 1
    FlowVSSequences1: 2
    FlowVSSequences1: I'm not blocked 2
    FlowVSSequences1: 3
    FlowVSSequences1: I'm not blocked 3
    FlowVSSequences1: 4
    FlowVSSequences1: I'm not blocked 4
    FlowVSSequences1: 5
    FlowVSSequences1: Done
    FlowVSSequences1: I'm not blocked 5
     */

    //Sequences 会阻塞主线程的运行。
    fun FlowVSSequences2() = runBlocking {

        launch {
            for (k in 1..5) {
                delay(100)
                Log.e(TAG, "FlowVSSequences2: I'm blocked $k")
            }
        }

        sequence {
            for (i in 1..5) {
                Thread.sleep(100)
                yield(i)
            }
        }.forEach { Log.e(TAG, "FlowVSSequences2: $it") }

        Log.e(TAG, "FlowVSSequences2: Done")
    }
    /*
    FlowVSSequences2: 1
    FlowVSSequences2: 2
    FlowVSSequences2: 3
    FlowVSSequences2: 4
    FlowVSSequences2: 5
    FlowVSSequences2: Done
    FlowVSSequences2: I'm blocked 1
    FlowVSSequences2: I'm blocked 2
    FlowVSSequences2: I'm blocked 3
    FlowVSSequences2: I'm blocked 4
    FlowVSSequences2: I'm blocked 5
     */


    //通过使用 try ... finally 实现
    fun tryFinally() = runBlocking {
        try {
            flow {
                for (i in 1..5) {
                    delay(100)
                    emit(i)
                }
            }.collect { Log.e(TAG, "tryFinally: $it") }
        } finally {
            Log.e(TAG, "tryFinally: Done")
        }
    }
    /*
    tryFinally: 1
    tryFinally: 2
    tryFinally: 3
    tryFinally: 4
    tryFinally: 5
    tryFinally: Done
     */


    // onCompletion 不能捕获异常，只能用于判断是否有异常。
    fun onCompletionTest() = runBlocking {
            flow {
                emit(1)
                throw RuntimeException()
            }.onCompletion { cause ->
                if (cause != null)
                    Log.e(TAG, "onCompletionTest: Flow completed exceptionally")
                else
                    Log.e(TAG, "onCompletionTest: Done")
            }.collect { Log.e(TAG, "onCompletionTest: $it") }
    }
    /*
    CoroutinesFlowActivity: onCompletionTest: 1
    CoroutinesFlowActivity: onCompletionTest: Flow completed exceptionally
    AndroidRuntime: FATAL EXCEPTION: main
     */


    //catch 操作符可以捕获来自上游的异常
    fun onCompletionCatch() = runBlocking {
        flow {
            emit(1)
            throw RuntimeException()
        }
            .onCompletion { cause ->
                if (cause != null)
                    Log.e(TAG, "onCompletionCatch: Flow completed exceptionally")
                else
                    Log.e(TAG, "onCompletionCatch: Done")
            }
            .catch{ Log.e(TAG, "onCompletionCatch: catch exception") }
            .collect { Log.e(TAG, "onCompletionCatch: $it") }
    }
    /*
    onCompletionCatch: 1
    onCompletionCatch: Flow completed exceptionally
    onCompletionCatch: catch exception
     */


    //上面的代码如果把 onCompletion、catch 交换一下位置，则 catch 操作符捕获到异常后，不会影响到下游。
    // 因此，onCompletion 操作符不再打印"Flow completed exceptionally"
    fun onCompletionCatchFirst() = runBlocking {
        flow {
            emit(1)
            throw RuntimeException()
        }
            .catch{ Log.e(TAG, "onCompletionCatchFirst: catch exception") }
            .onCompletion { cause ->
                if (cause != null)
                    Log.e(TAG, "onCompletionCatchFirst: Flow completed exceptionally")
                else
                    Log.e(TAG, "onCompletionCatchFirst: Done")
            }
            .collect { Log.e(TAG, "onCompletionCatchFirst: $it") }
    }
    /*
    onCompletionCatchFirst: 1
    onCompletionCatchFirst: catch exception
    onCompletionCatchFirst: Done
     */


    //Catch First Each，都加上了，比较齐全了。感觉和RxJava，真的差不多了
    fun onCompletionCatchFirstEach() = runBlocking {
        flow {
            for (i in 1..5) {
                delay(100)
                emit(i)
                if (i==3){
                    throw RuntimeException()
                }
            }
        }
            .onEach {
                Log.e(TAG, "onCompletionCatchFirstEach: onEach $it" )
            }
            .catch{ Log.e(TAG, "onCompletionCatchFirstEach: catch exception") }
            .onCompletion { cause ->
                if (cause != null)
                    Log.e(TAG, "onCompletionCatchFirstEach: Flow completed exceptionally")
                else
                    Log.e(TAG, "onCompletionCatchFirstEach: Done")
            }
            .collect { Log.e(TAG, "onCompletionCatchFirstEach: collect $it") }
    }
    /*
    onCompletionCatchFirstEach: onEach 1
    onCompletionCatchFirstEach: collect 1
    onCompletionCatchFirstEach: onEach 2
    onCompletionCatchFirstEach: collect 2
    onCompletionCatchFirstEach: onEach 3
    onCompletionCatchFirstEach: collect 3
    onCompletionCatchFirstEach: catch exception
    onCompletionCatchFirstEach: Done
     */


    //Flow已经很齐全了，真的和RxJava没什么区别了。
    //Flow 并没有多那么丰富的操作符来监听其生命周期的各个阶段，目前只有 onStart、onCompletion 来监听 Flow 的创建和结束。
    fun flowAllPart() = runBlocking {

        flow {
            for (i in 1..5) {
                delay(100)
                emit(i)
                if (i==3){
                    throw RuntimeException("Error on $i")
                }
            }
        }
            .onStart { Log.e(TAG, "flowAllPart: Starting flow") }
            .onEach { Log.e(TAG, "flowAllPart: On each $it") }
            .catch { Log.e(TAG, "flowAllPart: Exception : ${it.message}") }
            .onCompletion { Log.e(TAG, "flowAllPart: Flow completed") }
            .collect()
    }
    /*
    flowAllPart: Starting flow
    flowAllPart: On each 1
    flowAllPart: On each 2
    flowAllPart: On each 3
    flowAllPart: Exception : Error on 3
    flowAllPart: Flow completed
     */


    //上面例子中，在没有错误的状态下
    fun flowAllPartNoError() = runBlocking {
        flow {
            for (i in 1..5) {
                delay(100)
                emit(i)
            }
        }
            .onStart { Log.e(TAG, "flowAllPartNoError: Starting flow") }
            .onEach { Log.e(TAG, "flowAllPartNoError: On each $it") }
            .catch { Log.e(TAG, "flowAllPartNoError: Exception : ${it.message}") }
            .onCompletion { Log.e(TAG, "flowAllPartNoError: Flow completed") }
            .collect()
    }
    /*
    flowAllPartNoError: Starting flow
    flowAllPartNoError: On each 1
    flowAllPartNoError: On each 2
    flowAllPartNoError: On each 3
    flowAllPartNoError: On each 4
    flowAllPartNoError: On each 5
    flowAllPartNoError: Flow completed
     */


    //上面例子中，在CoroutineScope状态下
    fun flowAllPartCoroutineScope() = CoroutineScope(Dispatchers.Main).launch {
        flow {
            for (i in 1..5) {
                delay(100)
                emit(i)
            }
        }
            .onStart { Log.e(TAG, "flowAllPartCoroutineScope: Starting flow") }
            .onEach { Log.e(TAG, "flowAllPartCoroutineScope: On each $it") }
            .catch { Log.e(TAG, "flowAllPartCoroutineScope: Exception : ${it.message}") }
            .onCompletion { Log.e(TAG, "flowAllPartCoroutineScope: Flow completed") }
            .collect()
    }
    /*
    flowAllPartCoroutineScope: Starting flow
    flowAllPartCoroutineScope: On each 1
    flowAllPartCoroutineScope: On each 2
    flowAllPartCoroutineScope: On each 3
    flowAllPartCoroutineScope: On each 4
    flowAllPartCoroutineScope: On each 5
    flowAllPartCoroutineScope: Flow completed
     */

}

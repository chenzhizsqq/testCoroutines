package com.example.testcoroutines.testflow

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.testcoroutines.databinding.ActivityTestFlowBinding
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class TestFlowActivity : AppCompatActivity() {
    lateinit var binding: ActivityTestFlowBinding

    // Obtain ViewModel from ViewModelProviders
    private val viewModel by lazy {
        ViewModelProvider(this)[TestFlowViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTestFlowBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // ビューをインフレート
        binding.lifecycleOwner = this
        //绑定
        binding.viewModel = viewModel

        binding.flowAllPart.setOnClickListener { flowAllPart() }
        binding.flowAllPart2.setOnClickListener { flowAllPart2() }

        //dataBinding mvvm
        binding.flowViewModel.setOnClickListener {
            viewModel.testFlowDataList.observe(binding.lifecycleOwner as TestFlowActivity, {
                if (it != null) {
                    Toast.makeText(baseContext, "" + it, Toast.LENGTH_SHORT).show()
                }
            })
            flowViewModel()
        }

    }

    private fun flowViewModel() {
        runBlocking {
            flow {
                val retrofit = Retrofit.Builder()
                    .baseUrl("https://my-json-server.typicode.com/")
                    .addConverterFactory(GsonConverterFactory.create()) //把json转为gson，才可以直接用LiveData.postValue
                    .build()
                val service = retrofit.create(TestFlowService::class.java)
                val response = service.getFlowGson()
                emit(response)
            }
                .onStart { Log.e(TAG, "flowViewModel: Starting flow") }
                .onEach {
                    Log.e(TAG, "flowViewModel: onEach : $it")
                    viewModel.setFlowData(it)
                }
                .catch { binding.error.text = it.message }
                .onCompletion { if (it == null) Log.e(TAG, "Completed successfully") }
                .collect()
        }
    }

    private fun flowAllPart() = runBlocking {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://my-json-server.typicode.com/")
            .addConverterFactory(GsonConverterFactory.create()) //把json转为gson，才可以直接用LiveData.postValue
            .build()
        val service = retrofit.create(TestFlowService::class.java)
        val response = service.getFlowGson()
        flow {
            emit(response)
        }
            .onStart { Log.e(TAG, "flowAllPart: Starting flow") }
            .onEach { binding.resultsTextview.text = it.toString() }
            .catch { binding.error.text = it.message }
            .onCompletion { if (it == null) Log.e(TAG, "Completed successfully") }
            .collect()
    }

    private fun flowAllPart2() {
        runBlocking {
            flow {
                val retrofit = Retrofit.Builder()
                    .baseUrl("https://my-json-server.typicode.com/")
                    .addConverterFactory(GsonConverterFactory.create()) //把json转为gson，才可以直接用LiveData.postValue
                    .build()
                val service = retrofit.create(TestFlowService::class.java)
                val response = service.getFlowGson()
                emit(response)
            }
                .onStart { Log.e(TAG, "flowAllPart2: Starting flow") }
                .onEach { binding.resultsTextview.text = it.toString() }
                .catch { binding.error.text = it.message }
                .onCompletion { if (it == null) Log.e(TAG, "Completed successfully") }
                .collect()
        }
    }


    companion object {
        private const val TAG = "TestFlowActivity"
    }
}
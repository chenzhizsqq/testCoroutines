package com.example.testcoroutines.testflow

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.testcoroutines.databinding.ActivityTestFlowBinding
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class TestFlowActivity : AppCompatActivity() {
    lateinit var binding: ActivityTestFlowBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTestFlowBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // ビューをインフレート
        binding.lifecycleOwner = this

        binding.flowAllPart.setOnClickListener { flowAllPart() }

    }

    private fun flowAllPart() = runBlocking {
        flow {
            val retrofit = Retrofit.Builder()
                .baseUrl("https://my-json-server.typicode.com/")
                .addConverterFactory(GsonConverterFactory.create()) //把json转为gson，才可以直接用LiveData.postValue
                .build()
            val service = retrofit.create(TestFlowService::class.java)
            val response = service.getFlowGson()
            emit(response)
        }
            .onStart { Log.e(TAG, "flowAllPart: Starting flow") }
            .onEach { binding.resultsTextview.text = it.toString() }
            .catch { binding.error.text = it.message }
            .onCompletion { Log.e(TAG, "flowAllPart: Flow completed") }
            .collect()
    }


    companion object {
        private const val TAG = "TestFlowActivity"
    }
}
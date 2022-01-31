package com.example.testcoroutines

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.testcoroutines.databinding.ActivityJsonNormalGetBinding
import com.example.testcoroutines.test.GithubJsonService
import com.example.testcoroutines.test.TestData
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import kotlinx.coroutines.*
import retrofit2.Retrofit

class JsonNormalGetActivity : AppCompatActivity() {
    private lateinit var binding: ActivityJsonNormalGetBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityJsonNormalGetBinding.inflate(layoutInflater)
        setContentView(binding.root)

        this.title = "viewModel,Json的普通获取"

        binding.jsonTestButton.setOnClickListener {
            jsonGet()
        }
    }


    private fun jsonGet() {

        val retrofit = Retrofit.Builder()
            .baseUrl("https://my-json-server.typicode.com/")
            .build()

        val service = retrofit.create(GithubJsonService::class.java)

        CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val response = service.getResponse()

            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    binding.jsonCodeTextview.text = response.code().toString()
                    val gson = GsonBuilder().setPrettyPrinting().create()

                    //jsonOrg获取原来的json的样式
                    val jsonOrg = gson.toJson(
                        JsonParser.parseString(
                            response.body()?.string()
                        )
                    )

                    binding.jsonResultsTextview.text = jsonOrg

                    //把Json通过gson，转成结构的数据
                    val jsonData: TestData = gson.fromJson(jsonOrg, TestData::class.java)
                    Log.e(TAG, "getMethodTest: jsonData.id: " + jsonData.id)
                    Log.e(TAG, "getMethodTest: jsonData.title: " + jsonData.title)

                }
            }
        }
    }


    //有意外发生时的对应线程
    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Log.e(TAG, "throwable: $throwable")
    }


    companion object {
        private const val TAG = "JsonNormalGetActivity"
    }
}
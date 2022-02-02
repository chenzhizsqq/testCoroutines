package com.example.testcoroutines.testflow

import retrofit2.http.GET

interface TestFlowService {

    //https://raw.githubusercontent.com/chenzhizsqq/testJson/main/db.json中，
    /*"test":
    {
        "id": 333,
        "title": "fdsafdsaf333"
    }*/


    //放在网络上，https://github.com/chenzhizsqq/testJson/blob/main/db.json
    //app用的是，https://raw.githubusercontent.com/chenzhizsqq/testJson/main/db.json
    @GET("/chenzhizsqq/testJson/posts")
    suspend fun getFlowGson(): List<TestFlowData>

}
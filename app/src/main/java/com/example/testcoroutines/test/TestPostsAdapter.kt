package com.example.testcoroutines.test

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.testcoroutines.databinding.AdapterTestPostsBinding

class TestPostsAdapter :
    RecyclerView.Adapter<TestPostsAdapter.ViewHolder>() {
    private val list: ArrayList<PostsData> by lazy {
        ArrayList()
    }

    inner class ViewHolder(binding: AdapterTestPostsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val tv_id: TextView = binding.id
        val tv_title: TextView = binding.title
    }

    //添加数据
    fun notifyDataAddData(list: ArrayList<PostsData>) {
        this.list.addAll(list)
        notifyDataSetChanged()
    }

    //重新获取数据
    fun notifyDatNewData(list: ArrayList<PostsData>) {
        this.list.clear()
        this.list.addAll(list)
        notifyDataSetChanged()
    }

    //清除数据
    fun notifyDatClearData() {
        this.list.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            AdapterTestPostsBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tv_id.text = list[position].id.toString()
        holder.tv_title.text = list[position].title
    }
}
package com.vinay.kotlindemo.utils

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView

class RecyclerViewHelper {

    private lateinit var rv: RecyclerView
    private lateinit var onLoadMoreListener: OnLoadMoreListener
    private lateinit var scrollListener: EndlessRecyclerViewScrollListener

    fun setOnLoadMoreListener(rv: RecyclerView, onLoadingListener: OnLoadMoreListener) {
        this.rv = rv
        onLoadMoreListener = onLoadingListener
        scrollListener = object : EndlessRecyclerViewScrollListener(rv.layoutManager!!, rv.layoutManager is LinearLayoutManager) {

            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                onLoadMoreListener.onLoadMore(page, totalItemsCount)
            }
        }
        rv.addOnScrollListener(scrollListener)
    }

    fun reset() {
        rv.adapter!!.notifyDataSetChanged()
        scrollListener.reset()
    }
}

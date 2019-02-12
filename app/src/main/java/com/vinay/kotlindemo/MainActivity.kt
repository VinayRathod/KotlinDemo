package com.vinay.kotlindemo

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.google.gson.JsonElement
import com.vinay.kotlindemo.adapters.KotlinBaseAdapter
import com.vinay.kotlindemo.api.APIClient
import com.vinay.kotlindemo.api.OnApiResponseListener
import com.vinay.kotlindemo.utils.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.row_dummy.view.*

class MainActivity : AppCompatActivity() {

    var allHistory = ArrayList<History>()
    var viewHelper = RecyclerViewHelper()
    var totalpages = 0

    private var onRefreshListener: SwipeRefreshLayout.OnRefreshListener = SwipeRefreshLayout.OnRefreshListener {
        allHistory.clear()
        viewHelper.reset()
        getList(0)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        swipe_layout.setOnRefreshListener(onRefreshListener)
        swipe_layout.setColorSchemeColors(resources.getColor(R.color.colorAccent))
        swipe_layout.setProgressBackgroundColorSchemeColor(resources.getColor(R.color.colorPrimary))
        setList()
        recycler_list.setHasFixedSize(true)
        onRefreshListener.onRefresh()
    }

    private fun setList() {
        recycler_list.adapter = KotlinBaseAdapter(
            layoutId = R.layout.row_dummy,
            list = allHistory,
            viewHolder = { holder, item ->
                holder.itemView.apply {
                    holder.itemView.tv_title.text = item.firstname + " " + item.lastname
                    Glide.with(this@MainActivity).load(item.avtar).into(holder.itemView.iv_icon)
                }
            },
            clickableView = R.id.root,
            clickListener = { _, position ->
                allHistory[position].firstname.toast(this@MainActivity)
            }
        )
        viewHelper.setOnLoadMoreListener(recycler_list, object : OnLoadMoreListener {
            override fun onLoadMore(page: Int, totalItemCount: Int) {
                if (page < totalpages)
                    getList(page)
            }
        })
    }

    fun getList(page: Int) {
        swipe_layout.post { swipe_layout.isRefreshing = true }
        APIClient.getUserList(page + 1,
            object : OnApiResponseListener<JsonElement> {
                override fun onResponseComplete(clsGson: JsonElement, requestCode: Int) {
                    swipe_layout.post { swipe_layout.isRefreshing = false }
                    totalpages = clsGson.jsonInt("total_pages")
                    if (clsGson.asJsonObject.get("data").asJsonArray != null) {
                        for (array: JsonElement in clsGson!!.asJsonObject.get("data").asJsonArray) {
                            allHistory.add(
                                History(
                                    array.asJsonObject.jsonString("id"),
                                    array.asJsonObject.jsonString("first_name"),
                                    array.asJsonObject.jsonString("last_name"),
                                    array.asJsonObject.jsonString("avatar")
                                )
                            )
                        }
                        recycler_list.adapter?.notifyDataSetChanged()
                    }
                }

                override fun onResponseError(errorMessage: String, requestCode: Int, responseCode: Int) {
                    swipe_layout.post { swipe_layout.isRefreshing = false }
                }
            })
    }

    class History(val id: String, val firstname: String, val lastname: String, val avtar: String)
}

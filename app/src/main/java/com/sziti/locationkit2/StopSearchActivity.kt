package com.sziti.locationkit2

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import android.widget.SearchView
import android.widget.TextView
import com.example.easyrefreshloadview.base.BGANormalEasyRefreshViewHolder
import com.example.easyrefreshloadview.base.EasyRefreshLoadView
import com.sziti.locationkit2.data.BDSiteInfoData
import com.sziti.locationkit2.http.RetrofitClient
import com.sziti.locationkit2.treeRecycleView.TreeRecyclerAdapter
import com.sziti.locationkit2.treeRecycleView.base.BaseRecyclerAdapter
import com.sziti.locationkit2.treeRecycleView.base.ItemHelperFactory
import com.sziti.locationkit2.treeRecycleView.base.TreeRecyclerType
import com.sziti.locationkit2.treeRecycleView.base.ViewHolder
import kotlinx.android.synthetic.main.activity_stop_search.*
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.functions.Func1
import rx.schedulers.Schedulers

/**
 * 站点搜索页面
 */
class StopSearchActivity:AppCompatActivity(){
	var PAGESIZE = 20
	var currentPage = 1
	var historylist: ArrayList<SortItem> = ArrayList()
	var treeRecyclerAdapter:TreeRecyclerAdapter ?= null
	var searchName = ""
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_stop_search)

		treeRecyclerAdapter = TreeRecyclerAdapter(TreeRecyclerType.SHOW_ALL)
		activity_stop_search_rv.setLayoutManager(LinearLayoutManager(
			this@StopSearchActivity,
			LinearLayoutManager.VERTICAL,
			false
		))
		activity_stop_search_rv.adapter = treeRecyclerAdapter

		treeRecyclerAdapter?.setOnItemClickListener(object : BaseRecyclerAdapter.OnItemClickListener {
			override fun onItemClick(viewHolder: ViewHolder?, position: Int) {
				if (viewHolder?.getView<TextView>(R.id.item_sort_child_tv) != null) {
					// 将先要传回的数据放到Intent里
					// 可以用putExtra()的方法，也可以用setXXX()的方法
					var currentStop =
										viewHolder.getView<TextView>(R.id.item_sort_child_tv)?.getTag() as BDSiteInfoData.ModelData
					intent = Intent()
					intent.putExtra("current_stop",currentStop)
					// 设置返回码和返回携带的数据
					setResult(Activity.RESULT_OK, intent)
					// RESULT_OK就是一个默认值，=-1，它说OK就OK吧
					finish()
				}
			}
		})

		pull_to_refresh_layout.setDelegate(object : EasyRefreshLoadView.RefreshAndLoadListener{
			override fun onRefresh(easyRefreshLoadView: EasyRefreshLoadView?) {
				historylist.clear()
				currentPage = 0
				searchName = ""
				http(true,"")
			}

			override fun onLoad(easyRefreshLoadView: EasyRefreshLoadView?) {
				currentPage++
				http(false,searchName)
			}
		})
		// 设置下拉刷新和上拉加载更多的风格     参数1：应用程序上下文，参数2：是否具有上拉加载更多功能
		val refreshViewHolder = BGANormalEasyRefreshViewHolder(StopSearchActivity@this, true,EasyRefreshLoadView.PULL_UP_AUTO)

		// 设置下拉刷新和上拉加载更多的风格
		pull_to_refresh_layout.setRefreshViewHolder(refreshViewHolder)
//		pull_to_refresh_layout.shouldHandleRecyclerViewLoadingMore(activity_stop_search_rv)


//		//设置上拉和下拉加载的监听
//		pull_to_refresh_layout.setOnRefreshListener(object:PullToRefreshLayout.OnRefreshListener{
//			override fun onRefresh(pullToRefreshLayout: PullToRefreshLayout?) {
//				historylist.clear()
//				currentPage = 0
//				searchName = ""
//				http(true,"")
//			}
//
//			override fun onLoadMore(pullToRefreshLayout: PullToRefreshLayout?) {
//				currentPage++
//				http(false,searchName)
//			}
//		})


//		activity_stop_search_rv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
//			override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
//				val lm = recyclerView.layoutManager as LinearLayoutManager?
//				val totalItemCount = recyclerView.adapter!!.itemCount
//				val lastVisibleItemPosition = lm!!.findLastVisibleItemPosition()
//				val visibleItemCount = recyclerView.childCount
//
//				if (newState == RecyclerView.SCROLL_STATE_IDLE
//					&& lastVisibleItemPosition == totalItemCount - 1
//					&& visibleItemCount > 0
//				)
//				{
//					//加载更多
//					pull_to_refresh_layout.autoLoad()
//				}
//			}
//		})
		search_view.setOnSearchClickListener(View.OnClickListener {
           Log.i("aaa","")
		})
		//搜索框文字变化监听
		search_view.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
			override fun onQueryTextSubmit(s: String): Boolean {
				Log.e("CSDN_LQR", "TextSubmit : $s")
				searchName = s
				historylist.clear()
				http(true,s)
				return false
			}

			override fun onQueryTextChange(s: String): Boolean {
				Log.e("CSDN_LQR", "TextChange --> $s")
				return false
			}
		})
		http(true,"")
	}

	private fun http(isPull:Boolean,searchName:String) {
		//网络请求
			RetrofitClient.getInstance().getBDSiteInfoList("true", currentPage, PAGESIZE,searchName)
				.subscribeOn(Schedulers.io())
				.map(object : Func1<BDSiteInfoData, List<SortItem>> {
					override fun call(t: BDSiteInfoData?): List<SortItem> {
//						var temp: ArrayList<LetterData> = ArrayList()
						if (t?.Total as Int > 0 && t?.Model?.size as Int > 0) {

							//初始化26个字母的数据
							var temp = ItemHelperFactory.createTreeItemList(
								t.Model,
								SortItem::class.java,
								null
							) as List<SortItem>
							for (t in temp){
								if (historylist.size != 0) {
									for (index in historylist.indices) {
										if (historylist.get(index).data.Guid.equals(t.data.Guid))
											break
										if (index == historylist.size - 1)
											historylist.add(t)
									}
								}else{
									historylist.add(t)
								}
							}
							return historylist
						}
						return ItemHelperFactory.createTreeItemList(
							t.Model,
							SortItem::class.java,
							null
						) as List<SortItem>
					}
				})
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(object: Subscriber<List<SortItem>>(){
					override fun onNext(it: List<SortItem>?) {
						treeRecyclerAdapter?.setDatas(it)
						treeRecyclerAdapter?.notifyDataSetChanged()
						if(isPull)
							pull_to_refresh_layout.endRefreshing("已更新数据"+ it?.size + "条")
						else{
                            pull_to_refresh_layout.endLoadingMore()
						}
					}

					override fun onCompleted() {

					}

					override fun onError(e: Throwable?) {
						Log.e("zxb","崩溃:"+ e.toString())
						if(isPull)
//							pull_to_refresh_layout.refreshFinish(PullToRefreshLayout.FAIL,"获取数据失败")
							pull_to_refresh_layout.endRefreshing("更新数据失败")
						else{
//							pull_to_refresh_layout.loadmoreFinish(PullToRefreshLayout.FAIL)
							pull_to_refresh_layout.endLoadingMore()
						}
					}
				})
	}
}

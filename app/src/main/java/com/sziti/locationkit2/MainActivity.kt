package com.sziti.locationkit2

import android.app.Activity
import android.app.PendingIntent.getActivity
import android.content.Intent
import android.content.res.Configuration
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.PersistableBundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.maps.AMap
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.MapView
import com.amap.api.maps.model.MyLocationStyle
import com.example.easyrefreshloadview.base.BGANormalEasyRefreshViewHolder
import com.example.easyrefreshloadview.base.EasyRefreshLoadView
import com.lzy.imagepicker.ImagePicker
import com.lzy.imagepicker.bean.ImageItem
import com.lzy.imagepicker.ui.ImageGridActivity
import com.sziti.locationkit2.GlobalObjects.Companion.IMAGE_PICKER
import com.sziti.locationkit2.data.*
import com.sziti.locationkit2.http.RetrofitClient
import com.sziti.locationkit2.refreshlayout.BGANormalRefreshViewHolder
import com.sziti.locationkit2.refreshlayout.BGARefreshLayout
import com.sziti.locationkit2.treeRecycleView.SelectImageAdatper
import com.sziti.locationkit2.treeRecycleView.TreeRecyclerAdapter
import com.sziti.locationkit2.treeRecycleView.base.*
import com.sziti.locationkit2.util.PictureUtil
import com.sziti.locationkit2.util.PinYinUtil
import com.sziti.locationkit2.util.TransformLocation
import com.sziti.locationkit2.util.UploadKit
import com.sziti.locationkit2.widget.AlertDialog
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.pull_to_refresh_layout
import kotlinx.android.synthetic.main.activity_stop_search.*
import rx.Observable
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.functions.Action1
import rx.functions.Func1
import rx.schedulers.Schedulers
import java.io.File
import java.text.DecimalFormat
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {
	val REQUEST_CODE = 1234
	var tv_name: EditText? = null
	var tv_location: TextView? = null
	var tv_longitude: TextView? = null
	var tv_latitude: TextView? = null
	var tv_location_direction: EditText? = null
	lateinit var tv_auto_location: TextView
	lateinit var showImages: RecyclerView
	lateinit var showHistory: RecyclerView
	lateinit var showImageAdapter: SelectImageAdatper
	var showHistoryAdapter: TreeRecyclerAdapter = TreeRecyclerAdapter()
	var imgList: ArrayList<String> = ArrayList()
    lateinit var refreshLayout:EasyRefreshLoadView
	lateinit var mMapView: MapView
	var historylist: ArrayList<HistoryData> = ArrayList()
	var aMap: AMap? = null
	var df = DecimalFormat("#.00000000")
	var isFirst: Boolean = true
	var currentStop: BDSiteInfoData.ModelData? = null
	val PAGESIZE = 10
	var currentPage = 1
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			setContentView(R.layout.activity_main)
			refreshLayout = findViewById(R.id.pull_to_refresh_layout)
		}
		else {
			setContentView(R.layout.activity_main_portrait)
			refreshLayout = findViewById(R.id.pull_to_refresh_layout_portrait)
		}
		if (lastCustomNonConfigurationInstance != null)
			imgList = lastCustomNonConfigurationInstance as ArrayList<String>
		//获取地图控件引用
		mMapView = findViewById(R.id.map)
//		在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
		mMapView.onCreate(savedInstanceState)

		initView()
		initMapView()
		initData()
		initListener()
	}

	override fun onRetainCustomNonConfigurationInstance(): Any {
		return imgList
	}

	//	var stopNames = arrayOf("竹园路", "胥江路", "枫情水岸南门", "欧尚金鸡湖店西门", "天虹B座北门", "华东装饰城东", "华东装饰城西")
	private fun initListener() {
		showImageAdapter.setDeleteClickListener(object : SelectImageAdatper.SelectListener {
			override fun onAdd() {
				//设置本次能选择多少张
				ImagePicker.getInstance().setSelectLimit(1 - imgList.size)
				val intent = Intent(this@MainActivity, ImageGridActivity::class.java)
				startActivityForResult(intent, GlobalObjects.IMAGE_PICKER)
			}

			override fun onDelete(position: Int) {
				imgList.removeAt(position)
				showImageAdapter.notifyItemRemoved(position)
			}
		})

		aMap?.setOnMyLocationChangeListener {
			if (isFirst) {
				isFirst = false
				val myLocationStyle =
					MyLocationStyle()
				myLocationStyle.interval(1000)
				aMap?.setMyLocationStyle(myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER))
			}
			Log.e("mainactivity", "GCJ02" + it?.longitude + it?.latitude)
			val wgs84 = TransformLocation.toGPSPoint(it?.longitude as Double, it?.latitude)
			Log.e("mainactivity", "wgs84" + wgs84[0] + wgs84[1])
			tv_longitude?.setText("" + df.format(wgs84[0]))
			tv_latitude?.setText("" + df.format(wgs84[1]))
		}
		select_stop.setOnClickListener(View.OnClickListener {
			startActivityForResult(Intent(this@MainActivity,StopSearchActivity::class.java),REQUEST_CODE)
			//网络请求
//			RetrofitClient.getInstance().getBDSiteInfoList("False", 1, 1800)
//				.subscribeOn(Schedulers.io())
//				.map(object : Func1<BDSiteInfoData, List<SortItemGroup>> {
//					override fun call(t: BDSiteInfoData?): List<SortItemGroup> {
//						var temp: ArrayList<LetterData> = ArrayList()
//						if (t!!.Total > 0 && t!!.Model!!.size > 0) {
//							//初始化26个字母的数据
//							for (index in 0..26) {
//								var letterData = LetterData()
//								letterData.letter = GlobalObjects.LETTERS.get(index)
//								var subData = ArrayList<BDSiteInfoData.ModelData>()
//								for (m in t.Model!!) {
//									val py: String
//									if (PinYinUtil.getLowerCase(
//											m.SName, false
//										) != null
//									)
//										py = PinYinUtil.getLowerCase(
//											m.SName, false
//										)
//									else py = "#"
//									//如果是正常的字母并且等于当前循环的字母
//									if (py.substring(0, 1).equals(LETTERS.get(index), true))
//										subData.add(m)
//								}
//								letterData.datas = subData
//								if (letterData.datas.size > 0)
//									temp.add(letterData)
//							}
//							return ItemHelperFactory.createTreeItemList(temp, SortItemGroup::class.java, null) as List<SortItemGroup>
//						}
//						return ItemHelperFactory.createTreeItemList(temp, SortItemGroup::class.java, null) as List<SortItemGroup>
//					}
//				})
//				.observeOn(AndroidSchedulers.mainThread())
//				.subscribe(object:Subscriber<List<SortItemGroup>>(){
//					override fun onNext(it: List<SortItemGroup>?) {
//						var treeRecyclerAdapter = TreeRecyclerAdapter(TreeRecyclerType.SHOW_ALL)
//						//点击弹出站点选择dialog
//						var dialog = AlertDialog(this@MainActivity)
//						dialog.builder().setTitle("选择站点名称")
//							.setCancelable(false)
//							.setMsgLayoutManager(
//								LinearLayoutManager(
//									this@MainActivity,
//									LinearLayoutManager.VERTICAL,
//									false
//								)
//							)
//							.setMsgAdapter(treeRecyclerAdapter)
//							.setPositiveButton("", View.OnClickListener {
//
//							})
//							.setNegativeButton("取消", View.OnClickListener {
//
//							})
//							.setOnIndexChangedListener {
//								//根据滑动的索引快速定位
//								var sortIndex = 0
//								for (t in treeRecyclerAdapter.getDatas()) {
//									if (t is TreeItemGroup) {
//										if ((t.getData() as LetterData).getLetter().equals(it)) {
//											sortIndex = treeRecyclerAdapter.getDatas().indexOf(t)
////											Log.e("jjj","点击的字母:"+ it)
////											Log.e("jjj","是不是空指针:"+ t.itemManager)
//
////											sortIndex = t.getItemManager().getItemPosition(t)
//										}
//									}
//								}
//								(dialog.txt_msg.layoutManager as LinearLayoutManager)?.scrollToPositionWithOffset(
//									sortIndex,
//									0
//								)
//							}
//						dialog.show()
//						treeRecyclerAdapter.setDatas(it)
//						treeRecyclerAdapter.notifyDataSetChanged()
//						treeRecyclerAdapter.setOnItemClickListener(object : BaseRecyclerAdapter.OnItemClickListener {
//							override fun onItemClick(viewHolder: ViewHolder?, position: Int) {
//								if (viewHolder?.getView<TextView>(R.id.item_sort_child_tv) != null) {
//									currentStop =
//										viewHolder.getView<TextView>(R.id.item_sort_child_tv)?.getTag() as BDSiteInfoData.ModelData
//									tv_name?.setText(currentStop?.SName)
//									tv_location_direction?.setText(currentStop?.SDirect)
//									dialog.dismiss()
//								}
//							}
//						})
//					}
//
//					override fun onCompleted() {
//
//					}
//
//					override fun onError(e: Throwable?) {
//						Log.e("vvv",e?.toString())
//					}
//				})
		})

		//保存按钮监听
		button_save.setOnClickListener(View.OnClickListener {
			if(TextUtils.isEmpty(tv_name?.text.toString()) || TextUtils.isEmpty(tv_location_direction?.text.toString())){
				Toast.makeText(this@MainActivity,"保存点位时必须添加站点名称以及站点方位",Toast.LENGTH_SHORT).show()
				return@OnClickListener
			}
			if(imgList.size == 0){
				Toast.makeText(this@MainActivity,"保存点位时必须添加一张图片",Toast.LENGTH_SHORT).show()
				return@OnClickListener
			}
			Observable.just(imgList)
				.map(object : Func1<List<String>, List<File>> {
					override fun call(t: List<String>?): List<File> {
						val files = ArrayList<File>()
						var targetPath =
							getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES).absolutePath + "/temp" + System.currentTimeMillis() + ".jpeg"
						for (path in t!!) {
							val temp = PictureUtil.compressImage(
								path, targetPath, 60
							)
							files.add(File(temp))
						}
						return files
					}
				})
				.subscribeOn(Schedulers.io())
				.flatMap(object : Func1<List<File>, Observable<FileUploadResultData>> {
					override fun call(t: List<File>?): Observable<FileUploadResultData> {
						return RetrofitClient.getInstance()
							.FileUpload(UploadKit.filesToMultipartBody(t))
					}
				})
				.flatMap(object : Func1<FileUploadResultData, Observable<CommonResult>> {
					override fun call(t: FileUploadResultData?): Observable<CommonResult> {//上传站点保存信息
						var siteId = ""
						if (currentStop != null) {
							siteId = currentStop?.Guid as String
						} else {
							siteId = UUID.randomUUID().toString()
						}
						currentStop = BDSiteInfoData.ModelData()
						currentStop?.Guid = siteId
						currentStop?.SName = tv_name?.text.toString()
						currentStop?.SDirect = tv_location_direction?.text.toString()

						var s = SiteInfo(UUID.randomUUID().toString(),siteId,tv_name?.text.toString(),tv_location_direction?.text.toString(),
							tv_latitude?.text.toString().toDouble(),tv_longitude?.text.toString().toDouble(),t?.savedFileInfo.toString(),GlobalObjects.Username)
//						return RetrofitClient.getInstance().saveSitePosition(
//							UUID.randomUUID().toString(),
//							siteId,
//							tv_name?.text.toString(),
//							tv_location_direction?.text.toString(),
//							tv_latitude?.text.toString().toDouble(),
//							tv_longitude?.text.toString().toDouble(),
//							t?.savedFileInfo.toString(),
//							GlobalObjects.Username
//						)
                        return RetrofitClient.getInstance().saveSitePosition(s)
					}
				})
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(object : Subscriber<CommonResult>() {
					override fun onNext(t: CommonResult?) {
						if (t?.code == 0){
							//刷新站点数据
							historylist.clear()
							currentPage = 1
							queryHistory(true)
							Toast.makeText(this@MainActivity,"上传成功",Toast.LENGTH_SHORT).show()
						}else{
							Toast.makeText(this@MainActivity,"上传失败，请重试",Toast.LENGTH_SHORT).show()
						}
					}

					override fun onCompleted() {
					}

					override fun onError(e: Throwable?) {
						Log.e("vvv", e?.message)
					}
				})
		})
	}

	private fun initData() {
		val gridLayoutManager = GridLayoutManager(this, 3)
		showImages.setLayoutManager(gridLayoutManager)
		showImageAdapter = SelectImageAdatper(1, imgList)
		showImages.setAdapter(showImageAdapter)

		val linearLayoutManager = LinearLayoutManager(this)
		showHistory.layoutManager = linearLayoutManager
		showHistory.adapter = showHistoryAdapter

		queryHistory(true)
	}

	private fun queryHistory(isRefresh: Boolean) {
		Log.i("xxx","currentStop:"+ currentStop?.SName +"guid:"+currentStop?.Guid)
		RetrofitClient.getInstance()
			.querySitePositionRecord(currentStop?.Guid.toString(), true, PAGESIZE, currentPage)
//			.querySitePositionRecord("e156818e-ce06-442c-9378-91833bce28b2",true,PAGESIZE,currentPage)
			.subscribeOn(Schedulers.io())
			.observeOn(AndroidSchedulers.mainThread())
			.subscribe(object : Subscriber<QueryPositionRecordResult>() {
				override fun onNext(t: QueryPositionRecordResult?) {
					if (t?.Code == 1 && t?.Model?.size as Int >= 0) {
						for (i in t.Model!!) {
							var historyData: HistoryData =
								HistoryData(
									1,
									i.Longitude,
									i.Latitude,
									i.MeasuringTime,
									i,
									object : HistoryData.OnDeleteClickListener {
										//删除历史点位记录
										override fun onDelete(position: Int) {
											RetrofitClient.getInstance()
												.deleteSitePosition(historylist.get(position).resData.PositionID)
												.subscribeOn(Schedulers.io())
												.observeOn(AndroidSchedulers.mainThread())
												.subscribe(object : Subscriber<CommonResult>() {
													override fun onNext(t: CommonResult?) {
														//在界面移除item
														if (t?.code == 0)
															showHistoryAdapter.itemManager.removeItem(
																position
															)
														else
															Toast.makeText(
																this@MainActivity,
																"删除失败",
																Toast.LENGTH_SHORT
															).show()
													}
													override fun onCompleted() {
													}
													override fun onError(e: Throwable?) {
														Log.e("bbb", e?.message)
													}
												})
										}
									}
								)
							if (isRefresh){
								historylist.add(historyData)
							}else {
								for (index in historylist.indices) {
									if (historylist.get(index).resData.PositionID.equals(historyData.resData.PositionID))
										break
									if (index == historylist.size - 1)
										historylist.add(historyData)
								}
							}
						}
						var list =
							ItemHelperFactory.createTreeItemList(
								historylist,
								HistoryItem::class.java,
								null
							)
						showHistoryAdapter.setDatas(list)
						showHistoryAdapter.notifyDataSetChanged()
						if (isRefresh) {
							refreshLayout.endRefreshing("已更新数据"+ list?.size + "条")
						} else {
							//进行智能的加载
							refreshLayout.endLoadingMore()
						}
					} else {
						if (isRefresh) {
							refreshLayout.endRefreshing("更新数据失败")
						} else {
							refreshLayout.endLoadingMore()
						}
					}
				}

				override fun onCompleted() {
				}

				override fun onError(e: Throwable?) {
					Log.i("ccc", e.toString())
				}

			})
	}

	private fun initView() {
		tv_name = findViewById(R.id.tv_name)
		tv_location_direction = findViewById(R.id.tv_location_direction)
		tv_location = findViewById(R.id.select_stop)
		tv_longitude = findViewById(R.id.tv_longitude)
		tv_latitude = findViewById(R.id.tv_latitude)
		tv_auto_location = findViewById(R.id.tv_auto_location)

		var d = resources.getDrawable(R.mipmap.location)
		d.setBounds(0, 0, 40, 40)
		tv_auto_location.setCompoundDrawables(d, null, null, null)

		showImages = findViewById(R.id.showImages)
		showHistory = findViewById(R.id.showHistory)

		refreshLayout.setDelegate(object :
			EasyRefreshLoadView.RefreshAndLoadListener {
			override fun onRefresh(easyRefreshLoadView: EasyRefreshLoadView?) {
				//刷新历史点位列表
				historylist.clear()
				currentPage = 1
				queryHistory(true)
			}

			override fun onLoad(easyRefreshLoadView: EasyRefreshLoadView?) {
				currentPage++
				queryHistory(false)
			}
		})
		// 设置下拉刷新和上拉加载更多的风格     参数1：应用程序上下文，参数2：是否具有上拉加载更多功能
		val refreshViewHolder = BGANormalEasyRefreshViewHolder(MainActivity@this, true,EasyRefreshLoadView.PULL_UP_AUTO)
		// 设置下拉刷新
//		refreshViewHolder.setRefreshViewBackgroundColorRes(R.color.color_F3F5F4)//背景色
//		refreshViewHolder.setPullDownRefreshText(UIUtils.getString(R.string.refresh_pull_down_text))//下拉的提示文字
//		refreshViewHolder.setReleaseRefreshText(UIUtils.getString(R.string.refresh_release_text))//松开的提示文字
//		refreshViewHolder.setRefreshingText(UIUtils.getString(R.string.refresh_ing_text))//刷新中的提示文字

//		refreshLayout.setIsShowLoadingMoreView(true)
		// 设置下拉刷新和上拉加载更多的风格
		refreshLayout.setRefreshViewHolder(refreshViewHolder)
		refreshLayout.shouldHandleRecyclerViewLoadingMore(showHistory)
	}

	private fun initMapView() {
		isFirst = true
		val myLocationStyle =
			MyLocationStyle()//初始化定位蓝点样式类myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
		if (aMap == null) {
			aMap = mMapView.getMap()
		}
		aMap?.setMyLocationStyle(myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE))//定位一次，且将视角移动到地图中心点。
		aMap?.getUiSettings()?.setMyLocationButtonEnabled(true)//设置默认定位按钮是否显示，非必需设置。
		aMap?.setMyLocationEnabled(true)// 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。
		aMap?.moveCamera(CameraUpdateFactory.zoomTo(12.toFloat()))
	}

	override fun onDestroy() {
		super.onDestroy()
		mMapView.onDestroy()
	}

	override fun onResume() {
		super.onResume()
		mMapView.onResume()
		initMapView()
	}
	//根据高德地图的特性，需要完整的走过一遍activity的生命周期  所以不能使用这种不销毁的方式
//	override fun onConfigurationChanged(newConfig: Configuration?) {
//		super.onConfigurationChanged(newConfig)
//		mMapView.onDestroy()
//		if (newConfig?.orientation == Configuration.ORIENTATION_PORTRAIT) {
//			//竖屏
//			setContentView(R.layout.activity_main_portrait)
//			mMapView = findViewById(R.id.map)
//		} else {
//			setContentView(R.layout.activity_main)
//			mMapView = findViewById(R.id.map)
//		}
//		//在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
//		mMapView.onCreate(si)
//
//		initView()
//		initMapView()
//		initData()
//		initListener()
//	}

	override fun onPause() {
		super.onPause()
		mMapView.onPause()
	}

	override fun onSaveInstanceState(outState: Bundle?, outPersistentState: PersistableBundle?) {
		super.onSaveInstanceState(outState, outPersistentState)
		//在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
		mMapView.onSaveInstanceState(outState)
	}

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		super.onActivityResult(requestCode, resultCode, data)
		if (resultCode === ImagePicker.RESULT_CODE_ITEMS) {
			if (data != null && requestCode === IMAGE_PICKER) {
				val images =
					data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS) as ArrayList<ImageItem>
				for (i in images.indices) {
					imgList.add(images[i].path)
				}
				showImageAdapter.refresh(imgList)
			} else {
				Toast.makeText(this, "没有数据", Toast.LENGTH_SHORT).show()
			}
		}
		if (resultCode === Activity.RESULT_OK && requestCode === REQUEST_CODE){
			Log.i("zzz",currentStop?.SName + currentStop?.Guid)
			currentStop = data?.getSerializableExtra("current_stop") as BDSiteInfoData.ModelData
			tv_name?.setText(currentStop?.SName)
			tv_location_direction?.setText(currentStop?.SDirect)
			//清除图片选择记录
			imgList.clear()
			showImageAdapter.notifyDataSetChanged()

			//刷新站点数据
			historylist.clear()
			currentPage = 1
            queryHistory(true)
		}
	}
}

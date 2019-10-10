package com.sziti.locationkit2

import android.content.Intent
import android.content.res.Configuration
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.amap.api.maps.AMap
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.MapView
import com.amap.api.maps.model.MyLocationStyle
import com.lzy.imagepicker.ImagePicker
import com.lzy.imagepicker.bean.ImageItem
import com.lzy.imagepicker.ui.ImageGridActivity
import com.sziti.locationkit2.GlobalObjects.Companion.IMAGE_PICKER
import com.sziti.locationkit2.GlobalObjects.Companion.LETTERS
import com.sziti.locationkit2.Pullableview.PullToRefreshLayout
import com.sziti.locationkit2.data.HistoryData
import com.sziti.locationkit2.data.LetterData
import com.sziti.locationkit2.treeRecycleView.SelectImageAdatper
import com.sziti.locationkit2.treeRecycleView.TreeRecyclerAdapter
import com.sziti.locationkit2.treeRecycleView.base.ItemHelperFactory
import com.sziti.locationkit2.treeRecycleView.base.TreeItemGroup
import com.sziti.locationkit2.treeRecycleView.base.TreeRecyclerType
import com.sziti.locationkit2.util.PinYinUtil
import com.sziti.locationkit2.util.TransformLocation
import com.sziti.locationkit2.widget.AlertDialog
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Retrofit
import java.text.DecimalFormat
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {
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

//	lateinit var mMapView: MapView
	var historylist: ArrayList<HistoryData> = ArrayList()
	var aMap: AMap? = null
	var df = DecimalFormat("#.00000000")
	//	var mLocationOption: AMapLocationClientOption? = null
//	 //声明AMapLocationClient类对象
//    var mLocationClient: AMapLocationClient? = null;
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE)
			setContentView(R.layout.activity_main)
		else
			setContentView(R.layout.activity_main_portrait)

		if (lastCustomNonConfigurationInstance != null)
			imgList = lastCustomNonConfigurationInstance as ArrayList<String>
		//获取地图控件引用
//		mMapView = findViewById(R.id.map)
		//在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
//		mMapView.onCreate(savedInstanceState)

		initView()
		initMapView()
		initData()
		initListener()
	}

	override fun onRetainCustomNonConfigurationInstance(): Any {
		return imgList
	}

	var stopNames = arrayOf("竹园路", "胥江路", "枫情水岸南门", "欧尚金鸡湖店西门", "天虹B座北门", "华东装饰城东", "华东装饰城西")
	private fun initListener() {
		showImageAdapter.setDeleteClickListener(object : SelectImageAdatper.SelectListener {
			override fun onAdd() {
				//设置本次能选择多少张
				ImagePicker.getInstance().setSelectLimit(3 - imgList.size)
				val intent = Intent(this@MainActivity, ImageGridActivity::class.java)
				startActivityForResult(intent, GlobalObjects.IMAGE_PICKER)
			}

			override fun onDelete(position: Int) {
				imgList.removeAt(position)
				showImageAdapter.notifyItemRemoved(position)
			}
		})
		showHistory.addOnScrollListener(object : RecyclerView.OnScrollListener() {
			override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
				val lm = recyclerView.layoutManager as LinearLayoutManager?
				val totalItemCount = recyclerView.adapter!!.itemCount
				val lastVisibleItemPosition = lm!!.findLastVisibleItemPosition()
				val visibleItemCount = recyclerView.childCount

				if (newState == RecyclerView.SCROLL_STATE_IDLE
					&& lastVisibleItemPosition == totalItemCount - 1
					&& visibleItemCount > 0
				) {
					//加载更多
					findViewById<PullToRefreshLayout>(R.id.pull_to_refresh_layout).autoLoad()
				}
			}
		})
		findViewById<PullToRefreshLayout>(R.id.pull_to_refresh_layout).setOnRefreshListener(object :
			PullToRefreshLayout.OnRefreshListener {
			override fun onRefresh(pullToRefreshLayout: PullToRefreshLayout?) {
				findViewById<PullToRefreshLayout>(R.id.pull_to_refresh_layout).refreshFinish(
					PullToRefreshLayout.SUCCEED,
					"更新数据xx条"
				)
			}

			override fun onLoadMore(pullToRefreshLayout: PullToRefreshLayout?) {
				//进行智能的加载
				findViewById<PullToRefreshLayout>(R.id.pull_to_refresh_layout).loadmoreFinish(
					PullToRefreshLayout.SUCCEED
				)
			}
		})
		aMap?.setOnMyLocationChangeListener {
			Log.e("mainactivity", "GCJ02" + it?.longitude + it?.latitude)
			val wgs84 = TransformLocation.toGPSPoint(it?.longitude as Double, it?.latitude)
			Log.e("mainactivity", "wgs84" + wgs84[0] + wgs84[1])
			tv_longitude?.setText("" + df.format(wgs84[0]))
			tv_latitude?.setText("" + df.format(wgs84[1]))

		}
		select_stop.setOnClickListener(View.OnClickListener {
			//网络请求
			var t = TreeRecyclerAdapter(TreeRecyclerType.SHOW_ALL)
			//初始化26个字母的数据
			var temp: ArrayList<LetterData> = ArrayList()
			for (index in 0..26) {
				var letterData = LetterData()
				letterData.letter = GlobalObjects.LETTERS.get(index)
				var subData = ArrayList<String>()
				for (stopName in stopNames) {
					val py: String
					if (PinYinUtil.getLowerCase(
							stopName, false
						) != null
					) py = PinYinUtil.getLowerCase(
						stopName, false
					)
					else py = "#"
					//如果是正常的字母并且等于当前循环的字母
					if (py.substring(0, 1).equals(LETTERS.get(index), true))
						subData.add(stopName)
				}
				letterData.datas = subData
				if (letterData.datas.size > 0)
					temp.add(letterData)
			}
			//点击弹出站点选择dialog
			var dialog = AlertDialog(this@MainActivity)
				dialog.builder().setTitle("选择站点名称")
				.setCancelable(false)
				.setMsgLayoutManager(
					LinearLayoutManager(
						this@MainActivity,
						LinearLayoutManager.VERTICAL,
						false
					)
				)
				.setMsgAdapter(t)
				.setPositiveButton("", View.OnClickListener {

				})
				.setNegativeButton("取消", View.OnClickListener {

				})
				.setOnIndexChangedListener {
					var sortIndex = 0
					for (t in t.getDatas()) {
						if (t is TreeItemGroup) {
							if ((t.getData() as LetterData).getLetter().equals(it)) {
								sortIndex = t.getItemManager().getItemPosition(t)
							}
						}
					}
					(dialog.txt_msg.layoutManager as LinearLayoutManager)?.scrollToPositionWithOffset(sortIndex,0)
				}
			dialog.show()

			var treeItemList =
				ItemHelperFactory.createTreeItemList(temp, SortItemGroup::class.java, null)
			t.setDatas(treeItemList)
			t.notifyDataSetChanged()
		})
		tv_auto_location.setOnClickListener(View.OnClickListener {
//			Log.e(
//				"zxb", PinYinUtil.getFirstLetter(
//					"胥江路".substring(0, 1).toCharArray()[0]
//				).toString()
//			)
		})
		button_save.setOnClickListener(View.OnClickListener {

		})
	}

	private fun initData() {
		val gridLayoutManager = GridLayoutManager(this, 3)
		showImages.setLayoutManager(gridLayoutManager)
		showImageAdapter = SelectImageAdatper(3, imgList)
		showImages.setAdapter(showImageAdapter)

		val linearLayoutManager = LinearLayoutManager(this)
		showHistory.layoutManager = linearLayoutManager
		showHistory.adapter = showHistoryAdapter

		for (i in 0..5) {
			var historyData: HistoryData =
				HistoryData(
					1,
					"120.121212",
					"31.121211",
					"2019/9/24 17:21",
					 object:HistoryData.OnDeleteClickListener {
						 override fun onDelete(position: Int) {
							 showHistoryAdapter.itemManager.removeItem(position)
						 }
					 }
				)
			historylist.add(historyData)
		}
		var list =
			ItemHelperFactory.createTreeItemList(historylist, HistoryItem::class.java, null)
		showHistoryAdapter.setDatas(list)
		showHistoryAdapter.notifyDataSetChanged()
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

	}

	private fun initMapView() {
		val myLocationStyle =
			MyLocationStyle()//初始化定位蓝点样式类myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
		if (aMap == null) {
//			aMap = mMapView.getMap()
		}
		aMap?.setMyLocationStyle(myLocationStyle)//设置定位蓝点的Style
		aMap?.getUiSettings()?.setMyLocationButtonEnabled(true)//设置默认定位按钮是否显示，非必需设置。
		aMap?.setMyLocationEnabled(true)// 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。

		aMap?.moveCamera(CameraUpdateFactory.zoomTo(12.toFloat()))


	}

	override fun onDestroy() {
		super.onDestroy()
//		mMapView.onDestroy()
	}

	override fun onResume() {
		super.onResume()
//		mMapView.onResume()
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
//		mMapView.onPause()
	}

	override fun onSaveInstanceState(outState: Bundle?, outPersistentState: PersistableBundle?) {
		super.onSaveInstanceState(outState, outPersistentState)
		//在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
//		mMapView.onSaveInstanceState(outState)
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
	}
}

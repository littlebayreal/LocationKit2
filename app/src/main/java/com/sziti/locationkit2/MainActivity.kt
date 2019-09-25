package com.sziti.locationkit2
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import com.amap.api.maps.MapView
import com.lzy.imagepicker.ImagePicker
import com.lzy.imagepicker.bean.ImageItem
import com.lzy.imagepicker.ui.ImageGridActivity
import com.sziti.locationkit2.GlobalObjects.Companion.IMAGE_PICKER
import com.sziti.locationkit2.treeRecycleView.SelectImageAdatper
import com.sziti.locationkit2.treeRecycleView.TreeRecyclerAdapter
import com.sziti.locationkit2.treeRecycleView.base.ItemHelperFactory
import com.sziti.locationkit2.treeRecycleView.base.TreeRecyclerType
import java.util.ArrayList



class MainActivity : AppCompatActivity() {
	var tv_name:TextView?=null
	var tv_location:TextView?=null
	var tv_longitude:TextView?=null
	var tv_latitude:TextView?=null
	lateinit var tv_auto_location:TextView
	lateinit var showImages: RecyclerView
	lateinit var showHistory:RecyclerView
	lateinit var showImageAdapter:SelectImageAdatper
	var showHistoryAdapter:TreeRecyclerAdapter = TreeRecyclerAdapter()
	var imgList:ArrayList<String> = ArrayList()
	lateinit var mMapView:MapView
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)
		//获取地图控件引用
		mMapView = findViewById(R.id.map)
		//在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
		mMapView.onCreate(savedInstanceState)

		initView()
		initData()
		initListener()
	}

	private fun initListener() {
		showImageAdapter.setDeleteClickListener(object : SelectImageAdatper.SelectListener{
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
	}

	private fun initData() {
		val gridLayoutManager = GridLayoutManager(this, 3)
		showImages.setLayoutManager(gridLayoutManager)
		showImageAdapter = SelectImageAdatper(3, imgList)
		showImages.setAdapter(showImageAdapter)

		val linearLayoutManager = LinearLayoutManager(this)
		showHistory.layoutManager = linearLayoutManager
		showHistory.adapter = showHistoryAdapter
		var temp = ArrayList<HistoryData>()
		for (i in 0..5){
			var historyData:HistoryData = HistoryData(1,"120.121212","31.121211","2019/9/24 17:21")
			temp.add(historyData)
		}
		val list =
			ItemHelperFactory.createTreeItemList(temp, HistoryItem::class.java, null)
		showHistoryAdapter.setDatas(list)
		showHistoryAdapter.notifyDataSetChanged()
	}

	private fun initView() {
		tv_name = findViewById(R.id.tv_name)
		tv_location = findViewById(R.id.tv_location)
		tv_longitude=findViewById(R.id.tv_longitude)
		tv_latitude = findViewById(R.id.tv_latitude)
		tv_auto_location = findViewById(R.id.tv_auto_location)

		var d = resources.getDrawable(R.mipmap.location)
		d.setBounds(0,0,40,40)
		tv_auto_location.setCompoundDrawables(d,null,null,null)

		showImages = findViewById(R.id.showImages)
		showHistory = findViewById(R.id.showHistory)
	}

	override fun onDestroy() {
		super.onDestroy()
		mMapView.onDestroy()
	}

	override fun onResume() {
		super.onResume()
		mMapView.onResume()
	}

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
		Log.i("zxb","code:"+ resultCode)
		if (resultCode === ImagePicker.RESULT_CODE_ITEMS) {
			if (data != null && requestCode === IMAGE_PICKER) {
				val images = data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS) as ArrayList<ImageItem>
				for (i in images.indices) {
					Log.d("zxb", "path:" + images[i].path)
					imgList.add(images[i].path)
				}
				showImageAdapter.refresh(imgList)
			} else {
				Toast.makeText(this, "没有数据", Toast.LENGTH_SHORT).show()
			}
		}
	}
}

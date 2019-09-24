package com.sziti.locationkit2
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.TextView
import com.lzy.imagepicker.ImagePicker
import com.lzy.imagepicker.ui.ImageGridActivity
import com.sziti.locationkit2.treeRecycleView.SelectImageAdatper
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
	var imgList:ArrayList<String> = ArrayList()
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)
		initView()
		initData()
		initListener()
	}

	private fun initListener() {
		showImageAdapter.setDeleteClickListener(object : SelectImageAdatper.SelectListener{
			override fun onAdd() {
				//设置本次能选择多少张
				ImagePicker.getInstance().setSelectLimit(5 - imgList.size)
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
}

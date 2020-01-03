var itemHeight = 40;
var dividerHeight = 1;

var PAGESIZE = 10;
var CurrentPage = 0;
var TotalPage = 0;
var menuDatas = [];
const url = "http://192.168.207.231:8599";
//模拟数据
// var datas = [
// {"WFSiteManageID":"8248dc1c-6c22-4f1d-a37c-37e62cc01bc2","Title":"站点变更流程2019092601","Items":[
// {"StopName":"胥江路","Direction":"东","IsShoot":true},{"StopName":"桐泾公园","Direction":"东","IsShoot":false}]},
// {"WFSiteManageID":"9afe9998-969c-4bde-b33b-893ecdaa062e","Title":"2019092502","Items":[
// {"StopName":"胥江路","Direction":"东","IsShoot":true},{"StopName":"桐泾公园","Direction":"东","IsShoot":false}]},
// {"WFSiteManageID":"908818c9-2a36-4158-bbee-db90a0a38dc2","Title":"变更流程2019092501","Items":[
// {"StopName":"胥江路","Direction":"东","IsShoot":true},{"StopName":"桐泾公园","Direction":"东","IsShoot":false}]},
// {"WFSiteManageID":"b2d6185a-7e52-4581-b8a5-5dc5c122f06f","Title":"园区规建委关于站点变更流程","Items":[
// {"StopName":"胥江路","Direction":"东","IsShoot":true},{"StopName":"桐泾公园","Direction":"东","IsShoot":false}]},
// {"WFSiteManageID":"1689cce0-9a88-45d0-a240-8b39495d473d","Title":"补录站点","Items":[
// {"StopName":"胥江路","Direction":"东","IsShoot":true},{"StopName":"桐泾公园","Direction":"东","IsShoot":false}]},
// {"WFSiteManageID":"bbc48d48-300f-47e0-84f2-89e662fdb29b","Title":"相城行管中心关于黄埭片区候车亭启用流程","Items":[
// {"StopName":"胥江路","Direction":"东","IsShoot":true},{"StopName":"桐泾公园","Direction":"东","IsShoot":false}]},
// {"WFSiteManageID":"be4c573a-925d-4870-b989-d9ed7a04b2ad","Title":"相城行管中心关于落山上站点迁移流程","Items":[
// {"StopName":"胥江路","Direction":"东","IsShoot":true},{"StopName":"桐泾公园","Direction":"东","IsShoot":false}]},
// {"WFSiteManageID":"fc99a25c-610f-4dfc-be72-1eae024792c0","Title":"新区运管关于站点变更流程","Items":[
// {"StopName":"胥江路","Direction":"东","IsShoot":true},{"StopName":"桐泾公园","Direction":"东","IsShoot":false}]},
// {"WFSiteManageID":"7f8e3931-d058-4c52-a608-5a89a708235b","Title":"园区规建委关于站点新增流程","Items":[
// {"StopName":"胥江路","Direction":"东","IsShoot":true},{"StopName":"桐泾公园","Direction":"东","IsShoot":false}]},
// {"WFSiteManageID":"eb271430-87f4-46de-936b-d7d1217ae49a","Title":"吴中运管关于候车亭启用流程木渎、度假区","Items":[
// {"StopName":"胥江路","Direction":"东","IsShoot":true},{"StopName":"桐泾公园","Direction":"东","IsShoot":false}]}]
$(function() {// 初始化内容
	// initData();
 http(); 
});  
//请求网络数据
function http(){
  $.ajax({
    url:url+"/Mobile/QueryApprovedWFSiteFlow",
    type:'POST',             
                contentType:"application/x-www-form-urlencoded",   // 告诉jQuery不要去设置Content-Type请求头
                cache:false,
                data: {
                  'PageSize': PAGESIZE,
                  'CurrentPage': CurrentPage
                },
                dataType: "JSON",
                xhr:function(){
                  return $.ajaxSettings.xhr();
                },
                success:function(datax, textStatus, jqXHR){
                  console.log("请求成功:"+ datax['Code']);
                    //上传成功
                    if((datax&&datax['Code'] == 1)||jqXHR.status==204){
                      var t = datax['Total'];
                      if (t % PAGESIZE != 0)TotalPage = Math.ceil(t / PAGESIZE);
                      else
                        TotalPage = t/PAGESIZE;
                      console.log("总页数:"+ TotalPage);
                      //初始化分页控件
                      $("#pagination1").pagination({
                        currentPage: 1,
                        totalPage: TotalPage,
                        isShow: true,
                        count: 3,
                        homePageText: "首页",
                        endPageText: "尾页",
                        prevPageText: "<<",
                        nextPageText: ">>",
                        callback: function(current) {
                        //执行翻页逻辑
                        httpForPage(current);
                      }
                    });

                      menuDatas = datax;
                      initData(datax['Model']);
                    }
                  }
                });
}
//根据页码请求数据
function httpForPage(page){
  $.ajax({
    url:url+"/Mobile/QueryApprovedWFSiteFlow",
    type:'POST',             
                contentType:"application/x-www-form-urlencoded",   // 告诉jQuery不要去设置Content-Type请求头
                cache:false,
                data: {
                  'PageSize': PAGESIZE,
                  'CurrentPage': page
                },
                dataType: "JSON",
                xhr:function(){
                  return $.ajaxSettings.xhr();
                },
                success:function(datax, textStatus, jqXHR){
                  console.log("请求成功:"+ datax['Code']);
                    //上传成功
                    if((datax&&datax['Code'] == 1)||jqXHR.status==204){   
                      menuDatas = datax;
                      initData(datax['Model']);
                    }
                  }
                });
}
function initData(datas){
  var list_html = "";
  for(var i=0;i<datas.length;i++){
   list_html += "<div id='menu_title";
   list_html += i;
   list_html += "' class='menu_title' onclick='openMenu(this)'>"
   list_html += datas[i]['Title'];
   list_html += "<div class='indicator' id='indicator";
   list_html += i;
   list_html += "'>^</div></div>";
   list_html += "<div class='menu' id='menu" + i + "'>";
      //暂时模拟三次循环
      // for (var j = 0; j < datas[i]['Items'].length; j++) {
      // 	//内部的item
      // 	list_html += "<div class='item'>";
      // 	console.log("内循环:"+ datas[i]['Items'][j]['IsShoot']);
      // 	if (datas[i]['Items'][j]['IsShoot']) {
      // 		list_html += "<text class='col-xs-4 col-sm-4' style='color: #33ff33'>"+datas[i]['Items'][j]['StopName']+"</text>";
      // 		list_html += "<text class='col-xs-4 col-sm-4' style='color: #33ff33'>"+datas[i]['Items'][j]['Direction']+"</text>";
      // 		list_html += "<text class='col-xs-4 col-sm-4' style='color: #33ff33'>已拍√</text>";
      // 	}else{
      // 		list_html += "<text class='col-xs-4 col-sm-4' style='color: red'>"+datas[i]['Items'][j]['StopName']+"</text>";
      // 		list_html += "<text class='col-xs-4 col-sm-4' style='color: red'>"+datas[i]['Items'][j]['Direction']+"</text>";
      // 		console.log("内循环:"+ datas[i]['Items'][j]);
      // 		list_html += "<button type='button' class='btn btn-default' value ="+ JSON.stringify(datas[i]['Items'][j]) +" onclick='openShoot(this)'>拍照</button>";
      // 	}

      // 	list_html += "</div>";
      // 	if (j != datas[i]['Items'].length - 1) {
      // 		list_html += "<li class='item_divider'></li>";
      // 	}
      // }
      list_html += "</div>";
      if (i != datas.length - 1) {
      	list_html += "<li class='menu_divider'></li>";
      }
    }
    console.log(list_html);
    document.getElementById('menus').innerHTML = list_html;
  }
//点击拍摄按钮
function openShoot(obj){
	console.log("点击拍摄:"+obj.value);
  window.location.href="ShootContent.html?parameter=" + obj.value;
  // window.open("ShootContent.html?parameter=" + obj.value);//传入参数
}
//请求菜单数据
function initMenu(id){
  console.log("页码"+ JSON.stringify(menuDatas['Model'][id]));
  $.ajax({
    url:url+"/Mobile/QueryWFSitePhoto",
    type:'POST',             
                contentType:"application/x-www-form-urlencoded",   // 告诉jQuery不要去设置Content-Type请求头
                cache:false,
                data: {
                  'WFSiteManageID': menuDatas['Model'][id]['WFSiteManageID'],
                },
                dataType: "JSON",
                xhr:function(){
                  return $.ajaxSettings.xhr();
                },
                success:function(datax, textStatus, jqXHR){
                    //上传成功
                    if((datax&&datax['Code'] == 1)||jqXHR.status==204){
                     var list_html = "";
                       //暂时模拟三次循环
                       for (var j = 0; j < datax['Model'].length; j++) {
                      //内部的item
                      list_html += "<div class='item'>";

                      if (datax['Model'][j]['PhotoCount'] == "0") {
                       list_html += "<text class='col-xs-4 col-sm-4' style='color: #33ff33'>"+datax['Model'][j]['SiteName']+"</text>";
                       list_html += "<text class='col-xs-4 col-sm-4' style='color: #33ff33'>"+datax['Model'][j]['Direction']+"</text>";
                       list_html += "<text class='col-xs-4 col-sm-4' style='color: #33ff33'>已拍√</text>";
                     }else{
                       list_html += "<text class='col-xs-4 col-sm-4' style='color: red'>"+datax['Model'][j]['SiteName']+"</text>";
                       list_html += "<text class='col-xs-4 col-sm-4' style='color: red'>"+datax['Model'][j]['Direction']+"</text>";

                       list_html += "<button type='button' class='btn btn-default' value ="+ JSON.stringify(datax['Model'][j]) +" onclick='openShoot(this)'>拍照</button>";
                     }

                     list_html += "</div>";
                     if (j != datax['Model'].length - 1) {
                       list_html += "<li class='item_divider'></li>";
                     }
                   }
                   //为什么要点击两次才会生效
                   console.log("添加了子菜单")

                   document.getElementById('menu'+ id).innerHTML = list_html;

                   //切记 在动态添加进入页面后 设置高度  否则第一次点开是空白
                   menu = document.getElementById("menu"+ id);
                   indicator = document.getElementById("indicator" +id);
                   
                   childAmount = menu.getElementsByTagName('div').length;
                   dividerAmount = menu.getElementsByTagName('li').length;
                   height = childAmount * itemHeight + dividerAmount * dividerHeight;
                   menu.style.height = height + "px";
                   indicator.style.transform = "rotate(180deg)";
                 }
               }
             });
}
//打开菜单栏
function openMenu(obj) {
	// console.log("点击的是哪个:"+obj.id);
	menuTitleId = obj.id;
	menuId = "menu" + menuTitleId.substring(10);
	indicatorId = "indicator" + menuTitleId.substring(10);

    //保证每次只有一个menu被打开
    for (var i = 0; i < $('div.menu').length; i++) {
    	menu = document.getElementById("menu"+ i);
    	indicator = document.getElementById("indicator" + i);
      //除去当前点击的其它menu全部关闭
      if (menuTitleId.substring(10) != i) {
    	//关闭子菜单
    	menu.style.height = "0px";
    	indicator.style.transform = "rotate(0deg)";
    }
  }

  menu = document.getElementById(menuId);
  indicator = document.getElementById(indicatorId);
  height = menu.style.height;
  console.log("点击的是哪个:"+menuId);
    //打开子菜单
    if (height == "0px" || height == "") {
     console.log("点击的是开启");
     // childAmount = menu.getElementsByTagName('div').length;
     // dividerAmount = menu.getElementsByTagName('li').length;
     // height = childAmount * itemHeight + dividerAmount * dividerHeight;
     // menu.style.height = height + "px";
     // indicator.style.transform = "rotate(180deg)";
      //请求菜单内容
      initMenu(menuTitleId.substring(10))
    } else {
      console.log("点击的是关闭");
    	//关闭子菜单
    	menu.style.height = "0px";
    	indicator.style.transform = "rotate(0deg)";
    }
  }

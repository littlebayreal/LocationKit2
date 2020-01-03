var uploadImg;
//获得参数的方法
var obj = {};
const url = "http://192.168.207.231:8599";
var photoMsg = "";
var request = {

   QueryString : function(val) { 

       var uri = window.location.search; 
       uri = decodeURI(uri);
       var re = new RegExp("" +val+ "=([^&?]*)", "ig"); 

       return ((uri.match(re))?(uri.match(re)[0].substr(val.length+1)):null); 

   }
}
$(function(){
    console.log(request.QueryString("parameter"));
    obj = JSON.parse(request.QueryString("parameter"));
    console.log(obj['SiteName']+obj['Direction']);
    $("#stopName").val(obj['SiteName']);
    $("#stopDirection").val(obj['Direction']);
    uploadImg=uploadMd.defineImgUpload({
                container:$('#imgUpload'), //插件所需容器
                url:url+'/Upload/UploadFile', //文件上传地址
                max:1, //允许选择的最大上传图片数
                parameter:{}, //图片上传额外参数
                previewImg:[], //传入预览的图片，适用编辑时展示曾经上传的图片
                fileInputName:'file1', //图片文件表单项名称
                uploadCallback:function(data){
                    console.log(data);
                    photoMsg = data['Msg'];
                },
            });
});


function save(){ 
    //判断是否有图片没有上传
    if (photoMsg == "") {
       alert("请先点击上传图片")
   }else{
    $.ajax({
        url:url+'/Mobile/SaveWFSitePhoto',
        type:'POST',
                contentType:"application/x-www-form-urlencoded",   // 告诉jQuery不要去设置Content-Type请求头
                cache:false,
                data:{
                    'WFSiteDetailID':obj['WFSiteDetailID'],
                    'SitePhotos':photoMsg,
                    'RequestedBy':"SuperAdmin"
                },
                xhr:function(){
                    return $.ajaxSettings.xhr();
                },
                success:function(datax, textStatus, jqXHR){
                    console.log("走到这里了:"+ datax);
                    //上传成功
                    if ((datax && datax['Code'] == 0) || jqXHR.status == 204) {
                        alert("上传成功");
                        $("#stopName").val('');
                        $("#stopDirection").val('');
                        photoMsg = "";
                        uploadImg.reset();
                        history.go(-1);
                    }
                    else {
                        alert(datax['Msg']);
                    }
                }
            });
}
}

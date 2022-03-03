$(function(){
	$("#publishBtn").click(publish);
});

function publish() {
	//隐藏立即发布按钮
	$("#publishModal").modal("hide");

	//获取标题和内容
	var title=$("#recipient-name").val();
	var content=$("#message-text").val();
	//发送异步请求   post方式
	$.post(
		CONTEXT_PATH+"/discuss/add",
		{"title":title,"content":content},
		function (data) {
			data=$.parseJSON(data);
			//在提示框中显示返回信息
			$("#hintBody").text(data.msg);
			//显示提示框  2秒后消失
			$("#hintModal").modal("show");
			setTimeout(function(){
				$("#hintModal").modal("hide");
				//刷新页面
				if(data.code==0){
					window.location.reload();
				}

			}, 2000);

		}
	);

}
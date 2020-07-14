<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    
<%
	String ctxPath = request.getContextPath();
	//		/board
%>
    
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>

<style type="text/css">
   table, th, td{
      border: solid 1px gray;
      border-collapse: collapse;
   }
</style>

<script type="text/javascript" src="<%= ctxPath%>/resources/js/jquery-3.3.1.min.js"></script>
<script type="text/javascript">
	$(document).ready(function() {
		func_viewInfo();
		
		$("#btnSubmit").click(function() {
			$.ajax({
				url:"/board/ajaxtest/insert.action",
				type:"POST",
				data:{"no":$("#no").val()
					 ,"name":$("#name").val()},
				dataType:"JSON",
				success:function(json){
					if(json.n == 1) {
						func_viewInfo();
					}
				},
				error: function(request, status, error){
					alert("code: "+request.status+"\n"+"message: "+request.responseText+"\n"+"error: "+error);
				}
			});
		});
	}); // end of $(document).ready()
	
	function func_viewInfo() {
		$.ajax({
			url:"/board/ajaxtest/select.action",
			dataType:"JSON",
			success:function(json) {
				var html = "<table>";
					html += "<tr>";
					html += "<th>번호</th>";
					html += "<th>입력번호</th>";
					html += "<th>성명</th>";
					html += "<th>작성일자</th>";
					html += "</tr>";
					
				$.each(json, function(index, item){
					html += "<tr>";
					html += "<td>" + (index+1) + "</td>";
					html += "<td>" + item.no + "</td>";
					html += "<td>" + item.name + "</td>";
					html += "<td>" + item.writeday + "</td>";
					html += "</tr>";
				});
				html += "</table>";
				
				$("#view").html(html);
			},
			error: function(request, status, error){
				alert("code: "+request.status+"\n"+"message: "+request.responseText+"\n"+"error: "+error);
			}
		});
	} // end of function func_viewInfo()
</script>
</head>
<body>
	<h2>AJAX 연습</h2>
	<form>
		번호 : <input type="text" id="no" /><br/> 
		성명 : <input type="text" id="name" /><br/>
		<button type="button" id="btnSubmit">확인</button>
		<button type="reset">취소</button>
	</form>
	<br/>
	<div id="view"></div>
</body>
</html>
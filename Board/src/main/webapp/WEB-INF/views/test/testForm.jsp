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

<script type="text/javascript" src="<%= ctxPath%>/resources/js/jquery-3.3.1.min.js"></script>
<script type="text/javascript">
	$(document).ready(function() {
		$("#btnSubmit").click(function() {
			var frm = document.myFrm;
			frm.method = "post";
			frm.action = "<%= ctxPath%>/test/test_formEnd.action";
			frm.submit();
		});
	});
</script>

</head>
<body>
	<form name="myFrm">
		번호 : <input type="text" name="no" /><br/> 
		성명 : <input type="text" name="name" /><br/>
		<button type="button" id="btnSubmit">확인<button>
		<button type="reset">취소</button>
	</form>
</body>
</html>
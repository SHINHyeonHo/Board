<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<% String ctxPath = request.getContextPath(); %>

<style type="text/css">
table, th, td, input, textarea {border: solid gray 1px;}
	
#table {border-collapse: collapse;
 		width: 900px;
 		}
#table th, #table td{padding: 5px;}
#table th{width: 120px; background-color: #DDDDDD;}
#table td{width: 860px;}
.long {width: 470px;}
.short {width: 120px;}

</style>

<script type="text/javascript">
	$(document).ready(function(){
		
	});
</script>

<div style="">
	<h1>글쓰기</h1>
	
	<form name="addFrm">
		<table id="table">
			<tr>
				<th>성명</th>
				<td>
					<input type="text" name="name" value="${sessionScope.loginuser.name}" class="short" readonly/>
				</td>
			</tr>
			<tr>
				<th>글제목</th>
				<td>
					<input type="text" name="subject" class="long" />
				</td>
			</tr>
			<tr>
				<th>글내용</th>
				<td>
					<textarea rows="10" cols="100" name="content" style=""></textarea>
				</td>
			</tr>
		</table>
	</form>
</div>










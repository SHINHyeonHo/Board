<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

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
</head>
<body>
	<h3>내 오라클 서버의 정보</h3>
	<table>
		<tr>
			<th>번호</th>
			<th>입력번호</th>
			<th>성명</th>
			<th>작성일자</th>
		</tr>
		<c:forEach var="testvo" items="${testvoList}" varStatus="status">
			<tr>
				<td>${status.count}</td>
				<td>${testvo.no}</td>
				<td>${testvo.name}</td>
				<td>${testvo.writeday}</td>
			</tr>
		</c:forEach>
	</table>
	
	<br/>
	
	<h3>상대방 오라클 서버의 정보</h3>
	<table>
		<tr>
			<th>번호</th>
			<th>입력번호</th>
			<th>성명</th>
			<th>작성일자</th>
		</tr>
		<c:forEach var="testvo" items="${testvoList2}" varStatus="status">
			<tr>
				<td>${status.count}</td>
				<td>${testvo.no}</td>
				<td>${testvo.name}</td>
				<td>${testvo.writeday}</td>
			</tr>
		</c:forEach>
	</table>
</body>
</html>
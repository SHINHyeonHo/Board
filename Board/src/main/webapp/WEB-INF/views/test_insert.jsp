<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>test_insert 페이지</title>
</head>
<body>
	<c:if test="${n > 0}">
		<span style="color: blue; font-size: 16pt;">${message}</span>
	</c:if>
	
	<c:if test="${n <= 0}">
		<span style="color: red; font-size: 16pt;">${message}</span>
	</c:if>
</body>
</html>
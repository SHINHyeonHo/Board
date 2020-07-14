<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%
	String ctxPath = request.getContextPath();
	//		/board
%>

<style>
	span#storeName {
		font-size: 16pt;
		color: red;
	}

</style>

<div align="center" style="margin: 0 auto;">

<p>
	신현호짱짱맨 <span id="storeName">게시판 광고물</span> 입니다.
</p>

<div class="container">
	<div style="width: 70%; margin: 0 auto;">
		<div id="myCarousel" class="carousel slide" data-ride="carousel">
			  <!-- Indicators -->
			<ol class="carousel-indicators">
			 	<c:forEach items="${imgfilenameList}" varStatus="status">
			 		<c:if test="${status.index == 0}">
			 			<li data-target="#myCarousel" data-slide-to="${status.index}" class="active"></li>
			 		</c:if>
			 		<c:if test="${status.index != 0}">
			 			<li data-target="#myCarousel" data-slide-to="${status.index}"></li>
			 		</c:if>
			 	</c:forEach>
			</ol>
			
			<!-- Wrapper for slides -->	
			<div class="carousel-inner">
				 <c:forEach var="imgname" items="${imgfilenameList}" varStatus="status">
				 	<c:if test="${status.index == 0}">
				 		<div class="item active">
							<img src="<%= ctxPath%>/resources/images/${imgname}" style="width:100%;">
						</div>
				 	</c:if>
				 	<c:if test="${status.index != 0}">
				 		<div class="item">
							<img src="<%= ctxPath%>/resources/images/${imgname}" style="width:100%;">
						</div>
				 	</c:if>
				 </c:forEach>
			</div>
			
			<!-- Left and right controls -->
			<a class="left carousel-control" href="#myCarousel" data-slide="prev">
			    <span class="glyphicon glyphicon-chevron-left"></span>
			    <span class="sr-only">Previous</span>
			</a>
			<a class="right carousel-control" href="#myCarousel" data-slide="next">
			    <span class="glyphicon glyphicon-chevron-right"></span>
			    <span class="sr-only">Next</span>
			</a>
		</div>
	</div>
</div>

</div>
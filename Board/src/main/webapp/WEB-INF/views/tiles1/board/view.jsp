<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<style type="text/css">
	table, th, td, input, textarea {border: solid gray 1px;}
	
	#table, #table2 {border-collapse: collapse;
	 		         width: 900px;
	 		        }
	#table th, #table td{padding: 5px;}
	#table th{width: 120px; background-color: #DDDDDD;}
	#table td{width: 750px;}
	.long {width: 470px;}
	.short {width: 120px;}
	
	.move {cursor: pointer;}
	.moveColor {color: #660029; font-weight: bold;}
	
	a {text-decoration: none !important;}
</style>

<script type="text/javascript">
	$(document).ready(function(){
		
		$(".move").hover(function(){
							$(this).addClass("moveColor");
		                 }, 
		                 function(){
		                	 $(this).removeClass("moveColor"); 
		                 }
		);
		
	//	goReadComment();	// 페이징처리 안한 댓글 읽어오기 
		goViewComment("1"); // 페이징처리 한 댓글 읽어오기

		/* if(typeof "2" == "string") {
			alert("호호호");
		} */
		
	}); // end of $(document).ready(function(){})----------------
	
	
	// === 댓글쓰기 === //
	function goAddWrite() {
		var frm = document.addWriteFrm;
		var contentVal = frm.content.value.trim();
		if(contentVal=="") {
			alert("댓글 내용을 입력하세요!!");
			return;
		}
		
		var form_data = $("form[name=addWriteFrm]").serialize();
		
		$.ajax({
			url:"<%= request.getContextPath()%>/addComment.action",
			data:form_data,
			type:"POST",
			dataType:"JSON",
			success:function(json){
				if(json.n == 1) {
					// goReadComment(); // 페이징처리 안한 댓글 읽어오기
					goViewComment("1"); // 페이징처리 한 댓글 읽어오기
				} 
				else {
					alert("댓글쓰기 실패!!");
				}
				
				frm.content.value = "";
			},
			error: function(request, status, error){
				alert("code: "+request.status+"\n"+"message: "+request.responseText+"\n"+"error: "+error);
			}
		});
		
	}// end of function goAddWrite()------------------
	
	// === 페이징처리 안한 댓글 읽어오기  === //
	function goReadComment() {
		var frm = document.addWriteFrm;
		$.ajax({
			url:"<%= request.getContextPath()%>/readComment.action",
			data:{"parentSeq":"${boardvo.seq}"},
			dataType:"JSON",
			success:function(json){
				var html = "";
				if(json.length > 0) {
					$.each(json, function(index, item){
						html += "<tr>";
						html += "<td style='text-align: center;'>"+(index+1)+"</td>";
						html += "<td>"+item.content+"</td>";
						html += "<td style='text-align: center;'>"+item.name+"</td>";
						html += "<td style='text-align: center;'>"+item.regDate+"</td>";
						html += "</tr>";
					});
				}
				else {
					html += "<tr>";
					html += "<td colspan='4' style='text-align: center;'>댓글이 없습니다.</td>";
					html += "</tr>";
				}
				
				$("#commentDisplay").html(html);
				frm.content.value = "";
			},
			error: function(request, status, error){
				alert("code: "+request.status+"\n"+"message: "+request.responseText+"\n"+"error: "+error);
			}
		});	
	}// end of function goReadComment()--------------------
	
	// === #125. Ajax로 불러온 댓글내용을 페이징처리 한 댓글 읽어오기  === //
	function goViewComment(currentShowPageNo) {
		var frm = document.addWriteFrm;
		$.ajax({
			url:"<%= request.getContextPath()%>/commentList.action",
			data:{"parentSeq":"${boardvo.seq}",
				  "currentShowPageNo":currentShowPageNo},
			dataType:"JSON",
			success:function(json){
				var html = "";
				if(json.length > 0) {
					$.each(json, function(index, item){
						html += "<tr>";
						html += "<td style='text-align: center;'>"+(index+1)+"</td>";
						html += "<td>"+item.content+"</td>";
						html += "<td style='text-align: center;'>"+item.name+"</td>";
						html += "<td style='text-align: center;'>"+item.regDate+"</td>";
						html += "</tr>";
					});
				}
				else {
					html += "<tr>";
					html += "<td colspan='4' style='text-align: center;'>댓글이 없습니다.</td>";
					html += "</tr>";
				}
				
				$("#commentDisplay").html(html);
				
				// 페이지바 함수 호출
				makeCommentPageBar(currentShowPageNo);
			},
			error: function(request, status, error){
				alert("code: "+request.status+"\n"+"message: "+request.responseText+"\n"+"error: "+error);
			}
		});	
	}// end of function goReadComment()--------------------
	
	
	// ==== 댓글내용 페이지바 Ajax로 만들기 ====
	function makeCommentPageBar(currentShowPageNo) {
			
		$.ajax({
			url:"<%= request.getContextPath()%>/getCommentTotalPage.action",
			data:{"parentSeq":"${boardvo.seq}",
				  "sizePerPage":"5"},
			type:"GET",
			dataType:"JSON",
			success:function(json) {
				// console.log(json.totalPage);
				if(json.totalPage > 0) {
					// 댓글이 있는 경우
					
					var totalPage = json.totalPage;
					
					var pageBarHTML = "<ul style='list-style: none;'>";
					
					var blockSize = 10;
					// blockSize 는 1개 블럭(토막)당 보여지는 페이지번호의 개수 이다.
					/*
					      1 2 3 4 5 6 7 8 9 10  다음                   -- 1개블럭
					   이전  11 12 13 14 15 16 17 18 19 20  다음   -- 1개블럭
					   이전  21 22 23
					*/
					
					var loop = 1;
					/*
					    loop는 1부터 증가하여 1개 블럭을 이루는 페이지번호의 개수[ 지금은 10개(== blockSize) ] 까지만 증가하는 용도이다.
					*/
					
					if(typeof currentShowPageNo == "string") {
						currentShowPageNo = Number(currentShowPageNo);
					}
					
					var pageNo = Math.floor((currentShowPageNo - 1)/blockSize) * blockSize + 1;
					/*
								(2 - 1)/10	1/10 ==> Math.floor(0.1) ==> 0    
					*/
					
					
				/*
				    1  2  3  4  5  6  7  8  9  10  -- 첫번째 블럭의 페이지번호 시작값(pageNo)은 1 이다.
				    11 12 13 14 15 16 17 18 19 20  -- 두번째 블럭의 페이지번호 시작값(pageNo)은 11 이다.
				    21 22 23 24 25 26 27 28 29 30  -- 세번째 블럭의 페이지번호 시작값(pageNo)은 21 이다.
				    
				    currentShowPageNo         pageNo
				   ----------------------------------
				         1                      1 = ((1 - 1)/10) * 10 + 1
				         2                      1 = ((2 - 1)/10) * 10 + 1
				         3                      1 = ((3 - 1)/10) * 10 + 1
				         4                      1
				         5                      1
				         6                      1
				         7                      1 
				         8                      1
				         9                      1
				         10                     1 = ((10 - 1)/10) * 10 + 1
				        
				         11                    11 = ((11 - 1)/10) * 10 + 1
				         12                    11 = ((12 - 1)/10) * 10 + 1
				         13                    11 = ((13 - 1)/10) * 10 + 1
				         14                    11
				         15                    11
				         16                    11
				         17                    11
				         18                    11 
				         19                    11 
				         20                    11 = ((20 - 1)/10) * 10 + 1
				         
				         21                    21 = ((21 - 1)/10) * 10 + 1
				         22                    21 = ((22 - 1)/10) * 10 + 1
				         23                    21 = ((23 - 1)/10) * 10 + 1
				         ..                    ..
				         29                    21
				         30                    21 = ((30 - 1)/10) * 10 + 1
				*/
				}
				
				// === [이전] 만들기 === 
				if(pageNo != 1) {
					pageBarHTML += "<li style='display:inline-block; width:50px; font-size:12pt;'><a href='javascript:goViewComment(\""+pageNo-1+"\")'>[이전]</a></li>";
				}
				
				while( !(loop > blockSize || pageNo > totalPage) ) {
					
					if(pageNo == currentShowPageNo) {
						pageBarHTML += "<li style='display:inline-block; width:30px; font-size:12pt; border:solid 1px gray; color:red; padding:2px 4px;'>"+pageNo+"</li>";
					}
					else {
						pageBarHTML += "<li style='display:inline-block; width:30px; font-size:12pt;'><a href='javascript:goViewComment(\""+pageNo+"\")'>"+pageNo+"</a></li>";
					}
					
					loop++;
					pageNo++;
					
				}// end of while------------------------------
				
				
				// === [다음] 만들기 ===
				if( !(pageNo > totalPage) ) {
					pageBarHTML += "<li style='display:inline-block; width:50px; font-size:12pt;'><a href='javascript:goViewComment(\""+pageNo+"\")'>[다음]</a></li>";
				}
				
				pageBar += "</ul>";
				
				$("#pageBar").html(pageBarHTML);
				pageBarHTML = "";
				
			},
			error: function(request, status, error){
				alert("code: "+request.status+"\n"+"message: "+request.responseText+"\n"+"error: "+error);
			}
		});
		
	} // function makeCommentPageBar(currentShowPageNo)
	
</script>

<div style="padding-left: 10%;">
	<h1>글내용보기</h1>
	
	<table id="table" style="word-wrap: break-word; table-layout: fixed;">
		<tr>
			<th>글번호</th>
			<td>${boardvo.seq}</td>
		</tr>
		<tr>
			<th>성명</th>
			<td>${boardvo.name}</td>
		</tr>
		<tr>
			<th>제목</th>
			<td>${boardvo.subject}</td>
		</tr>
		<tr>
			<th>내용</th>
			<td>
			 <p style="word-break: break-all;">${boardvo.content}</p>
			 <%-- 
			      style="word-break: break-all; 은 공백없는 긴영문일 경우 width 크기를 뚫고 나오는 것을 막는 것임. 
			           그런데 style="word-break: break-all; 나 style="word-wrap: break-word; 은
			           테이블태그의 <td>태그에는 안되고 <p> 나 <div> 태그안에서 적용되어지므로 <td>태그에서 적용하려면
			      <table>태그속에 style="word-wrap: break-word; table-layout: fixed;" 을 주면 된다.
			 --%>
			</td>
		</tr>
		<tr>
			<th>조회수</th>
			<td>${boardvo.readCount}</td>
		</tr>
		<tr>
			<th>날짜</th>
			<td>${boardvo.regDate}</td>
		</tr>
		
		<!-- === #158. 첨부파일 및 파일크기가 있으면 알려준다.  -->
		<tr>
			<th>첨부파일</th>
			<td>
				<c:if test="${not empty sessionScope.loginuser}">
					<a href="<%= request.getContextPath()%>/download.action?seq=${boardvo.seq}">${boardvo.orgFilename}</a>
				</c:if>
				
				<c:if test="${empty sessionScope.loginuser}">
					${boardvo.orgFilename}
				</c:if>
			</td>
		</tr>
		<tr>
			<th>파일사이즈</th>
			<td>
				${boardvo.fileSize}
			</td>
		</tr>
	</table>
	
	<br/>
	
	<div style="margin-bottom: 1%;">이전글&nbsp;:&nbsp;<span class="move" onclick="javascript:location.href='view.action?seq=${boardvo.previousseq}'">${boardvo.previoussubject}</span></div>
	<div style="margin-bottom: 1%;">다음글&nbsp;:&nbsp;<span class="move" onclick="javascript:location.href='view.action?seq=${boardvo.nextseq}'">${boardvo.nextsubject}</span></div>
	
	<br/>
	
	<%-- <button type="button" onclick="javascript:location.href='<%= request.getContextPath()%>/list.action'">목록보기</button> --%>
	<button type="button" onclick="javascript:location.href='<%= request.getContextPath()%>/${gobackURL }'">목록보기</button>
	<button type="button" onclick="javascript:location.href='<%= request.getContextPath()%>/edit.action?seq=${boardvo.seq}'">수정</button>
	<button type="button" onclick="javascript:location.href='<%= request.getContextPath()%>/del.action?seq=${boardvo.seq}'">삭제</button>
	
	<!-- // === #136. 어떤 글에 대한 답변 글쓰기는 로그인 되어진 회원의 gradelevel 값이 10 인 회원만('admin', 'seoyh') 가능하도록 하겠다.  -->
	<c:if test="${sessionScope.loginuser.gradelevel == 10}">
		<button type="button" onclick="javascript:location.href='<%= request.getContextPath()%>/add.action?fk_seq=${boardvo.seq}&groupno=${boardvo.groupno}&depthno=${boardvo.depthno}'">답변글쓰기</button>
	</c:if>
	
	<!-- === #83. 댓글쓰기 폼 추가 === -->
	<c:if test="${not empty sessionScope.loginuser}">
		<h3 style="margin-top: 50px;">댓글쓰기 및 보기</h3>
		<form name="addWriteFrm" style="margin-top: 20px;">
			      <input type="hidden" name="fk_userid" value="${sessionScope.loginuser.userid}" />
			성명 : <input type="text" name="name" value="${sessionScope.loginuser.name}" class="short" readonly />  
			&nbsp;&nbsp;
			댓글내용 : <input id="commentContent" type="text" name="content" class="long" /> 
			
			<!-- 댓글에 달리는 원게시물 글번호(즉, 댓글의 부모글 글번호) -->
			<input type="hidden" name="parentSeq" value="${boardvo.seq}" /> 
			
			<button id="btnComment" type="button" onclick="goAddWrite()">확인</button> 
			<button type="reset">취소</button> 
		</form>
	</c:if>
	<c:if test="${empty sessionScope.loginuser}">
		<h3 style="margin-top: 50px;">댓글보기</h3>
	</c:if>
	
	<!-- ===== #94. 댓글 내용 보여주기 ===== -->
	<table id="table2" style="margin-top: 2%; margin-bottom: 3%;">
		<thead>
		<tr>
		    <th style="width: 10%; text-align: center;">번호</th>
			<th style="width: 60%; text-align: center;">내용</th>
			<th style="width: 10%; text-align: center;">작성자</th>
			<th style="text-align: center;">작성일자</th>
		</tr>
		</thead>
		<tbody id="commentDisplay"></tbody>
	</table>
	
	<!-- ===== #134. 댓글 페이지바 ===== -->
	<div id="pageBar" style="border: solid 0px gray; width: 70%; margin: 25px auto; padding-left: 150px;"></div>
	
</div>





    
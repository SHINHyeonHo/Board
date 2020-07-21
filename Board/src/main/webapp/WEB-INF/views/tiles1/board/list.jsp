<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>    

<% String ctxPath = request.getContextPath(); %>

<style type="text/css">
	table, th, td {border: solid 1px gray;}

    #table {width: 970px; border-collapse: collapse;}
    #table th, #table td {padding: 5px;}
    #table th {background-color: #DDD;}
     
    .subjectStyle {font-weight: bold;
                   color: navy;
                   cursor: pointer;} 
</style>

<script type="text/javascript">

	$(document).ready(function(){
		
		$(".subject").bind("mouseover", function(event){
			var $target = $(event.target);
			$target.addClass("subjectStyle");
		});
		
		$(".subject").bind("mouseout", function(event){
			var $target = $(event.target);
			$target.removeClass("subjectStyle");
		});
		
		$("#searchWord").keydown(function(event) {
			 if(event.keyCode == 13) {
				 // 엔터를 했을 경우
				 goSearch();
			 }
		 });
		
		// 검색시 검색조건 및 검색어 값 유지시키기 
		if(${paraMap != null}) {
			$("#searchType").val("${paraMap.searchType}");
			$("#searchWord").val("${paraMap.searchWord}");
		}
		
		<%-- === #105. 검색어 입력시 검색자동완성 하기 2 === --%>
		$("#displayList").hide();
		
		$("#searchWord").keyup(function() {

			var wordLength = $(this).val().length;
			
			if(wordLength == 0) {
				$("#displayList").hide();
				// 검색어 입력후 백스페이스키를 눌러서 검색어를 모두 지우면 검색된 내용이 안 나오도록 해야 한다.
			}
			else {
				$.ajax({
					url:"<%=request.getContextPath()%>/wordSearchShow.action",
					type:"GET",
					data:{searchType:$("#searchType").val()
						, searchWord:$("#searchWord").val()},
					dataType:"JSON",
					success:function(json) {
						
						// === #11. 검색어 입력시 자동글 완성하기 7 === //ㅡ
						if(json.length > 0) {
							// 검색된 데이터가 있는경우
							
							var html = "";
							
							$.each(json, function(extraIndex, item){
								var word = item.word;
								
								var index = word.toLowerCase().indexOf($("#searchWord").val().toLowerCase() );
								// console.log("index : " + index);
								var len = $("#searchWord").val().length;

								var result = "";
								
								// console.log(word.substr(0,index));	// 검색어 앞까지의 글자
								// console.log(word.substr(index,len));	// 검색어 글자
								// console.log(word.substr(index+len)); // 검색어 뒤부터의 글자
								
								result = "<span style='color:blue;'>" + word.substr(0,index) + "</span>" + "<span style='color:red;'>" + word.substr(index, len) + "</span>" + "<span style='color:blue;'>" + word.substr(index+len) + "</span>" ;
								
								html += "<span style='cursor:pointer;' class='result'>" + result + "</span><br/>";
							});
							
							$("#displayList").html(html);
							$("#displayList").show();
						}
						else {
							// 검색된 데이터가 없는경우
							$("#displayList").hide();
						}
					},
					error: function(request, status, error){
						alert("code: "+request.status+"\n"+"message: "+request.responseText+"\n"+"error: "+error);
					}
				});
			}
		}); // $("#searchWord").keyup(function(){})
		
		<%-- === #111. 검색어 이력시 자동글 완성하기 8 === --%>
		$(document).on("click", ".result", function() {
			var word = $(this).text();
			$("#searchWord").val(word);
			$("#displayList").hide();
			goSearch();
		});
		
		/*
		이렇게 하면 안된다... script 에서 가져온 것이니까 위에처럼 해야한다.
		$("#displayList").click(function(event){
			var $target = $(event.target);
			if($target.is(".result")) {
				alert("check");
			}
		});
		 */
		
	 });// end of $(document).ready(function(){})-------------------
	 
	 function goView(seq) {
		 location.href="<%=ctxPath%>/view.action?seq="+seq;
	 } // end of function goView(seq) {}
	 
	 function goSearch() {
		 var searchWord = $("#searchWord").val();
		 
		 if(searchWord == "") {
			 alert("검색어를 입력해주세요.");
			 return;
		 }
		 
	 	var frm = document.searchFrm;
		frm.method = "GET";
		frm.action = "<%= request.getContextPath()%>/search.action";
		frm.submit();
	 }
 
</script>
	
<div style="padding-left: 3%;">
	<h2 style="margin-bottom: 30px;">글목록</h2>
	
	<table id="table">
		<tr>
			<th style="width: 70px;  text-align: center;">글번호</th>
			<th style="width: 360px; text-align: center;">제목</th>
			<th style="width: 70px;  text-align: center;">성명</th>
			<th style="width: 180px; text-align: center;">날짜</th>
			<th style="width: 70px;  text-align: center;">조회수</th>
		</tr>	
		<c:if test="${boardList != null}">
		<c:forEach var="boardvo" items="${boardList}" varStatus="status">
			<tr>
				<td align="center">${boardvo.seq}</td>
				<td align="left">
					<%-- === 댓글쓰기가 없는 게시판 시작=== --%>
					<%-- <span class="subject" onclick="goView('${boardvo.seq}')">${boardvo.subject}</span> --%>
					<%-- === 댓글쓰기가 없는 게시판 끝=== --%>
					
					<%-- === 댓글쓰기가 있는 게시판 시작=== --%>
					<c:if test="${boardvo.commentCount > 0}">
						<span class="subject" onclick="goView('${boardvo.seq}')">${boardvo.subject}&nbsp;<span style="vertical-align: super;">[<span style="color:red; font-size: 9pt; font-style:italic; font-weight: bold;">${boardvo.commentCount}</span> ]</span></span>
					</c:if>
					<c:if test="${boardvo.commentCount == 0 }">
						<span class="subject" onclick="goView('${boardvo.seq}')">${boardvo.subject}</span>
					</c:if>
					<%-- === 댓글쓰기가 있는 게시판  끝=== --%>
				</td>
				<td align="center">${boardvo.name}</td>
				<td align="center">${boardvo.regDate}</td>
				<td align="center">${boardvo.readCount}</td>
		</c:forEach>
		</c:if>
		<c:if test="${boardList == null}">
			<tr>
				<td>게시물이 존재하지 않습니다.</td>
			</tr>
		</c:if>
	</table>
	
	<%-- === #99. 글검색 폼 추가하기 : 글제목, 글쓴이로 검색을 하도록 한다. === --%> 
	<form name="searchFrm" style="margin-top: 20px;">
		<select name="searchType" id="searchType" style="height: 26px;">
			<option value="subject">글제목</option>
			<option value="name">글쓴이</option>
		</select>
		<input type="text" name="searchWord" id="searchWord" size="40" autocomplete="off" /> 
		<button type="button" onclick="goSearch()">검색</button>
	</form>
	
	<%-- === #104. 검색어 입력시 검색자동완성 하기 1 === --%>
	<div id="displayList" style="border: solid 1px gray; border-top:0px; width: 318px; height: 100px; margin-left: 69px; margin-top:-1px; overflow: auto;">
	
	</div>
	
</div>		




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
		
		// 쓰기버튼
		$("#btnWrite").click(function() {
			
			// 글제목 유효성 검사
			var subjectVal = $("#subject").val().trim();
			if(subjectVal == "") {
				alert("글제목을 입력하세요!!");
				return;
			}
			
			// 글내용 유효성 검사
			var contentVal = $("#content").val().trim();
			if(contentVal == "") {
				alert("글내용을 입력하세요!!");
				return;
			}
			
			// 글암호 유효성 검사
			var pwVal = $("#pw").val().trim();
			if(pwVal == "") {
				alert("글암호을 입력하세요!!");
				return;
			}
			
			// 폼(form) 을 전송(submit)
			var frm = document.addFrm;
			frm.method = "POST";
			frm.action = "<%= ctxPath%>/addEnd.action";
			frm.submit();
			
		});
		
	}); // end of $(document).ready(function(){})
	
	
	function goPrint(title) {
		
		var sw = screen.width; // 화면 가로길이
		var sh = screen.height; // 화면 세로길이
		var popw = 800; // 팝업창 가로길이
		var poph = 600; // 팝업창 세로길이
		var xpos = (sw-popw)/2; // 화면중앙에 띄우도록 한다.
		var ypos = (sh-poph)/2;// 화면중앙에 띄우도록 한다.
		
		var popWin = window.open("", "print", "width="+popw+", height="+poph+", top="+ypos+", left="+xpos+", status=yes, scrollbars=yes");
		// 일단 내용이 없는 팝업윈도우창을 만든다.
		
		popWin.document.open(); // 팝업윈도우창에 내용을 넣을 수 있도록 열어주어야한다.( 오픈한다.)
	
		// 팝업윈도우창에 내용을 입력한다.
		popWin.document.write("<html><head><style type='text/css'>*{color:blue;}</style><body onload='window.print()'><body onload='window.print()'>");
		popWin.document.write(document.getElementById("subject").value);
		popWin.document.write("<br/><pre>안녕");
		popWin.document.write("</pre></body></html>");
		
		popWin.document.close(); // 팝업윈도우창 문서를 닫는다.
		popWin.print(); // 팝업윈도우창에 대한 인쇄창을 띄우고
		popWin.close(); // 인쇄를 하던가 또는 취소를 누르면 팝업윈도우창을 닫는다.
	
		
		
	} // end of function goPrint(title) {}
</script>

<div style="padding-left: 10%;">
	<h1>글쓰기</h1>
	
	<form name="addFrm">
		<table id="table">
			<tr>
				<th>성명</th>
				<td>
					<input type="hidden" name="fk_userid" value="${sessionScope.loginuser.userid}" />
					<input type="text" name="name" value="${sessionScope.loginuser.name}" class="short" readonly/>
				</td>
			</tr>
			<tr>
				<th>글제목</th>
				<td>
					<input type="text" name="subject" id="subject" class="long" />
				</td>
			</tr>
			<tr>
				<th>글내용</th>
				<td>
					<textarea rows="10" cols="100" name="content" id="content" style="width: 95%; height: 412px;"></textarea>
				</td>
			</tr>
			<tr>
				<th>글암호</th>
				<td>
					<input type="password" name="pw" id="pw" class="short" />
				</td>
			</tr>
		</table>
		
		<div style="margin: 20px;">
			<button type="button" id="btnWrite">쓰기</button>
			<button type="button" onclick="javascript:history.back()">취소</button>
			<button type="button" onclick="goPrint('글쓰기인쇄')">인쇄</button>
		</div>
		
	</form>
	
</div>











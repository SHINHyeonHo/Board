<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>    

<%-- //=== #167. (웹채팅관련5) ===  --%>

<script type="text/javascript" >

    // === !!! WebSocket 통신은 스크립트로 작성하는 것이다. !!! === //
    $(document).ready(function(){
        
        var url = window.location.host;   // 웹브라우저의 주소창의 포트까지 가져옴
    //  alert("url : " + url);
    //  결과값  url: 192.168.50.65:9090
    	    
    	var pathname = window.location.pathname; // '/'부터 오른쪽에 있는 모든 경로
    // 	alert("pathname : " + pathname);
    //  결과값  pathname : /board/chatting/multichat.action
    	 	
    	var appCtx = pathname.substring(0, pathname.lastIndexOf("/"));  // "전체 문자열".lastIndexOf("검사할 문자");   
    // 	alert("appCtx : " + appCtx);
    //  결과값  appCtx : /board/chatting
    	 	
    	var root = url+appCtx;
    // 	alert("root : " + root);
    //  결과값   root : 192.168.50.65:9090/board/chatting
   	
    	var wsUrl = "ws://"+root+"/multichatstart.action";  // 웹소켓통신을 하기위해서는 http:// 을 사용하는 것이 아니라 ws:// 을 사용해야 한다. 
       	var websocket = new WebSocket(wsUrl);  //  /WEB-INF/web.xml 에 가서 appServlet 의 contextConfigLocation 을 수정한다. 
     // var websocket = new WebSocket("ws://192.168.50.65:9090/board/chatting/multichatstart.action");
        
     // alert(wsUrl);
    	
    	// >>>> === Javascript WebSocket 이벤트 정리 === <<<< 
	    //      onopen    ==>  WebSocket 연결
	    //      onmessage ==>  메시지 수신
	    //      onerror   ==>  전송 에러 발생
	    //      onclose   ==>  WebSocket 연결 해제
    	
    	var messageObj = {}; // 자바스크립트에서 객체 생성함.
    	
	    // === 웹소켓에 최초로 연결이 되었을 경우에 실행되어지는 콜백함수 ===
    	websocket.onopen = function() {
    	//	alert("웹소켓 연결됨!!");
    		$("#chatStatus").text("정보: 웹소켓에 연결이 성공됨!!");
    	
    	/*	
            messageObj = {};  // 초기화
            messageObj.message = "반갑습니다.";
            messageObj.type = "all";
            messageObj.to = "all";
        */    
            // 또는
            messageObj = { message : "채팅방에 <span style='color: red;'>입장</span>했습니다"
        		     	 , type : "all"
        		     	 , to : "all" };  // 자바스크립트에서 객체의 데이터값 초기화
        		     	
            websocket.send(JSON.stringify(messageObj));
                        // JSON.stringify() 는 값을 그 값을 나타내는 JSON 표기법의 문자열(String)로 변환한다
                        /*
	                        JSON.stringify({});                  // '{}'
							JSON.stringify(true);                // 'true'
							JSON.stringify('foo');               // '"foo"'
							JSON.stringify([1, 'false', false]); // '[1,"false",false]'
							JSON.stringify({ x: 5 });            // '{"x":5}'
                        */
        };
    	
    	// === 메시지 수신 콜백함수
        websocket.onmessage = function(evt) {
            $("#chatMessage").append(evt.data);
            $("#chatMessage").append("<br />");
            $("#chatMessage").scrollTop(99999999);
        };
        
        // === 웹소캣 연결 해제 콜백함수
        websocket.onclose = function() {
            // websocket.send("채팅을 종료합니다.");
        }
         
        
        $("#message").keydown(function (key) {
             if (key.keyCode == 13) {
                $("#sendMessage").click();
             }
          });
         
        $("#sendMessage").click(function() {
            if( $("#message").val() != "") {
                
            	// ==== 자바스크립트에서 replace를 replaceAll 처럼 사용하기 ====
                // 자바스크립트에서 replaceAll 은 없다.
                // 정규식을 이용하여 대상 문자열에서 모든 부분을 수정해 줄 수 있다.
                // 수정할 부분의 앞뒤에 슬래시를 하고 뒤에 gi 를 붙이면 replaceAll 과 같은 결과를 볼 수 있다.
                var messageVal = $("#message").val();
                messageVal = messageVal.replace(/<script/gi, "&lt;script"); // 스크립트 공격을 막으려고 한 것임.
            	
                messageObj = {};
                messageObj.message = messageVal;
                messageObj.type = "all";
                messageObj.to = "all";
                 
                var to = $("#to").val();
                if ( to != "" ) {
                    messageObj.type = "one";
                    messageObj.to = to;
                }
                 
                websocket.send(JSON.stringify(messageObj));
                // JSON.stringify() 는 값을 그 값을 나타내는 JSON 표기법의 문자열로 변환한다
                
                $("#chatMessage").append("<span style='color:navy; font-weight:bold;'>[나] ▷ " + messageVal + "</span><br/>");
                $("#chatMessage").scrollTop(99999999);
                 
                $("#message").val("");
            }
        });
        
        /////////////////////////////////////////////////////////////
        
        $("#btnAllDialog").hide();
        
        // 아래는 귓속말을 위해서 대화를 나누는 상대방의 이름을 클릭하면 상대방IP주소를 귓속말대상IP주소에 입력하도록 하는 것.
        $(document).on("click",".loginuserName",function(){
        	/* class loginuserName 은 
        	   com.spring.chatting.websockethandler.WebsocketEchoHandler 의 
        	   protected void handleTextMessage(WebSocketSession wsession, TextMessage message) 메소드내에
        	      기재해두었음.
        	*/
        	var ip = $(this).prev().text();
        //	alert(ip);
            $("#to").val(ip); 
            
            $("#privateWho").text($(this).text());
            $("#btnAllDialog").show();
        });
        
        
        $("#btnAllDialog").click(function(){
        	// 귀속말대화끊기 버튼을 클릭한 경우. 
        	// 전체대상으로 채팅하겠다는 말이다. 
            	$("#to").val("");
            	$("#privateWho").text("");
            	$(this).hide();
        });
        
    });// end of $(document).ready()----------------------
</script>
</head>
<body>
	<div id="chatStatus"></div><br/>
	<input type="text" id="to" placeholder="귓속말대상IP주소"/><br/>
	귓속말대상 : <span id="privateWho" style="font-weight: bold; color: red;"></span>
	&nbsp;&nbsp;<button type="button" id="btnAllDialog">귀속말대화끊기</button><br/>
	<input type="text" id="message" size="50" placeholder="메시지 내용"/>
    <input type="button" id="sendMessage" value="보내기" />
    <input type="button" onClick="javascript:location.href='<%=request.getContextPath() %>/index.action'" value="채팅방나가기" />
    
    <div id="chatMessage" style="overFlow: auto; max-height: 500px;"></div>
</body>
</html>
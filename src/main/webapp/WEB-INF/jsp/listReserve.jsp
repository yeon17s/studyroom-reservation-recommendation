<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<%@ page import="java.util.Locale" %>
<%@ page import="java.time.LocalDate" %>
<%@ page import="java.time.format.TextStyle" %>
<%@ page import="com.example.studyroom_reservation_recommendation.entity.Reservation" %>
<%
    List<Reservation> list = (List<Reservation>) request.getAttribute("list");
    String keyword = (String) request.getAttribute("searchKeyword");
    if(keyword == null) keyword = "";
%>

<div class="list-header">
    <button type="button" class="delete-btn" onclick="submitDeleteForm()">삭제</button>
	<form action="<%= request.getContextPath() %>/reserve" method="GET" class="search-form">
        <input type="hidden" name="action" value="search">
        <div class="search-box">
            <input type="text" name="keyword" value="<%=keyword%>" placeholder="이름/학번 검색">
            <button type="submit" class="search-btn">🔍</button>
        </div>
    </form>
</div>

<form id="deleteForm" action="<%= request.getContextPath() %>/reserve" method="POST">
    <input type="hidden" name="action" value="delete">
		
	<table class="status-table">
	    <thead>
	        <tr>
	        	<th><input type="checkbox" onclick="toggleAll(this)"></th>
	            <th>예약 번호</th>
	            <th>이름</th>
	            <th>학번</th>
	            <th>날짜</th>
	            <th>시간</th>
	            <th>인원</th>
	            <th>목적</th>
	        </tr>
	    </thead>
	    <tbody>
	    <% 
	    if (list != null && !list.isEmpty()) { 
	        for (Reservation r : list) { 
	        	// 날짜에 요일 붙이는 로직
	        	String rawDate = r.getDate(); // "2025-12-04"
	        	String formattedDate = rawDate; // 기본값
	        	
	        	try {
	        		if(rawDate != null && !rawDate.isEmpty()) {
	        			// 1. 날짜 문자열을 날짜 객체로 변환
	        			LocalDate dateObj = LocalDate.parse(rawDate);
	        			// 2. 요일 구하기 (한국어, 짧은 형태 "월", "화")
	        			String dayOfWeek = dateObj.getDayOfWeek().getDisplayName(TextStyle.NARROW, Locale.KOREAN);
	        			// 3. 합치기: "2025-12-04(목)"
	        			formattedDate = rawDate + "(" + dayOfWeek + ")";
	        		}
	        	} catch(Exception e) {
	        		// 날짜 형식이 안 맞을 경우 원래 문자열 그대로 출력
	        	}
	    %>
	        <tr>
	       		<td><input type="checkbox" name="deleteIds" value="<%= r.getId() %>"></td>
	            <td><%= r.getId() %></td>
	            <td><%= r.getName() %></td>
	            <td><%= r.getStudentId() %></td>
	            <td><%= formattedDate %></td>
	            <td><%= r.getTimeSlot() %></td>
	            <td><%= r.getPeople() %>명</td>
	            <td><%= r.getPurpose() %></td>
	        </tr>
	    <%  
	        } 
	    } else { 
	    %>
	        <tr>
	            <td colspan="8" style="text-align: center; padding: 20px;">등록된 예약이 없습니다.</td>
	        </tr>
	    <% 
	    } 
	    %>
	    </tbody>
	</table>
</form>

<script>
function toggleAll(source) {
    checkboxes = document.getElementsByName('deleteIds');
    for(var i=0, n=checkboxes.length;i<n;i++) {
        checkboxes[i].checked = source.checked;
    }
}
function submitDeleteForm() {
    const checkboxes = document.querySelectorAll('input[name="deleteIds"]:checked');
    if (checkboxes.length === 0) {
        alert("삭제할 항목을 선택해주세요.");
        return;
    }
    if (confirm("선택한 " + checkboxes.length + "개의 예약을 삭제하시겠습니까?")) {
        document.getElementById("deleteForm").submit();
    }
}
</script>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<% 
    // 성공 메시지 확인
    String successMsg = request.getParameter("success");
    
    // 에러 메시지 및 입력값 확인 
    String errorMsg = (String) request.getAttribute("errorMsg");
    String prevName = (String) request.getAttribute("name");
    String prevStudentId = (String) request.getAttribute("student_id");
    String prevDate = (String) request.getAttribute("date");
    String prevTimeSlot = (String) request.getAttribute("time_slot");
    String prevPeople = (String) request.getAttribute("people");
    String prevPurpose = (String) request.getAttribute("purpose");

    // null 방지 처리 (null이면 빈 문자열로)
    if(prevName == null) prevName = "";
    if(prevStudentId == null) prevStudentId = "";
    if(prevDate == null) prevDate = "";
    if(prevTimeSlot == null) prevTimeSlot = "";
    if(prevPeople == null) prevPeople = "";
    if(prevPurpose == null) prevPurpose = "";
%>

<% if ("true".equals(successMsg)) { %>
    <div id="success-msg" class="success-message">
        예약이 성공적으로 등록되었습니다!
    </div>
<% } %>

<% if (errorMsg != null) { %>
    <div id="server-error-msg" class="server-error-message">
        <%= errorMsg %>
    </div>
<% } %>

<form id="reservation-form" action="<%= request.getContextPath() %>/reserve" method="POST" onsubmit="return validateReserve()">
    <input type="hidden" name="action" value="insert">
    
    <div class="form-group">
        <div class="label-error-row">
            <label for="name" class="input-label">이름</label>
            <div id="name-error" class="error-message"></div>
        </div>
        <input type="text" id="name" name="name" class="input-box" placeholder="예약자 이름" value="<%=prevName%>">
    </div>

    <div class="form-group">
        <div class="label-error-row">
            <label for="student_id" class="input-label">학번</label>
            <div id="student-id-error" class="error-message"></div>
        </div>
        <input type="text" id="student_id" name="student_id" class="input-box" placeholder="학번 (예: 202512345)" value="<%=prevStudentId%>">
    </div>

    <div class="form-group">
        <div class="label-error-row">
            <label for="date" class="input-label">날짜</label>
            <div id="date-error" class="error-message"></div>
        </div>
        <input type="date" id="date" name="date" class="input-box" value="<%=prevDate%>">
    </div>

    <div class="form-group">
        <div class="label-error-row">
            <label for="time_slot" class="input-label">시간대</label>
            <div id="time-slot-error" class="error-message"></div>
        </div>
        <select id="time_slot" name="time_slot" class="input-box">
            <option value="" disabled <%= "".equals(prevTimeSlot) ? "selected" : "" %>>--시간 선택--</option>
            <option value="09:00-11:00" <%= "09:00-11:00".equals(prevTimeSlot) ? "selected" : "" %>>09:00 - 11:00</option>
            <option value="11:00-13:00" <%= "11:00-13:00".equals(prevTimeSlot) ? "selected" : "" %>>11:00 - 13:00</option>
            <option value="13:00-15:00" <%= "13:00-15:00".equals(prevTimeSlot) ? "selected" : "" %>>13:00 - 15:00</option>
            <option value="15:00-17:00" <%= "15:00-17:00".equals(prevTimeSlot) ? "selected" : "" %>>15:00 - 17:00</option>
            <option value="17:00-19:00" <%= "17:00-19:00".equals(prevTimeSlot) ? "selected" : "" %>>17:00 - 19:00</option>
            <option value="19:00-21:00" <%= "19:00-21:00".equals(prevTimeSlot) ? "selected" : "" %>>19:00 - 21:00</option>
        </select>
    </div>

    <div class="form-group">
        <div class="label-error-row">
            <label for="people" class="input-label">인원</label>
            <div id="people-error" class="error-message"></div>
        </div>
        <input type="number" id="people" name="people" class="input-box" placeholder="인원 수 (2~8명)" value="<%=prevPeople%>">
    </div>

    <div class="form-group">
        <div class="label-error-row">
            <label for="purpose" class="input-label">목적</label>
            <div id="purpose-error" class="error-message"></div>
        </div>
        <textarea id="purpose" name="purpose" class="input-box" rows="3" placeholder="스터디 목적을 입력하세요"><%=prevPurpose%></textarea>
    </div>
</form>

<div class="button-group">
    <button type="button" class="cancel-button" onclick="location.href='<%= request.getContextPath() %>/reserve?action=main'">취소</button>
    <button type="submit" form="reservation-form" class="submit-button">예약 등록</button>
</div>
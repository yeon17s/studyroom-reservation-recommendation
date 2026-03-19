<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.example.studyroom_reservation_recommendation.entity.Reservation" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<%
    // 성공 메시지 확인 (URL 파라미터에서 가져옴)
    String successMsg = request.getParameter("success");
    
    // 에러 메시지 확인 (컨트롤러에서 addFlashAttribute로 보낸 값)
    String errorMsg = (String) request.getAttribute("errorMsg");

    // 입력 데이터 유지를 위해 객체 꺼내기
    Reservation prev = (Reservation) request.getAttribute("prevData");

    // 각 변수에 값 할당 (객체가 null이면 빈 문자열로 처리)
    String prevName = (prev != null) ? prev.getName() : "";
    String prevStudentId = (prev != null) ? prev.getStudentId() : "";
    String prevPurpose = (prev != null) ? prev.getPurpose() : "";
    String prevDate = (prev != null) ? prev.getDate() : "";
    String prevTimeSlot = (prev != null) ? prev.getTimeSlot() : "";

    // 인원수가 0이면(초기 상태) 빈 문자열, 값이 있으면 그 숫자 출력
    String prevPeople = (prev != null && prev.getPeople() != 0) ? String.valueOf(prev.getPeople()) : "";
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
        <sec:authorize access="isAuthenticated()">
            <input type="text" id="name" name="name" class="input-box"
                value="<%=prevName%>" placeholder="예약자 이름을 입력하세요">
        </sec:authorize>
        <sec:authorize access="isAnonymous()">
            <input type="text" id="name" name="name" class="input-box" placeholder="로그인이 필요합니다" disabled>
        </sec:authorize>
    </div>

    <div class="form-group">
        <div class="label-error-row">
            <label for="student_id" class="input-label">학번</label>
            <div id="student-id-error" class="error-message"></div>
        </div>
        <sec:authorize access="isAuthenticated()">
            <input type="text" id="student_id" name="studentId" class="input-box" placeholder="학번 (예: 202512345)"
                value="<sec:authentication property='principal.username'/>" readonly>
        </sec:authorize>
        <sec:authorize access="isAnonymous()">
            <input type="text" id="student_id" name="studentId" class="input-box" placeholder="로그인이 필요합니다." disabled>
        </sec:authorize>
    </div>

    <div class="form-group">
        <div class="label-error-row">
            <label for="purpose" class="input-label">목적</label>
            <div id="purpose-error" class="error-message"></div>
        </div>
        <textarea id="purpose" name="purpose" class="input-box" rows="3" placeholder="스터디 목적을 입력하세요"><%=prevPurpose%></textarea>
    </div>

    <div class="form-group">
        <div class="label-error-row">
            <label for="date" class="input-label">날짜</label>
            <div id="date-error" class="error-message"></div>
        </div>
        <input type="date" id="date" name="date" class="input-box" value="<%=prevDate%>">
    </div>

    <div class="recommend-section">
        <button type="button" class="recommend-btn" onclick="getRecommendation()">
            ✨ AI 맞춤형 시간대 추천받기
        </button>
        <div id="recommend-result">
            목적과 날짜를 입력해 주세요.
        </div>
    </div>

    <div class="form-group">
        <div class="label-error-row">
            <label for="time_slot" class="input-label">시간대</label>
            <div id="time-slot-error" class="error-message"></div>
        </div>
        <select id="time_slot" name="timeSlot" class="input-box">
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
</form>

<sec:authorize access="isAuthenticated()">
    <div class="button-group">
        <button type="button" class="cancel-button" onclick="location.href='<%= request.getContextPath() %>/reserve'">취소</button>
        <button type="submit" form="reservation-form" class="submit-button">예약 등록</button>
    </div>
</sec:authorize>
<sec:authorize access="isAnonymous()">
    <div id="no-login-msg" class="login-error-message">
        로그인 후 예약 가능합니다.
    </div>
</sec:authorize>

<script>
async function getRecommendation() {
    // 1. 화면에 입력된 날짜와 목적 데이터 가져오기
    const date = document.getElementById('date').value;
    const purpose = document.getElementById('purpose').value;
    const resultDiv = document.getElementById('recommend-result');

    // 날짜는 필수 값이므로 체크
    if (!date) {
        alert("추천을 받으려면 먼저 날짜를 선택해주세요!");
        return;
    }

    // 통신 중일 때 보여줄 로딩 메시지
    resultDiv.innerHTML = "<span style='color: #666;'>추천 알고리즘 분석 중... ⏳</span>";

    try {
        // 2. 백엔드 API 호출 (GET 방식) - encodeURIComponent로 한글 목적 깨짐 방지
        // JSP가 해석하지 못하게 $ 앞에 역슬래시(\) 추가
        const response = await fetch(`/api/recommend?date=\${date}&purpose=\${encodeURIComponent(purpose)}`);

        if (!response.ok) {
            throw new Error("API 통신 에러 발생");
        }

        // 3. JSON 데이터 파싱
        const data = await response.json();

        // 결과 0개일 때 처리
        if (data.length === 0) {
            resultDiv.innerHTML = `
                <div style="color: #666; background: #eee; padding: 10px; border-radius: 5px;">
                    😥 현재 모든 시간대가 혼잡하거나 조건에 맞는 쾌적한 장소가 없습니다. <br>
                    <strong>직접 다른 날짜나 시간대를 선택해 주세요.</strong>
                </div>`;
            return; // 결과가 없으므로 아래의 HTML 생성 로직을 타지 않고 여기서 종료!
        }

        // 4. 화면에 그릴 HTML 생성 (데이터가 1개 이상 있을 때만 실행)
        let html = `<strong>💡 AI 분석 결과 (Top 2)</strong><ul style="margin-top:10px; padding-left:20px;">`;

        data.forEach((item, index) => {
            // $ 앞에 전부 역슬래시(\) 추가
            html += `<li style="margin-bottom:12px;">
                        <span style="color:#007bff; font-weight:bold; font-size: 15px;">
                            \${index + 1}순위: \${item.recommendedTime}
                        </span>
                        <span style="color:#888; font-size: 12px;">(적합도 점수: \${item.score}점)</span><br>
                        <span style="color:#444;">\${item.reason}</span>
                     </li>`;
        });
        html += `</ul>`;

        // 5. 결과 영역에 삽입
        resultDiv.innerHTML = html;

    } catch (error) {
        console.error("추천 로드 실패:", error);
        resultDiv.innerHTML = "<span style='color:red;'>추천 정보를 불러오는 중 서버 오류가 발생했습니다.</span>";
    }
}

// 페이지가 로드될 때 실행되는 함수
window.onload = function() {
    // 1. 서버에서 전달된 에러 메시지(중복 예약 등)가 있다면 경고창 띄우기
    <% if (errorMsg != null && !errorMsg.isEmpty()) { %>
        alert("<%= errorMsg %>");
    <% } %>

    // 2. 성공 메시지(success=true)가 URL에 있다면 알림창 띄우기
    const urlParams = new URLSearchParams(window.location.search);
    if (urlParams.get('success') === 'true') {
        alert("예약이 성공적으로 등록되었습니다!");
    }
};
</script>
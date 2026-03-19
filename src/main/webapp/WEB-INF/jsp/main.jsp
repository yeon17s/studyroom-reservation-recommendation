<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>스터디룸 예약 시스템</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css">
    <script src="<%= request.getContextPath() %>/js/validateReserve.js"></script>
</head>
<body>
    <main class="main-layout">

        <!-- 왼쪽 예약 등록 섹션 -->
        <section class="reservation-section">
            <h1>새 예약 등록</h1>
            <jsp:include page="reserveForm.jsp" />
        </section>

        <!-- 오른쪽 예약 현황 섹션 -->
        <section class="status-section">
            <div class="login-container">
                <jsp:include page="login.jsp" />
            </div>

            <hr class="section-divider">

        	<h1>예약 현황 목록</h1>
            <jsp:include page="listReserve.jsp" />

            <sec:authorize access="hasRole('ADMIN')">
                <h1>예약 현황 차트 (관리자 전용)</h1>
                <jsp:include page="chartReserve.jsp" />
            </sec:authorize>
        </section>
    </main>
</body>
</html>
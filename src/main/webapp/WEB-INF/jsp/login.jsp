<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<div class="auth-box">
    <sec:authorize access="isAnonymous()">
        <form action="<%= request.getContextPath() %>/login" method="POST" class="login-form">
            <input type="text" name="username" placeholder="학번(아이디)" required class="auth-input">
            <input type="password" name="password" placeholder="비밀번호" required class="auth-input">
            <button type="submit" class="auth-btn login-btn">로그인</button>
        </form>
    </sec:authorize>

    <sec:authorize access="isAuthenticated()">
        <div class="welcome-box">
            <span class="welcome-text">
                <strong><sec:authentication property="principal.username"/></strong>로 로그인했습니다.
            </span>
            <form action="<%= request.getContextPath() %>/logout" method="POST" style="display: inline;">
                <button type="submit" class="auth-btn logout-btn">로그아웃</button>
            </form>
        </div>
    </sec:authorize>
</div>
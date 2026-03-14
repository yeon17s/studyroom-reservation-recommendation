<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<%@ page import="java.time.LocalDate" %>
<%@ page import="studyroom.model.Reservation" %>
<%
    // Controller에서 넘겨준 리스트 가져오기
    List<Reservation> list = (List<Reservation>) request.getAttribute("list");

	//1. 데이터 집계용 Map
    Map<String, Integer> dateMap = new TreeMap<>(); // 날짜별 (차트용)
    Map<String, Integer> timeMap = new HashMap<>(); // 시간대별 (차트, 분석용)
    Map<String, Integer> dayOfWeekMap = new HashMap<>(); // 요일별 (분석용)

    String[] dayNames = {"", "월", "화", "수", "목", "금", "토", "일"};
    
    if (list != null) {
        for (Reservation r : list) {
            // 날짜별 집계
            String d = r.getDate();
            dateMap.put(d, dateMap.getOrDefault(d, 0) + 1);

            // 시간대별 집계
            String t = r.getTimeSlot();
            timeMap.put(t, timeMap.getOrDefault(t, 0) + 1);
            
            // 요일별 집계
            try {
                if (d != null && !d.isEmpty()) {
                    LocalDate date = LocalDate.parse(d); // 2025-12-04 -> Date 객체
                    int dayIdx = date.getDayOfWeek().getValue(); // 1(월) ~ 7(일)
                    String dayName = dayNames[dayIdx];
                    dayOfWeekMap.put(dayName, dayOfWeekMap.getOrDefault(dayName, 0) + 1);
                }
            } catch (Exception e) { e.printStackTrace(); }
        }
    }

	// 2. 가장 선호하는 요일 찾기 (Max Value 찾기)
    String bestDay = "데이터 없음";
    int maxDayCount = -1;
    for (Map.Entry<String, Integer> entry : dayOfWeekMap.entrySet()) {
        if (entry.getValue() > maxDayCount) {
            maxDayCount = entry.getValue();
            bestDay = entry.getKey();
        }
    }

    // 3. 가장 선호하는 시간대 찾기
    String bestTime = "데이터 없음";
    int maxTimeCount = -1;
    for (Map.Entry<String, Integer> entry : timeMap.entrySet()) {
        if (entry.getValue() > maxTimeCount) {
            maxTimeCount = entry.getValue();
            bestTime = entry.getKey();
        }
    }
    
    // 시간 포맷 변경 (09:00-11:00 -> 09:00~11:00) 보기 좋게
    if (bestTime.contains("-")) {
        bestTime = bestTime.replace("-", "~");
    }

    // 4. 차트용 JSON 문자열 생성 (기존 로직 유지)
    StringBuilder dateLabels = new StringBuilder("[");
    StringBuilder dateData = new StringBuilder("[");
    for (Map.Entry<String, Integer> entry : dateMap.entrySet()) {
        dateLabels.append("'").append(entry.getKey()).append("',");
        dateData.append(entry.getValue()).append(",");
    }
    if (dateMap.size() > 0) { dateLabels.setLength(dateLabels.length()-1); dateData.setLength(dateData.length()-1); }
    dateLabels.append("]");
    dateData.append("]");

    StringBuilder timeLabels = new StringBuilder("[");
    StringBuilder timeData = new StringBuilder("[");
    for (Map.Entry<String, Integer> entry : timeMap.entrySet()) {
        timeLabels.append("'").append(entry.getKey()).append("',");
        timeData.append(entry.getValue()).append(",");
    }
    if (timeMap.size() > 0) { timeLabels.setLength(timeLabels.length()-1); timeData.setLength(timeData.length()-1); }
    timeLabels.append("]");
    timeData.append("]");
%>

<script src="https://cdn.jsdelivr.net/npm/chart.js"></script>

<div class="chart-container">
	<% if (list != null && !list.isEmpty()) { %>
		<div class="chart-summary">
		    가장 선호하는 시간대는 <span class="highlight"><%= bestDay %>요일</span>, 
		    <span class="highlight"><%= bestTime %>시</span>입니다.
		</div>
	<% } %>

	<div class="chart-group">
		<div class="chart-box">
	        <canvas id="dateChart"></canvas>
	    </div>
	    <div class="chart-box">
	        <canvas id="timeChart"></canvas>
	    </div>
	</div>
</div>

<script>
document.addEventListener("DOMContentLoaded", function() {
    // JSP에서 생성한 데이터 받기
    const dateLabels = <%= dateLabels.toString() %>;
    const dateData = <%= dateData.toString() %>;
    const timeLabels = <%= timeLabels.toString() %>;
    const timeData = <%= timeData.toString() %>;

    // 1. 날짜별 차트 (Line)
    const ctxDate = document.getElementById('dateChart').getContext('2d');
    new Chart(ctxDate, {
        type: 'line',
        data: {
            labels: dateLabels,
            datasets: [{
                label: '일별 예약 수',
                data: dateData,
                borderColor: '#3498db',
                backgroundColor: 'rgba(52, 152, 219, 0.1)',
                tension: 0.3,
                fill: true,
                pointRadius: 5,
                pointHoverRadius: 7
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: { display: false },
                title: { display: true, text: '날짜별 예약 추이' }
            },
            scales: {
                y: { beginAtZero: true, ticks: { stepSize: 1 } }
            }
        }
    });

    // 2. 시간대별 차트 (Doughnut)
    const ctxTime = document.getElementById('timeChart').getContext('2d');
    new Chart(ctxTime, {
        type: 'doughnut',
        data: {
            labels: timeLabels,
            datasets: [{
                data: timeData,
                backgroundColor: [
                    '#FF6384', '#36A2EB', '#FFCE56', '#4BC0C0', '#9966FF', '#FF9F40'
                ],
                hoverOffset: 4
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: { position: 'right' },
                title: { display: true, text: '시간대별 선호도' }
            }
        }
    });
});
</script>
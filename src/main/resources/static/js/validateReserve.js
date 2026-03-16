function validateReserve() {
	console.log("검증 함수 시작!");
	
	const successDiv = document.getElementById('success-msg');
	if (successDiv) successDiv.style.display = 'none';

    const serverErrorDiv = document.getElementById('server-error-msg');
    if (serverErrorDiv) serverErrorDiv.style.display = 'none';

	const name = document.getElementById('name').value.trim();
	const studentId = document.getElementById('student_id').value.trim();
	const date = document.getElementById('date').value;
	const timeSlot = document.getElementById('time_slot').value;
	const people = document.getElementById('people').value;
	const purpose = document.getElementById('purpose').value.trim();
	
	clearErrors();
		
	let isValid = true;
	
	// 이름
	if (name === '') {
	    showError('name-error', '이름을 입력해주세요.');
	    isValid = false;
	} else if (name.length < 2) {
	    showError('name-error', '이름은 2자 이상 입력해주세요.');
	    isValid = false;
	}
	
	// 학번 
	if (studentId === '') {
	    showError('student-id-error', '학번을 입력해주세요.');
	    isValid = false;
	} else if (!/^\d{9}$/.test(studentId)) {
	    showError('student-id-error', '학번은 9자리 숫자여야 합니다.');
	    isValid = false;
	}
	
	// 날짜
	if (date === '') {
	    showError('date-error', '날짜를 선택해주세요.');
	    isValid = false;
	} else {
	    const selectedDate = new Date(date);
	    const today = new Date();
	    today.setHours(0, 0, 0, 0);
	    
	    if (selectedDate < today) {
	        showError('date-error', '오늘 이후의 날짜를 선택해주세요.');
	        isValid = false;
	    }
	}
	  
	// 시간
	if (timeSlot === '') {
	    showError('time-slot-error', '시간대를 선택해주세요.');
	    isValid = false;
	}
	
	// 인원 수 
	if (people === '') {
	    showError('people-error', '인원을 입력해주세요.');
	    isValid = false;
	} else if 	(isNaN(people)) {
	    showError('people-error', '인원은 숫자로 입력해야 합니다.');
	    isValid = false;
	} else if (!Number.isInteger(Number(people))) {
	    showError('people-error', '인원은 정수로 입력해야 합니다.');
	    isValid = false;
	} else if (people < 2 || people > 8) {
	    showError('people-error', '인원은 2명에서 8명 사이여야 합니다.');
	    isValid = false;
	} 
	
	// 목적
	if (purpose === '') {
	    showError('purpose-error', '예약 목적을 입력해주세요.');
	    isValid = false;
	} else if (purpose.length < 5) {
	    showError('purpose-error', '예약 목적은 5자 이상 입력해주세요.');
	    isValid = false;
	}
	
	// 검증 통과
	if (isValid) {
	    return confirm('예약을 등록하시겠습니까?');
	}

	return false;
}

function showError(elementId, message) {
    const errorElement = document.getElementById(elementId);
    if (errorElement) {
        errorElement.textContent = message;        
        errorElement.classList.add('show');        
    }
}

function clearErrors() {
    const errorMessages = document.querySelectorAll('.error-message');
    errorMessages.forEach(error => {
        error.textContent = '';
        error.classList.remove('show');
    });
}

window.addEventListener('DOMContentLoaded', function() {
    const dateInput = document.getElementById('date');
    if (dateInput) {
        const today = new Date().toISOString().split('T')[0];
        dateInput.setAttribute('min', today);
    }
});
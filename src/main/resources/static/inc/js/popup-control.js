// 오른쪽 상단 x 버튼 클릭 시 창 닫기
document.querySelectorAll(".del-btn").forEach(value=> {
    value.addEventListener("click", e => {
        if(confirm("이 페이지에서 나가시겠습니까?")) {
            self.close();
        }
    })
});

history.pushState(null, null, location.href);

// 뒤로라기 이벤트감지 -> 현재페이지로 이동
window.onpopstate = function() {
    history.go(1);
}
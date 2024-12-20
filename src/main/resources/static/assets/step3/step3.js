function goFirstStep() {
    if(confirm("처음 화면으로 이동하시겠습니까?\n" +
        "(출제 방법 선택 화면으로 이동)\n" +
        "페이지 이동 시 변경사항이 저장되지 않습니다")) {
        location.replace("/customExam/complete");
    }
}
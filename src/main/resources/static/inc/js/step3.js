function goFirstStep() {
    if(confirm("처음 화면으로 이동하시겠습니까?\n" +
        "(출제 방법 선택 화면으로 이동)\n" +
        "페이지 이동 시 변경사항이 저장되지 않습니다.")) {

        const form = document.createElement("form");
        form.method = "POST";
        form.action = "/customExam/step0";
        document.querySelector("body").appendChild(form);
        form.innerHTML = `<input type="hidden" value="${subjectId}" name="subjectId">`;
        form.submit();
    }
}

function goStep2() {
    const form = document.createElement("form");
    form.method = "POST";
    form.action = "/customExam/step2";
    document.querySelector("body").appendChild(form);
    // document.querySelectorAll(".item-no").forEach(itemNo => {
    //     const no = itemNo.firstChild.textContent;
    //     form.innerHTML += `<input type="hidden" value="${no}" name="questionIds">`;
    // });

    document.querySelectorAll("input[name=questionIds]").forEach( item => {
        form.append(item);
        }
    );

    form.submit();
}
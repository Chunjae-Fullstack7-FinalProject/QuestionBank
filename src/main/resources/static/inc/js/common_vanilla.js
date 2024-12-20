document.addEventListener("DOMContentLoaded", function () {
    // tab
    const tabBtn = document.querySelectorAll(".ui-tab-btn");

    function tabUI(event) {
        const _this = event.target;
        const _cnt = _this.closest(".tab-wrap").querySelectorAll(".contents");
        const _idx = Array.from(tabBtn).indexOf(_this);

        if (_this.closest("div").classList.contains("contents") &&
            !_this.closest("div").classList.contains("content")) {
            if (!_this.classList.contains("active")) {
                Array.from(_this.parentNode.children).forEach(item => item.classList.remove("active"));
                _this.classList.add("active");
                const contents = _this.closest(".contents").querySelectorAll(".content");
                contents.forEach(content => content.classList.remove("on"));
                contents[_idx].classList.add("on");
            }
        } else if (_this.closest("div").classList.contains("content")) {
            if (!_this.classList.contains("active")) {
                Array.from(_this.parentNode.children).forEach(item => item.classList.remove("active"));
                _this.classList.add("active");

                const contentLast = _this.closest(".tab-wrap").querySelectorAll(".content-last");
                contentLast.forEach(content => content.classList.remove("on"));
                contentLast[_idx].classList.add("on");
            }
        } else if (_this.closest("ul").classList.contains("ui-tab-type02")) {
            const _idx = _this.getAttribute("data-idx");

            if (!_this.classList.contains("active")) {
                const siblingBtns = _this.closest("li").querySelectorAll(".ui-tab-btn");
                siblingBtns.forEach(btn => btn.classList.remove("active"));
                Array.from(_this.parentNode.children).forEach(item => item.classList.remove("active"));
                _this.classList.add("active");
                _cnt.forEach(item => item.classList.remove("on"));
                _cnt[_idx].classList.add("on");
            }
        } else {
            if (!_this.classList.contains("active")) {
                Array.from(_this.parentNode.children).forEach(item => item.classList.remove("active"));
                _this.classList.add("active");
                _cnt.forEach(item => item.classList.remove("on"));
                _cnt[_idx].classList.add("on");
            }
        }
    }

    tabBtn.forEach(button => button.addEventListener("click", tabUI));

    // table drag
    const table1 = document.getElementById("table-1");

    if (table1) {
        new Sortable(table1, {
            handle: ".dragHandle",
        });
    }

    // paging
    const pageBtn = document.querySelectorAll(".page");

    function pageFunc(event) {
        const _this = event.target;

        if (!_this.classList.contains("active")) {
            pageBtn.forEach(btn => btn.classList.remove("active"));
            _this.classList.add("active");
        }
    }

    pageBtn.forEach(button => button.addEventListener("click", pageFunc));

    // select
    const selectBtn = document.querySelectorAll(".select-btn");
    const selectCnt = document.querySelectorAll(".open-select-list li a");

    function selectUI(event) {
        const _this = event.target;
        const _cnt = _this.nextElementSibling;

        if (!_this.classList.contains("active")) {
            _this.classList.add("active");
            _cnt.style.display = "block";
        } else {
            _cnt.style.display = "none";
            _this.classList.remove("active");
        }

        if (_this.closest("ul").classList.contains("open-select-list")) {
            selectCnt.forEach(cnt => cnt.classList.remove("active"));
            _this.classList.add("active");
        }
    }

    selectBtn.forEach(button => button.addEventListener("click", selectUI));
    selectCnt.forEach(cnt => cnt.addEventListener("click", selectUI));

    // tooltip hover
    const tipBtns = document.querySelectorAll(".btn-tip");

    tipBtns.forEach(button => {
        button.addEventListener("mouseover", function () {
            const _this = this;
            const _tooltip = _this.nextElementSibling;
            const _tooltipPosition = _this.getBoundingClientRect().top;

            if (_tooltipPosition > 500) {
                _tooltip.style.top = "-112px";
                _tooltip.classList.add("active");
            }
        });
    });

    // 북마크 버튼
    const bookBtn = document.querySelectorAll(".btn-book");

    bookBtn.forEach(button => {
        button.addEventListener("click", function () {
            this.classList.toggle("active");
        });
    });

    // button active
    const _btnMul = document.querySelectorAll(".btn-wrap.multi .btn-line");

    _btnMul.forEach(button => {
        button.addEventListener("click", function () {
            this.classList.toggle("active");
            return false;
        });
    });

    const _btn = document.querySelectorAll(".btn-wrap .btn-line");

    function btnClickFunc(event) {
        const _this = event.target;
        if (!_this.classList.contains("active")) {
            _this.closest(".btn-wrap").querySelectorAll(".btn-line").forEach(item => item.classList.remove("active"));
            _this.classList.add("active");
        }
    }

    _btn.forEach(button => button.addEventListener("click", btnClickFunc));

    // 유사 문항 버튼 active
    const _viewBtn = document.querySelectorAll(".view-que-list .btn-similar-que");

    function viewBtnClickFunc(event) {
        const _this = event.target;
        if (!_this.classList.contains("active")) {
            _this.closest(".view-que-list").querySelectorAll(".btn-similar-que").forEach(item => item.classList.remove("active"));
            _this.classList.add("active");
        }
    }

    _viewBtn.forEach(button => button.addEventListener("click", viewBtnClickFunc));

    // popup
    const _dim = document.querySelector(".dim");
    const _html = document.documentElement;
    const popBtn = document.querySelectorAll(".pop-btn");
    const closePop = document.querySelectorAll(".pop-close, .ui-close");

    function popFunc(event) {
        const _this = event.target;
        const popData = _this.dataset.pop;

        _html.style.overflow = "hidden";
        _dim.style.display = "block";

        const popWrap = document.querySelector(`.pop-wrap[data-pop='${popData}']`);
        if (popWrap) {
            popWrap.style.display = "block";
        }
    }

    function popClose() {
        document.querySelectorAll(".pop-wrap").forEach(pop => pop.style.display = "none");
        _html.style.overflow = "auto";
        _dim.style.display = "none";
    }

    popBtn.forEach(button => button.addEventListener("click", popFunc));
    closePop.forEach(button => button.addEventListener("click", popClose));

    // checkbox
    const chkAll = document.querySelectorAll(".allCheck");
    const chkList = document.querySelectorAll('.left-cnt .chk-acc input[type="checkbox"]');
    const selectList = document.querySelectorAll('.right-cnt .chk-acc input[type="checkbox"]');

    function checkFunc(event) {
        const _this = event.target;

        if (_this.closest("div").classList.contains("sheet-cnt")) {
            const chkNum = _this.dataset.chk;

            if (_this.checked) {
                const checkedItems = document.querySelectorAll(`input[data-chk='${chkNum}']`);
                checkedItems.forEach(item => item.checked = true);
            } else {
                const uncheckedItems = document.querySelectorAll(`input[data-chk='${chkNum}']`);
                uncheckedItems.forEach(item => item.checked = false);
            }
        } else {
            const checkboxes = _this.closest("table").querySelectorAll("input[type=checkbox]");
            checkboxes.forEach(checkbox => checkbox.checked = _this.checked);
        }
    }

    chkAll.forEach(checkbox => checkbox.addEventListener("click", checkFunc));

    // accordion
    const accBtn = document.querySelectorAll(".acc-btn");

    function accFunc(event) {
        const _this = event.target;
        const accWrap = _this.closest(".acc-btn-wrap");

        accWrap.classList.toggle("active");
        const cnt = accWrap.nextElementSibling;
        if (accWrap.classList.contains("active")) {
            cnt.style.display = "block";
        } else {
            cnt.style.display = "none";
        }
    }

    accBtn.forEach(button => button.addEventListener("click", accFunc));

    // swiper (using Swiper.js)
    const swiper = new Swiper(".sheet-swiper .swiper-container", {
        slidesPerView: 3,
        spaceBetween: 0,
        navigation: {
            nextEl: ".sheet-swiper .swiper-button-next",
            prevEl: ".sheet-swiper .swiper-button-prev",
        },
    });

    // 형제 연결
    const cntList = document.querySelectorAll(".cnt-list span");

    cntList.forEach(span => {
        span.addEventListener("click", function () {
            const _this = this;
            const _txt = _this.textContent;

            const targetWrap = _this.closest("div").classList.contains("box")
                ? _this.closest(".box").querySelector(".select-wrap .scroll-inner")
                : _this.closest(".col").querySelector(".select-wrap .scroll-inner");

            const newSelectStu = document.createElement("span");
            newSelectStu.classList.add("select-stu");
            newSelectStu.innerHTML = `${_txt}<button type="button" class="del"></button>`;

            targetWrap.appendChild(newSelectStu);
        });
    });

    document.addEventListener("click", function (event) {
        if (event.target && event.target.matches(".select-stu .del")) {
            event.target.closest(".select-stu").remove();
        }
    });

    // tablist 글자 숨김
    const cntBtn = document.querySelectorAll(".cnt-btn");

    cntBtn.forEach(button => {
        button.addEventListener("click", function () {
            const hiddenCnt = this.closest(".hidden-cnt");
            hiddenCnt.classList.toggle("on");
        });
    });

    // btn img
    const btnIcon = document.querySelectorAll(".btn-icon.type02");

    btnIcon.forEach(button => {
        button.addEventListener("mouseover", function () {
            this.querySelector("i").classList.add("hover");
        });
        button.addEventListener("mouseleave", function () {
            this.querySelector("i").classList.remove("hover");
        });
    });
});

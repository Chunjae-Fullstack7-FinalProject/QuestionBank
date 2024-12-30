// 개별 svg -> png
async function convertSvgToPng(svgUrl) {
    return new Promise((resolve, reject) => {
        const img = new Image();
        img.src = svgUrl;

        img.onload = () => {
            const canvas = document.createElement('canvas');
            canvas.width = img.width;
            canvas.height = img.height;
            const ctx = canvas.getContext('2d');
            ctx.drawImage(img, 0, 0);
            resolve(canvas.toDataURL('image/png'));
        };

        img.onerror = (error) => {
            console.error('SVG를 PNG로 변환하는 중 오류 발생:', error);
            reject(error);
        };
    });
}



//사용할 이미지 png로 변환
async function replaceSvgWithPng(imageClassName) {
    const svgImages = document.querySelectorAll(imageClassName);

    for (const svgImg of svgImages) {
        const svgUrl = "/api/customExam/proxy?url=" + svgImg.src; // cors 문제 피하기 위해 서버 우회
        try {
            const pngDataUrl = await convertSvgToPng(svgUrl);
            svgImg.src = pngDataUrl; // png로 교체
        } catch (error) {
            console.error(`SVG 변환 실패: ${svgUrl}`, error);
        }
    }

}

async function replaceSvgWithPng2(element) {
    const svgImages = element.querySelectorAll("img");

    for (const svgImg of svgImages) {
        const ext = svgImg.src.substring(svgImg.src.lastIndexOf(".") + 1);
        const svgUrl = `/api/customExam/proxy/${ext}?url=` + svgImg.src; // cors 문제 피하기 위해 서버 우회
        try {
            svgImg.src = await convertSvgToPng(svgUrl); // png로 교체
        } catch (error) {
            console.error(`SVG 변환 실패: ${svgUrl}`, error);
        }
    }
}

async function savePdf1(title, item) {

    // await replaceSvgWithPng('.pdf-img');

    const element = document.getElementById('pdf-content'); // 변환할 요소
    const options = {
        margin: 15, // 여백 설정
        image: { type: 'jpeg', quality: 0.98 }, // 이미지 설정
        html2canvas: { scale: 2, useCORS: true }, // 해상도 설정
        jsPDF: { unit: 'mm', format: 'a4', orientation: 'portrait' }, // PDF 설정
        pagebreak: { mode: ['css'] },
    };

    // for (const item of itemclass) {
        const filename = `${title}-${item}.pdf`; // 파일 이름 설정
        options.filename = filename;

        console.log(`Generating PDF for: ${item}`);

        // PDF 생성 후 저장
        html2pdf().set(options).from(element).save();

    // }

}

//html 시험지 레이아웃
function page(title, item) {
    const include = document.getElementById(item);
    const items = include.querySelectorAll(".pdf-item");
    const content = document.getElementById("pdf-content");


    replaceSvgWithPng2(include).then(element => {
        content.innerHTML = `
			<div class="page">
                <table class="pdf-title-table">
                    <tr>
                        <td rowspan="2" class="pdf-td-title">${title}</td>
                        <td class="pdf-td-grade"> &nbsp;&nbsp;&nbsp; 학년 &nbsp;&nbsp;반 &nbsp;&nbsp;번</td>
                    </tr>
                    <tr>
                        <td class="pdf-td-name">이름:</td>
                    </tr>
                </table>
                <div class="pdf-container" style="height: 235mm">
                    <div class="pdf-item-container pdf-item-container-left" style="height: 235mm" id="left-1"></div>
                    <div class="pdf-item-container pdf-item-container-right" style="height: 235mm" id="right-1"></div>
                </div>
                <div class="page-no">1</div>
            </div>
			`;

        const toPx = document.querySelector(".pdf-title-table").offsetWidth / 180.0;

        console.log(toPx);

        let currentHeight = 5*toPx;
        let gridArea = document.getElementById("left-1");
        let pageNo = 1;
        let maxHeight = 235 * toPx;
        let currentArea = 0;

        items.forEach(value => {
            value.removeAttribute("style");
            const height = value.offsetHeight;

            if(value.innerHTML.includes("<table")) {
                value.querySelectorAll("table").forEach(table => {
                    table.removeAttribute("style");
                    table.classList.add("pdf-item-table");
                })
            }

            if(value.innerHTML.includes("<table") && height > maxHeight-currentHeight && maxHeight-currentHeight > 30*toPx) {
                let divElement = document.createElement("div");
                divElement.classList.add("pdf-item");

                const passageNo = value.querySelector(".pdf-passage-no");
                const question_main = value.querySelector(".pdf-item-question-main");

                if(passageNo != null && passageNo.offsetHeight <= maxHeight-currentHeight) {
                    divElement.appendChild(passageNo);
                }
                if(question_main != null && question_main.offsetHeight <= maxHeight-currentHeight) {
                    divElement.appendChild(question_main);
                }

                gridArea.appendChild(divElement);

                currentHeight += divElement.offsetHeight;

                divElement = document.createElement("div");
                divElement.classList.add("pdf-item");

                let table = document.createElement("table");
                table.classList.add("pdf-item-table");
                divElement.appendChild(table);
                let tr = document.createElement("tr");
                let td = document.createElement("td");
                table.appendChild(tr);
                tr.appendChild(td);

                gridArea.appendChild(divElement);

                value.querySelectorAll(".pdf-line").forEach(value1 => {
                    if(value1.innerHTML.includes("<table")){
                        return;
                    }
                    if (value1.offsetHeight + (5*toPx) + currentHeight + divElement.offsetHeight < maxHeight) {

                        td.appendChild(value1);
                    }
                    else {
                        if(td.childNodes.length === 0) {
                            gridArea.removeChild(divElement);
                        }
                        currentHeight = 5*toPx;
                        if (currentArea === 1) {
                            maxHeight = 255 * toPx;
                            pageNo = pageNo + 1;
                            currentArea = 0;
                            content.innerHTML += `
                            <div class="page">
                                <div class="pdf-container">
                                    <div class="pdf-item-container pdf-item-container-left" id="left-${pageNo}"></div>
                                    <div class="pdf-item-container pdf-item-container-right" id="right-${pageNo}"></div>
                                </div>
                                <div class="page-no">${pageNo}</div>
                            </div>
                    `;

                            gridArea = document.getElementById("left-" + pageNo);
                        } else {
                            currentArea = 1;
                            gridArea = document.getElementById("right-" + pageNo);
                        }

                        currentHeight = 5*toPx;
                        divElement = document.createElement("div");
                        divElement.classList.add("pdf-item");
                        table = document.createElement("table");
                        table.classList.add("pdf-item-table");
                        divElement.appendChild(table);
                        tr = document.createElement("tr");
                        td = document.createElement("td");
                        table.appendChild(tr);
                        tr.appendChild(td);

                        gridArea.appendChild(divElement);

                        td.appendChild(value1);
                    }
                });
                currentHeight += divElement.offsetHeight + 5 * toPx;
                value.remove();
                return;
            }

            if (maxHeight-currentHeight < height) {
                currentHeight = 5 * toPx;
                if (currentArea === 1) {
                    maxHeight = 255 * toPx;
                    pageNo = pageNo + 1;
                    currentArea = 0;
                    content.innerHTML += `
                            <div class="page">
                                <div class="pdf-container">
                                    <div class="pdf-item-container pdf-item-container-left" id="left-${pageNo}"></div>
                                    <div class="pdf-item-container pdf-item-container-right" id="right-${pageNo}"></div>
                                </div>
                                <div class="page-no">${pageNo}</div>
                            </div>
                    `;

                    gridArea = document.getElementById("left-" + pageNo);
                } else {
                    currentArea = 1;
                    gridArea = document.getElementById("right-" + pageNo);
                }
            }
            currentHeight = currentHeight + height + 5*toPx ;
            gridArea.appendChild(value);
        });
    });


}

//이미지 시험지 레이아웃
function pagenation(itemclass, title) {
    const items = document.querySelectorAll(".pdf-item-"+itemclass);
    const content = document.getElementById("pdf-content");

    content.innerHTML = `
			<div class="page">
                <table class="pdf-title-table">
                    <tr>
                        <td rowspan="2" class="pdf-td-title">${title}</td>
                        <td class="pdf-td-grade"> &nbsp;&nbsp;&nbsp; 학년 &nbsp;&nbsp;반 &nbsp;&nbsp;번</td>
                    </tr>
                    <tr>
                        <td class="pdf-td-name">이름:</td>
                    </tr>
                </table>
                <div class="pdf-container" style="height: 240mm">
                    <div class="pdf-item-container pdf-item-container-left" style="height: 240mm" id="left-1"></div>
                    <div class="pdf-item-container pdf-item-container-right" style="height: 240mm" id="right-1"></div>
                </div>
                <div class="page-no">1</div>
            </div>
			`;

    const toPx = document.querySelector(".pdf-title-table").offsetWidth / 180.0;

    let currentHeight = 15*toPx;
    let gridArea = document.getElementById("left-1");
    let page = 1;
    let maxHeight = 240 * toPx;
    let currentArea = 0;

    items.forEach(value => {
        const height = value.offsetHeight;

        if (currentHeight + height > maxHeight) {
            if (currentArea === 1) {
                maxHeight = 255 * toPx;
                page = page + 1;
                currentArea = 0;
                currentHeight = 0;
                content.innerHTML += `
                            <div class="page">
                                <div class="pdf-container">
                                    <div class="pdf-item-container pdf-item-container-left" id="left-${page}"></div>
                                    <div class="pdf-item-container pdf-item-container-right" id="right-${page}"></div>
                                </div>
                                <div class="page-no">${page}</div>
                            </div>
            `
                gridArea = document.getElementById("left-" + page);
            } else {
                currentArea = 1;
                currentHeight = 0;
                gridArea = document.getElementById("right-" + page);
            }
        }
        currentHeight = currentHeight + height + 5 * toPx;
        gridArea.appendChild(value);
    });

}
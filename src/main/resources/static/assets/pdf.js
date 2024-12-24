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

async function savePdf1(title, itemclass) {

    await replaceSvgWithPng('.pdf-img');

    const element = document.getElementById('pdf-content'); // 변환할 요소
    const options = {
        margin: 15, // 여백 설정
        image: { type: 'jpeg', quality: 0.98 }, // 이미지 설정
        html2canvas: { scale: 2, useCORS: true }, // 해상도 설정
        jsPDF: { unit: 'mm', format: 'a4', orientation: 'portrait' }, // PDF 설정
        pagebreak: { mode: ['css'] },
    };

    for (const item of itemclass) {
        pagenation(item, title);
        const filename = `${item}-[[${pdfFileId}]].pdf`; // 파일 이름 설정
        options.filename = filename;

        console.log(`Generating PDF for: ${item}`);

        // PDF 생성 후 저장
        html2pdf().set(options).from(element).save();

    }

}

//시험지 레이아웃 맞추기
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
                <div class="pdf-container" style="height: 235mm">
                    <div class="pdf-item-container pdf-item-container-left" style="height: 235mm" id="left-1"></div>
                    <div class="pdf-item-container pdf-item-container-right" style="height: 235mm" id="right-1"></div>
                </div>
                <div class="page-no">1</div>
            </div>
			`;

    const toPx = document.querySelector(".pdf-title-table").offsetWidth / 180.0;

    let currentHeight = 15;
    let gridArea = document.getElementById("left-1");
    let page = 1;
    let maxHeight = 235 * toPx;
    let currentArea = 0;

    items.forEach(value => {
        const height = value.offsetHeight;

        if (currentHeight + height > maxHeight) {
            if (currentArea === 1) {
                maxHeight = 255 * toPx;
                page = page + 1;
                currentArea = 0;
                currentHeight = 19;
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
        currentHeight = currentHeight + height + 10 * toPx;
        gridArea.appendChild(value);
    });

}
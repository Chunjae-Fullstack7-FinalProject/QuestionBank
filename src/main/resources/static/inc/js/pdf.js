// window.onload = replaceSvgWithPng;


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
async function replaceSvgWithPng() {
    const svgImages = document.querySelectorAll('.pdf-img');

    for (const svgImg of svgImages) {
        const svgUrl = "/api/customExam/proxy?url=" + svgImg.src; // cors 문제 피하기 위해 서버로 우회
        try {
            const pngDataUrl = await convertSvgToPng(svgUrl);
            svgImg.src = pngDataUrl; // png로 교체
        } catch (error) {
            console.error(`SVG 변환 실패: ${svgUrl}`, error);
        }
    }
}

//시험지 레이아웃 맞추기
function pagenation() {
    const items = document.querySelectorAll(".pdf-item");
    const content = document.getElementById("pdf-content");

    const toPx = document.querySelector(".pdf-title-table").offsetWidth / 180.0;

    let currentHeight = 15;
    let gridArea = document.getElementById("left-1");
    let page = 1;
    let maxHeight = 230 * toPx;
    let currentArea = 0;

    items.forEach(value => {
        const height = value.offsetHeight;

        if (currentHeight + height > maxHeight) {
            if (currentArea === 1) {
                maxHeight = 250 * toPx;
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
        currentHeight = currentHeight + height + 19;
        gridArea.appendChild(value);
    });
}


function savePdf() {
    // PDF 변환 및 다운로드 기능

    const element = document.getElementById('pdf-content'); // 변환할 요소
    const options = {
        margin: 15, // 여백 설정
        filename: 'test.pdf', // 파일 이름
        image: {type: 'jpeg', quality: 0.98}, // 이미지 설정
        html2canvas: {scale: 2, useCORS: true}, // 해상도 설정
        jsPDF: {unit: 'mm', format: 'a4', orientation: 'portrait'}, // PDF 설정
        pagebreak: {mode: ['css']}
    };

    html2pdf().set(options).from(element).outputPdf('blob')
        .then((pdfBlob) => {
            sendPdf(pdfBlob);
        });
}

//서버에 변환된 pdf 파일 전송
async function sendPdf(pdf) {
    const formData = new FormData();
    formData.append('file', pdf, 'test.pdf');

    try {
        const response = await fetch('/api/customExam/pdf', {
            method: 'POST',
            body: formData,
        });

        if (!response.ok) {
            throw new Error('서버 전송 실패');
        }

        // 파일 저장 성공했을 때 어떻게 할지

    } catch (error) {
        console.error('PDF 전송 중 오류 발생:', error);
        alert('PDF 전송 중 오류 발생');
    }
}

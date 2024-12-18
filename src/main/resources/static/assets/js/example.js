async function getList() {
    try {
        // API에서 문제 데이터 가져오기
        const response = await fetch(`${API_BASE_URL}/item/item-list`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                itemIdList: [966536, 1588525, 1588526, 1589103, 1589104]
            })
        });

        const data = await response.json();

        if (data.successYn === 'Y') {
            const questions = data.itemList;
            console.log(questions);
        } else {
            console.error('Failed to fetch questions:', data);
        }
    } catch (error) {
        console.error('Error fetching questions:', error);
    }
}
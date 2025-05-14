document.addEventListener('DOMContentLoaded', function() {
    const submitBtn = document.getElementById('submitBtn');
    const checkStatusBtn = document.getElementById('checkStatusBtn');
    const statusSection = document.getElementById('statusSection');
    const resultDiv = document.getElementById('result');
    const statusDisplay = document.getElementById('statusDisplay');
    const progressContainer = document.querySelector('.progress-container');
    const progressBar = document.getElementById('progressBar');
    const progressPercentage = document.getElementById('progressPercentage');
    const managerUrl = 'http://localhost:8080/api/hash';
    let checkInterval = null;

    submitBtn.addEventListener('click', async function() {
        const hash = document.getElementById('hash').value.trim();
        const maxLength = parseInt(document.getElementById('maxLength').value, 10);
        if (!hash) { alert('Please enter a hash'); return; }

        // Reset UI
        resetUI();
        if (checkInterval) clearInterval(checkInterval);

        try {
            const response = await fetch(`${managerUrl}/crack`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ hash, maxLength })
            });

            if (!response.ok) {
                throw new Error(await response.text());
            }

            const { requestId } = await response.json();
            document.getElementById('requestId').value = requestId;
            statusSection.style.display = 'block';
            progressContainer.style.display = 'block';

            startAutoStatusCheck(requestId);
        } catch (err) {
            console.error(err);
            showError(err.message);
        }
    });

    function resetUI() {
        resultDiv.textContent = '';
        statusDisplay.textContent = 'Status: ';
        progressBar.style.width = '0%';
        progressPercentage.textContent = '0%';
        progressContainer.style.display = 'none';
    }

    function showError(message) {
        resultDiv.textContent = `Error: ${message}`;
        resultDiv.style.color = 'red';
    }

    function showSuccess(message) {
        resultDiv.textContent = message;
        resultDiv.style.color = 'green';
    }

    async function checkStatus(requestId) {
        try {
            const res = await fetch(`${managerUrl}/status?requestId=${requestId}&t=${Date.now()}`, {
                headers: {
                    'Cache-Control': 'no-cache',
                    'Accept': 'application/json'
                }
            });

            if (!res.ok) {
                throw new Error(await res.text());
            }

            return await res.json();
        } catch (err) {
            console.error(err);
            throw err;
        }
    }

    function startAutoStatusCheck(requestId) {
        if (checkInterval) clearInterval(checkInterval);

        checkInterval = setInterval(async () => {
            try {
                const data = await checkStatus(requestId);
                updateUI(data, requestId);

                if (data.status === 'READY' || data.status === 'ERROR') {
                    clearInterval(checkInterval);
                    finalizeStatus(data);
                }
            } catch (err) {
                clearInterval(checkInterval);
                showError(err.message.includes('Unexpected token')
                    ? 'Invalid response from server'
                    : err.message);
            }
        }, 2000);
    }

    function updateUI(data, requestId) {
        const { status, data: answers, responsesCount, totalWorkers } = data;
        const percent = Math.round((responsesCount / totalWorkers) * 100);

        progressBar.style.width = percent + '%';
        progressPercentage.textContent = `${percent}%`;
        statusDisplay.textContent = `Status: ${status}`;

        // Update request ID in case it was restored
        document.getElementById('requestId').value = requestId;
    }

    function finalizeStatus(data) {
        progressBar.style.width = '100%';
        progressPercentage.textContent = '100%';

        if (data.status === 'READY') {
            const result = data.data.length > 0
                ? `Found: ${data.data.join(', ')}`
                : 'No matches found';
            showSuccess(result);
        } else {
            showError('Error processing request');
        }
    }

    checkStatusBtn.addEventListener('click', async () => {
        const requestId = document.getElementById('requestId').value.trim();
        if (!requestId) return alert('Please enter a request ID');

        resetUI();
        progressContainer.style.display = 'block';

        try {
            const data = await checkStatus(requestId);
            updateUI(data, requestId);

            if (data.status !== 'IN_PROGRESS') {
                finalizeStatus(data);
            } else {
                startAutoStatusCheck(requestId);
            }
        } catch (err) {
            showError(err.message.includes('Unexpected token')
                ? 'Invalid response from server'
                : err.message);
        }
    });
});

/*document.addEventListener('DOMContentLoaded', function() {
    const submitBtn = document.getElementById('submitBtn');
    const checkStatusBtn = document.getElementById('checkStatusBtn');
    const statusSection = document.getElementById('statusSection');
    const resultDiv = document.getElementById('result');
    const statusDisplay = document.getElementById('statusDisplay');
    const progressContainer = document.querySelector('.progress-container');
    const progressBar = document.getElementById('progressBar');
    const progressPercentage = document.getElementById('progressPercentage');
    const managerUrl = 'http://localhost:8080/api/hash';
    let checkInterval = null;

    submitBtn.addEventListener('click', async function() {
        const hash = document.getElementById('hash').value.trim();
        const maxLength = parseInt(document.getElementById('maxLength').value, 10);
        if (!hash) { alert('Please enter a hash'); return; }

        // Reset UI
        resultDiv.textContent = '';
        statusDisplay.textContent = 'Status: ';
        progressBar.style.width = '0%';
        progressPercentage.textContent = '0%';
        progressContainer.style.display = 'none';
        if (checkInterval) clearInterval(checkInterval);

        try {
            const response = await fetch(`${managerUrl}/crack`, {
                method: 'POST', headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ hash, maxLength })
            });
            const { requestId } = await response.json();
            document.getElementById('requestId').value = requestId;
            statusSection.style.display = 'block';
            progressContainer.style.display = 'block';

            startAutoStatusCheck(requestId);
        } catch (err) {
            console.error(err);
            resultDiv.textContent = `Error: ${err.message}`;
        }
    });

    function startAutoStatusCheck(requestId) {
        if (checkInterval) clearInterval(checkInterval);
        checkInterval = setInterval(async () => {
            try {
                const res = await fetch(`${managerUrl}/status?requestId=${requestId}&t=${Date.now()}`, {
                    headers: { 'Cache-Control': 'no-cache' }
                });
                const data = await res.json();

                const { status, data: answers, responsesCount, totalWorkers } = data;
                const percent = Math.round((responsesCount / totalWorkers) * 100);
                progressBar.style.width = percent + '%';
                progressPercentage.textContent = `${percent}%`;

                statusDisplay.textContent = `Status: ${status}`;

                if (status === 'READY' || status === 'ERROR') {
                    clearInterval(checkInterval);
                    if (status === 'READY') {
                        resultDiv.textContent = answers.length > 0
                            ? `Found: ${answers.join(', ')}`
                            : 'No matches found';
                    } else {
                        resultDiv.textContent = 'Error processing request';
                    }
                    // ensure final bar is 100%
                    progressBar.style.width = '100%';
                    progressPercentage.textContent = '100%';
                }
            } catch (err) {
                console.error(err);
                clearInterval(checkInterval);
                resultDiv.textContent = `Error: ${err.message}`;
            }
        }, 2000);
    }

    checkStatusBtn.addEventListener('click', () => {
        const requestId = document.getElementById('requestId').value;
        if (!requestId) return alert('No request ID');
        startAutoStatusCheck(requestId);
    });
}); */

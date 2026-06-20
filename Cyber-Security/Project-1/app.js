// Grab the input field
const passwordInput = document.getElementById('livePassword');
const attackerLogs = document.getElementById('attackerLogs');
const ramBlocks = document.getElementById('ramBlocks');

// Function to add lines to the attacker terminal
function logAttack(message, isCritical = false) {
    const li = document.createElement('li');
    li.textContent = `> ${message}`;
    if (isCritical) li.className = 'log-red';
    attackerLogs.prepend(li);
}

// LISTEN FOR EVERY KEYSTROKE
passwordInput.addEventListener('input', (e) => {
    const pwd = e.target.value;
    
    // 1. UPDATE RAM LIVE
    ramBlocks.innerHTML = '';
    for(let i = 0; i < pwd.length; i++) {
        const div = document.createElement('div');
        div.className = 'ram-block';
        div.textContent = pwd.charCodeAt(i).toString(16).toUpperCase(); // Show Hex data
        ramBlocks.appendChild(div);
    }

    if (pwd.length === 0) {
        document.getElementById('gatekeeper').textContent = "STATUS: WAITING FOR KEYSTROKES";
        document.getElementById('gatekeeper').className = "gatekeeper-status";
        return;
    }

    // 2. CALCULATE LIVE ENTROPY (The math from Page 7)
    let poolSize = 0;
    if (/[a-z]/.test(pwd)) poolSize += 26;
    if (/[A-Z]/.test(pwd)) poolSize += 26;
    if (/[0-9]/.test(pwd)) poolSize += 10;
    if (/[^a-zA-Z0-9]/.test(pwd)) poolSize += 32;

    const combinations = Math.pow(poolSize, pwd.length);
    document.getElementById('entropyValue').textContent = poolSize > 0 ? `${poolSize}^${pwd.length}` : '0';

    // 3. THE ATTACKER SCRIPT LOGIC
    let crackTime = "";
    
    if (pwd.length < 8) {
        // The Zero Point Rule from the PDF
        crackTime = "INSTANT (< 8 chars)";
        document.getElementById('crackTime').textContent = crackTime;
        document.getElementById('crackTime').className = "critical";
        
        document.getElementById('gatekeeper').textContent = "GATEKEEPER FAIL: ZERO POINT VIOLATION";
        document.getElementById('gatekeeper').className = "gatekeeper-status critical";
        
        logAttack(`Bruteforcing length ${pwd.length}... Cracked instantly.`, true);
    } 
    else if (poolSize < 60) {
        // Has length, but lacks complexity (Only lowercase + numbers)
        crackTime = "Minutes / Hours";
        document.getElementById('crackTime').textContent = crackTime;
        document.getElementById('crackTime').className = "critical";
        
        document.getElementById('gatekeeper').textContent = "GATEKEEPER FAIL: LACKS COMPLEXITY";
        document.getElementById('gatekeeper').className = "gatekeeper-status critical";
        
        logAttack(`Pattern weak. Dictionary attack successful.`, true);
    } 
    else {
        // Strong password
        crackTime = "Centuries";
        document.getElementById('crackTime').textContent = crackTime;
        document.getElementById('crackTime').className = "safe";
        
        document.getElementById('gatekeeper').textContent = "GATEKEEPER PASS: SECURE ENTROPY";
        document.getElementById('gatekeeper').className = "gatekeeper-status safe";
        
        logAttack(`Exponential brute force required. Script stalled...`);
    }
    
    // Simulate a Timing Attack if they type a specific word
    if (pwd === "admin") {
         logAttack(`TIMING ATTACK VULNERABILITY FOUND: Dictionary match 'admin'`, true);
    }
});
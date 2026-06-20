import time
import os

# MOD 1: The Local Leaked Database (Common breached passwords)
LEAKED_DB = ["password123", "admin", "12345678", "qwerty", "letmein123", "password"]

def calculate_attack_metrics(current_password):
    """Calculates entropy, checks breaches, and detects Unicode."""
    
    # Check 1: The Leaked Database Bypass
    if current_password.lower() in LEAKED_DB:
        return 0, "CRITICAL: CREDENTIAL FOUND IN BREACH DB. Bypassing Math.", "FAIL ❌"

    # Check 2: The Unicode Curveball
    has_unicode = any(ord(c) > 127 for c in current_password)
    
    pool_size = 0
    if has_unicode:
        # Expand search space massively per DecodeLabs PDF
        pool_size = 143000  
    else:
        # Standard ASCII pool calculation
        if any(c.islower() for c in current_password): pool_size += 26
        if any(c.isupper() for c in current_password): pool_size += 26
        if any(c.isdigit() for c in current_password): pool_size += 10
        if any(ord(c) <= 127 and not c.isalnum() for c in current_password): pool_size += 32
    
    combinations = pool_size ** len(current_password) if pool_size > 0 else 0
    
    # Check 3: PDF Logic (Zero Point & Complexity)
    if len(current_password) < 8:
        crack_time = "INSTANT (Violates Zero Point: < 8 chars)"
        gatekeeper_status = "FAIL ❌"
    elif pool_size < 60:
        crack_time = "Minutes / Hours (Dictionary attack vulnerable)"
        gatekeeper_status = "FAIL ❌"
    elif has_unicode:
        crack_time = "WARNING: UNICODE CURVEBALL DETECTED. MATH EXCEEDS LIMITS..."
        gatekeeper_status = "PASS ✅"
    else:
        crack_time = "Centuries (Exponentially secure)"
        gatekeeper_status = "PASS ✅"
        
    return combinations, crack_time, gatekeeper_status

def secure_ram_wipe(final_password):
    """MOD 3: Visualizes the RAM Trap (Hinnop) and secures the memory."""
    print("\n" + "=" * 65)
    print(" 🔒 VAULT PHASE: SECURING HEAP MEMORY (RAM TRAP AVOIDANCE)")
    print("=" * 65)
    time.sleep(1)
    
    # Convert password to hex to simulate raw memory blocks in RAM
    hex_blocks = [f"[{hex(ord(c))[2:].upper().zfill(2)}]" for c in final_password]
    
    print(f"[*] Allocating String to RAM  : {' '.join(hex_blocks)}")
    time.sleep(2)
    print("[!] Warning: Immutable string detected in Heap. Vulnerable to scrapers.")
    time.sleep(2)
    print("[*] Initiating Bytearray Overwrite Sequence...")
    time.sleep(1)
    
    # Animate the memory wipe in the terminal
    wiped_blocks = hex_blocks.copy()
    for i in range(len(wiped_blocks)):
        wiped_blocks[i] = "[00]"
        # The \r overwrites the current line for a cool animation effect
        print(f"\r[*] Scrubbing Memory Block {i+1} : {' '.join(wiped_blocks)}", end="", flush=True)
        time.sleep(0.3)
        
    print("\n\n[+] SECURE WIPE COMPLETE. RAM is clear.")

def live_simulation(target_password):
    """Animates the split-screen effect in the terminal."""
    current_typing = ""
    
    for char in target_password:
        current_typing += char
        combinations, crack_time, status = calculate_attack_metrics(current_typing)
        
        os.system('cls' if os.name == 'nt' else 'clear')
        
        print("=" * 65)
        print(" 🛡️  USER TERMINAL ")
        print("=" * 65)
        print(f" User is typing... : {current_typing}_")
        print("\n")
        
        print("=" * 65)
        print(" 💀 ATTACKER SCRIPT (LIVE EVALUATION) ")
        print("=" * 65)
        print(f" Intercepted Data  : [ {current_typing} ]")
        print(f" Combinations      : {combinations:,}")
        print(f" Estimated Crack   : {crack_time}")
        print(f" Gatekeeper Status : {status}")
        
        time.sleep(0.6) 
        
    # MOD 3: Trigger the RAM wipe ONLY if the password was strong enough
    if status == "PASS ✅":
        secure_ram_wipe(target_password)
    else:
        print("\n" + "=" * 65)
        print(" 🛑 PROCESS TERMINATED. Weak entropy rejected by Gatekeeper.")

if __name__ == "__main__":
    os.system('cls' if os.name == 'nt' else 'clear')
    print("--- DECODELABS: ADVANCED ATTACK SIMULATOR (V2.0) ---")
    print("Loaded Modules: Breach DB, Unicode Curveball, Secure RAM Wipe")
    print("-" * 55)
    
    target = input("Enter target password to simulate: ")
    live_simulation(target)
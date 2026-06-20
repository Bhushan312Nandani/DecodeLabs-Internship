import time
import os

# ==========================================
# 1. CORE CRYPTO ENGINE
# ==========================================
def generate_keypair(p, q):
    n = p * q
    phi = (p - 1) * (q - 1)
    e = 17 
    d = 0
    for i in range(1, phi):
        if (e * i) % phi == 1:
            d = i
            break
    return ((e, n), (d, n))

def rsa_process(key_tuple, text_or_data):
    key, n = key_tuple
    if isinstance(text_or_data, str):
        return [pow(ord(char), key, n) for char in text_or_data]
    else:
        return ''.join([chr(pow(char, key, n)) for char in text_or_data])

# ==========================================
# 2. THE MITM ATTACK SIMULATION
# ==========================================
if __name__ == "__main__":
    os.system('cls' if os.name == 'nt' else 'clear')
    print("=" * 65)
    print(" DECODELABS: MAN-IN-THE-MIDDLE (MITM) EXPLOIT SIMULATOR ")
    print("=" * 65)
    
    # 1. Network setup
    print("\n[*] SETTING UP NETWORK ACTORS...")
    alice_pub, alice_priv = generate_keypair(61, 53)
    bob_pub, bob_priv = generate_keypair(59, 47)
    eve_pub, eve_priv = generate_keypair(43, 41) # Eve generates her own keys
    time.sleep(1)
    
    # 2. The Interception
    print("\n--- 🕵️ THE INTERCEPTION PHASE (Eve's Wi-Fi Router) ---")
    print("[*] Bob broadcasts his Public Key: ", bob_pub)
    print("[!] EVE INTERCEPTS IT. Bob's key is held by Eve.")
    print("[!] EVE SENDS HER OWN PUBLIC KEY TO ALICE INSTEAD: ", eve_pub)
    time.sleep(2)
    
    # 3. Alice Encrypts
    print("\n--- 🔒 ALICE'S DEVICE ---")
    message = input("[Alice] Enter a highly secret message for Bob: ")
    print("[*] Alice encrypts payload using what she thinks is Bob's Key...")
    
    # ALICE USES EVE'S KEY BY MISTAKE!
    intercepted_payload = rsa_process(eve_pub, message) 
    print(f"[+] CIPHERTEXT SENT: {intercepted_payload}")
    time.sleep(2)

    # 4. Eve Hacks the Payload
    print("\n" + "=" * 65)
    print(" 🚨 CRITICAL BREACH: EVE'S TERMINAL ")
    print("=" * 65)
    print("[*] Eve intercepts the transmission.")
    print("[*] Decrypting with EVE'S PRIVATE KEY...")
    
    # EVE CAN READ IT BECAUSE ALICE USED EVE'S PUBLIC KEY!
    stolen_message = rsa_process(eve_priv, intercepted_payload)
    print(f"\n[💀] STOLEN DATA ACQUIRED : '{stolen_message}'")
    
    # Eve can even alter the message!
    altered_message = stolen_message + " (P.S. Transfer $500 to Eve)"
    print(f"[💀] ALTERING PAYLOAD   : '{altered_message}'")
    print("\n[*] Re-encrypting altered payload with Bob's REAL Public Key...")
    
    # EVE SENDS IT TO BOB USING BOB'S REAL KEY
    malicious_payload = rsa_process(bob_pub, altered_message)
    print("=" * 65 + "\n")
    time.sleep(3)

    # 5. Bob Receives the Hacked Payload
    print("--- 🔓 BOB'S DEVICE ---")
    print("[*] Bob receives data. Decrypting with BOB'S PRIVATE KEY...")
    final_message = rsa_process(bob_priv, malicious_payload)
    
    print(f"[+] MESSAGE RECEIVED FROM 'ALICE': {final_message}")
    print("\n[!] Bob has no idea the message was read and altered by Eve!")
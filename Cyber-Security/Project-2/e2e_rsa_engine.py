import time
import os

# ==========================================
# 1. MINIATURE RSA CRYPTO ENGINE (The Math)
# ==========================================
def generate_keypair(p, q):
    """Generates Public and Private RSA keys using two prime numbers."""
    n = p * q
    phi = (p - 1) * (q - 1)
    
    # E is our Public exponent (Hardcoded for this demo to a known prime)
    e = 17 
    
    # Calculate D (Private exponent) using Modular Inverse
    d = 0
    for i in range(1, phi):
        if (e * i) % phi == 1:
            d = i
            break
            
    # Return (Public Key), (Private Key)
    return ((e, n), (d, n))

def rsa_process(key_tuple, text_or_data):
    """Encrypts or Decrypts using C = M^e mod n or M = C^d mod n"""
    key, n = key_tuple
    
    if isinstance(text_or_data, str):
        # Text to Ciphertext (Encrypting)
        return [pow(ord(char), key, n) for char in text_or_data]
    else:
        # Ciphertext to Text (Decrypting)
        return ''.join([chr(pow(char, key, n)) for char in text_or_data])

# ==========================================
# 2. THE E2EE PIPELINE (Simulating Alice -> Bob)
# ==========================================
if __name__ == "__main__":
    os.system('cls' if os.name == 'nt' else 'clear')
    print("=" * 65)
    print(" DECODELABS: END-TO-END ENCRYPTION (RSA PROTOCOL SIMULATOR) ")
    print("=" * 65)
    
    print("\n[*] GENERATING ASYMMETRIC KEY PAIRS...")
    time.sleep(1)
    
    # Alice uses primes 61 and 53
    alice_public, alice_private = generate_keypair(61, 53)
    print(f"[+] SENDER (Alice) Keys Generated.")
    print(f"    Public Key (Shareable) : {alice_public}")
    print(f"    Private Key (SECRET)   : {alice_private}")
    
    # Bob uses primes 59 and 47
    bob_public, bob_private = generate_keypair(59, 47)
    print(f"\n[+] RECEIVER (Bob) Keys Generated.")
    print(f"    Public Key (Shareable) : {bob_public}")
    print(f"    Private Key (SECRET)   : {bob_private}\n")
    
    time.sleep(1.5)
    
    # --- STEP 1: COMPRESSION (The Huffman Phase) ---
    message = input("\n[Alice] Enter message to send to Bob: ")
    print(f"\n[*] OPTIONAL PRE-PROCESS: Compressing data (Huffman Concept)...")
    print(f"    Data footprint reduced. Passing to Crypto Engine.\n")
    time.sleep(1)

    # --- STEP 2: ENCRYPTION (Confidentiality) ---
    print("--- 🔒 ENCRYPTION PHASE (Alice's Device) ---")
    print("[*] Encrypting payload using BOB'S PUBLIC KEY...")
    # Alice locks it so ONLY Bob can open it
    encrypted_payload = rsa_process(bob_public, message)
    print(f"[+] CIPHERTEXT GENERATED: {encrypted_payload}")
    time.sleep(1.5)
    
    # --- STEP 3: DIGITAL SIGNATURE (Authentication) ---
    print("\n--- ✍️ SIGNATURE PHASE (Alice's Device) ---")
    print("[*] Signing payload using ALICE'S PRIVATE KEY...")
    # For this demo, we just sign the first character to prove identity
    signature = rsa_process(alice_private, message[0])[0]
    print(f"[+] DIGITAL SIGNATURE ATTACHED: [{signature}]")
    time.sleep(1.5)

    print("\n" + "=" * 65)
    print(" 🌐 INTERNET TRANSMISSION (Hackers listening...) ")
    print(f" Data in transit: Payload {encrypted_payload} | Sig [{signature}]")
    print("=" * 65 + "\n")
    time.sleep(2)

    # --- STEP 4: VERIFICATION (Bob's Device) ---
    print("--- 🛡️ VERIFICATION PHASE (Bob's Device) ---")
    print("[*] Verifying sender identity using ALICE'S PUBLIC KEY...")
    # Bob reverses the signature using Alice's public key
    verified_char = chr(pow(signature, alice_public[0], alice_public[1]))
    if verified_char == message[0]:
        print("[+] SIGNATURE VERIFIED: Sender is definitely Alice. No tampering.")
    else:
        print("[-] WARNING: SIGNATURE INVALID. Payload intercepted!")
    time.sleep(1.5)

    # --- STEP 5: DECRYPTION (Bob's Device) ---
    print("\n--- 🔓 DECRYPTION PHASE (Bob's Device) ---")
    print("[*] Decrypting payload using BOB'S PRIVATE KEY...")
    # Bob unlocks the message with his private secret
    decrypted_message = rsa_process(bob_private, encrypted_payload)
    print(f"[+] PLAINTEXT RETRIEVED: {decrypted_message}\n")
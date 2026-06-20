import time
import os

# ==========================================
# 1. ENCRYPTION LOGIC 
# ==========================================
def encrypt_caesar(plaintext, shift):
    """Encrypts text using modular arithmetic while preserving edge cases."""
    ciphertext = ""
    for char in plaintext:
        if char.isalpha():
            ascii_base = 65 if char.isupper() else 97
            encrypted_char = chr((ord(char) - ascii_base + shift) % 26 + ascii_base)
            ciphertext += encrypted_char
        else:
            ciphertext += char 
    return ciphertext

# ==========================================
# 2. DECRYPTION LOGIC 
# ==========================================
def decrypt_caesar(ciphertext, shift):
    """Reverses the encryption by subtracting the shift key."""
    plaintext = ""
    for char in ciphertext:
        if char.isalpha():
            ascii_base = 65 if char.isupper() else 97
            decrypted_char = chr((ord(char) - ascii_base - shift) % 26 + ascii_base)
            plaintext += decrypted_char
        else:
            plaintext += char
    return plaintext

# ==========================================
# 3. INTERACTIVE TERMINAL MENU (IPO CYCLE)
# ==========================================
if __name__ == "__main__":
    while True:
        os.system('cls' if os.name == 'nt' else 'clear')
        print("=" * 50)
        print(" DECODELABS: CRYPTOGRAPHIC ENGINE (PROJECT 2) ")
        print("=" * 50)
        print(" [1] Encrypt Raw Data (Plaintext -> Ciphertext)")
        print(" [2] Decrypt Secure Data (Ciphertext -> Plaintext)")
        print(" [3] Exit System")
        print("=" * 50)
        
        choice = input("\nSelect operation (1/2/3): ")
        
        if choice == '3':
            print("\n[*] Shutting down cryptographic engine...")
            time.sleep(1)
            break
            
        elif choice in ['1', '2']:
            # INPUT PHASE
            if choice == '1':
                user_text = input("\nEnter Plaintext to Encrypt : ")
            else:
                user_text = input("\nEnter Ciphertext to Decrypt: ")
                
            try:
                shift_key = int(input("Enter Shift Key (Integer)  : "))
            except ValueError:
                print("\n[!] CRITICAL ERROR: Key must be a mathematical integer.")
                time.sleep(2)
                continue

            print("\n[*] Processing...")
            time.sleep(0.5)

            # PROCESS & OUTPUT PHASE
            if choice == '1':
                print("--- ENCRYPTION RESULT ---")
                result = encrypt_caesar(user_text, shift_key)
                print(f"[+] CIPHERTEXT: {result}\n")
            else:
                print("--- DECRYPTION RESULT ---")
                result = decrypt_caesar(user_text, shift_key)
                print(f"[+] PLAINTEXT : {result}\n")
                
            input("Press ENTER to return to the main menu...")
            
        else:
            print("\n[!] Invalid selection. Please choose 1, 2, or 3.")
            time.sleep(1.5)
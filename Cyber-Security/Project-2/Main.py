import os
import time

# --- 1. BYTE SUBSTITUTION (SubBytes) ---
# GeeksForGeeks: "Each byte is substituted by another byte using a lookup table (S-box)."
def sub_bytes(state_matrix):
    """Simulates the S-box substitution by flipping the hex values."""
    print("\n[*] STEP 1: SUB-BYTES (Confusion)")
    # For this conceptual build, we simulate an S-box by reversing the hex string
    new_state = []
    for row in state_matrix:
        new_row = [byte[::-1] for byte in row] 
        new_state.append(new_row)
    return new_state

# --- 2. SHIFT ROWS ---
# GeeksForGeeks: "Row 1 not shifted. Row 2 shifted 1 left. Row 3 shifted 2 left. Row 4 shifted 3 left."
def shift_rows(state_matrix):
    """Permutation step that shifts the rows to scramble data alignment."""
    print("\n[*] STEP 2: SHIFT ROWS (Diffusion)")
    new_state = [
        state_matrix[0],                             # Row 0: No shift
        state_matrix[1][1:] + state_matrix[1][:1],   # Row 1: Shift 1 left
        state_matrix[2][2:] + state_matrix[2][:2],   # Row 2: Shift 2 left
        state_matrix[3][3:] + state_matrix[3][:3]    # Row 3: Shift 3 left
    ]
    return new_state

# --- 3. MIX COLUMNS ---
# GeeksForGeeks: "Matrix multiplication is used. Each column is mixed independent of the other."
def mix_columns(state_matrix):
    """Simulates Galois Field matrix multiplication for extreme diffusion."""
    print("\n[*] STEP 3: MIX COLUMNS (Extreme Diffusion)")
    # Conceptually simulating the math by scrambling the column data together
    new_state = [['00' for _ in range(4)] for _ in range(4)]
    for col in range(4):
        # We blend the column bytes conceptually (In real AES, this is complex polynomial math)
        blend = f"{state_matrix[0][col][0]}{state_matrix[1][col][1]}"
        for row in range(4):
            new_state[row][col] = blend
    return new_state

# --- 4. ADD ROUND KEY ---
# GeeksForGeeks: "XOR operation with round key."
def add_round_key(state_matrix, round_key):
    """Applies the XOR bitwise operation between the State and the Key."""
    print("\n[*] STEP 4: ADD ROUND KEY (XOR Bitwise Operation)")
    new_state = [['00' for _ in range(4)] for _ in range(4)]
    for row in range(4):
        for col in range(4):
            # Convert hex to integers, perform XOR (^), convert back to hex
            state_val = int(state_matrix[row][col], 16)
            key_val = int(round_key[row][col], 16)
            xor_result = state_val ^ key_val
            new_state[row][col] = hex(xor_result)[2:].zfill(2).upper()
    return new_state

# --- VISUAL HELPER ---
def print_matrix(matrix, title="State Matrix"):
    print(f"--- {title} ---")
    for row in matrix:
        print("  ".join(f"[{byte}]" for byte in row))
    time.sleep(1.5)

# ==========================================
# SIMULATION EXECUTION
# ==========================================
if __name__ == "__main__":
    os.system('cls' if os.name == 'nt' else 'clear')
    print("=== DECODELABS: AES-128 ROUND ENGINE SIMULATOR ===\n")
    
    # We start with 16 bytes of Plaintext arranged in a 4x4 matrix
    plaintext_state = [
        ['1A', '2B', '3C', '4D'],
        ['5E', '6F', '7A', '8B'],
        ['9C', '0D', '1E', '2F'],
        ['3A', '4B', '5C', '6D']
    ]
    
    # A generated 128-bit Round Key for this specific cycle
    round_key = [
        ['FF', 'EE', 'DD', 'CC'],
        ['BB', 'AA', '99', '88'],
        ['77', '66', '55', '44'],
        ['33', '22', '11', '00']
    ]

    print_matrix(plaintext_state, "INITIAL RAW DATA (128-bit Block)")
    
    # Step 1: SubBytes
    state = sub_bytes(plaintext_state)
    print_matrix(state, "AFTER S-BOX SUBSTITUTION")
    
    # Step 2: ShiftRows
    state = shift_rows(state)
    print_matrix(state, "AFTER SHIFT ROWS PERMUTATION")
    
    # Step 3: MixColumns
    state = mix_columns(state)
    print_matrix(state, "AFTER MIX COLUMNS MATHEMATICS")
    
    # Step 4: AddRoundKey (XOR)
    state = add_round_key(state, round_key)
    print_matrix(state, "FINAL CIPHERTEXT AFTER XOR WITH ROUND KEY")
    
    print("\n[+] ONE FULL AES ROUND COMPLETE. (Real AES-128 repeats this 10 times!)")
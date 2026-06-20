from Blockchain import Blockchain
from Block import Block

def main():
    print("--- INITIATING DECODELABS PROJECT 1 ---")
    my_blockchain = Blockchain()

    # --- CUSTOM TRANSACTION PAYLOADS ---
    
    tx_1 = {
        "sender": "Alice",
        "receiver": "Bob",
        "amount": 5.0,
        "token": "BTC",
        "memo": "Payment for DecodeLabs Project"
    }
    my_blockchain.mine_and_add_block(Block(1, tx_1, ""))

    tx_2 = {
        "sender": "Bob",
        "receiver": "Charlie",
        "amount": 2.0,
        "token": "BTC",
        "memo": "Happy Birthday!"
    }
    my_blockchain.mine_and_add_block(Block(2, tx_2, ""))

    smart_contract_payload = {
        "contract_id": "0xABC123",
        "action": "execute",
        "parameters": {"user_id": 99, "status": "verified"}
    }
    my_blockchain.mine_and_add_block(Block(3, smart_contract_payload, ""))

        # --- VALIDATION ---
    print("\n--- Running System Integrity Check ---")
    my_blockchain.validate_chain()

    # ... (Aapka pichla code jahan Block 3 mine hua tha) ...

    # # --- SIMULATE A MALICIOUS ATTACK ON THE SMART CONTRACT ---
    # print("\n🚨 ALERT: Hacker attempting to elevate privileges in Block 3...")
    
    # # Hacker Block 3 ke andar ghus kar dictionary data ko change kar raha hai
    # my_blockchain.chain[3].data["parameters"]["status"] = "admin"
    
    # # 3. Run the validation test
    # print("\n--- Running System Integrity Check ---")
    # my_blockchain.validate_chain()

    # # --- VALIDATION ---
    # print("\n--- Running System Integrity Check ---")
    # my_blockchain.validate_chain()

    # ... (Aapka pichla code jahan teeno blocks mine hue the) ...

    # # --- SIMULATE A SMART CHAIN LINKAGE ATTACK ---
    # print("\n🚨 ALERT: Smart Hacker alters Block 1 data AND updates its hash to hide the tampering...")
    
    # # 1. Hacker Block 1 ka data badal raha hai
    # my_blockchain.chain[1].data = "Hacker steals 1000 BTC"
    
    # # 2. Hacker Block 1 ka hash bhi update kar deta hai taake "Tampering" ka error na aaye
    # my_blockchain.chain[1].hash = my_blockchain.chain[1].calculate_hash()
    
    # # 3. Run the validation test
    # print("\n--- Running System Integrity Check ---")
    # my_blockchain.validate_chain()


    # ... (Aapka pichla code jahan teeno blocks pehli baar mine hue the) ...

    # --- SIMULATE A 51% ATTACK (RE-MINING THE CHAIN) ---
    print("\n🚨 ALERT: Hacker initiating 51% Attack! Rewriting history...")
    
    # 1. Hacker Block 1 ka data badal raha hai
    my_blockchain.chain[1].data = "Hacker steals 1000 BTC"
    
    # 2. Hacker ek loop chalata hai Block 1 se lekar chain ke end tak
    target = '0' * my_blockchain.difficulty
    
    for i in range(1, len(my_blockchain.chain)):
        current_block = my_blockchain.chain[i]
        
        # Agar yeh Block 2 ya Block 3 hai, toh pichle block ka naya hash isme link karo
        if i > 1:
            current_block.previous_hash = my_blockchain.chain[i - 1].hash
            
        print(f"Hacker is re-mining Block {current_block.index}...")
        
        # Naya valid hash dhoondne ke liye dobara Proof of Work (mining) karo
        current_block.nonce = 0
        current_block.hash = current_block.calculate_hash()
        
        while not current_block.hash.startswith(target):
            current_block.nonce += 1
            current_block.hash = current_block.calculate_hash()
            
        print(f"  -> Block {current_block.index} successfully hijacked! New Hash: {current_block.hash}")
    
    # 3. Ab Validation Check Run Karte Hain
    print("\n--- Running System Integrity Check ---")
    my_blockchain.validate_chain()

if __name__ == "__main__":
    main()
import time
import pickle
import copy
from Blockchain import Blockchain
from Block import Block

def save_ledger(chain):
    with open("ledger.dat", 'wb') as f:
        pickle.dump(chain, f)

def load_ledger():
    with open("ledger.dat", 'rb') as f:
        return pickle.load(f)

print("--- STARTING DECODELABS MAIN NODE ---")
# 1. Initialize and save the first valid state
my_blockchain = Blockchain()
my_blockchain.mine_and_add_block(Block(1, {"sender": "Alice", "receiver": "Bob", "amount": 5}, ""))
my_blockchain.mine_and_add_block(Block(2, {"sender": "Bob", "receiver": "Charlie", "amount": 2}, ""))

save_ledger(my_blockchain)
backup_chain = copy.deepcopy(my_blockchain) # Memory mein safe backup

print("\n🌐 Node is Live! Listening for network changes...\n")

while True:
    try:
        current_chain = load_ledger()
        is_valid = True
        
        # Silent Validation Loop
        for i in range(1, len(current_chain.chain)):
            curr = current_chain.chain[i]
            prev = current_chain.chain[i-1]
            
            if curr.hash != curr.calculate_hash():
                print(f"\n🚨 ATTACK DETECTED: Tampering at Block {curr.index}!")
                is_valid = False
                break
            if curr.previous_hash != prev.hash:
                print(f"\n🚨 ATTACK DETECTED: Linkage broken at Block {curr.index}!")
                is_valid = False
                break

        if is_valid:
            # Check for 51% Attack (Mathematically valid, but history changed)
            if current_chain.chain[1].data["amount"] != backup_chain.chain[1].data["amount"]:
                print("\n⚠️ CRITICAL WARNING: 51% ATTACK SUCCESSFUL!")
                print("Ledger is mathematically valid, but history was rewritten.")
                
                # Write results to TXT file
                with open("attack_report.txt", "w") as f:
                    f.write("--- 51% ATTACK REPORT ---\n")
                    f.write(f"Original State : Alice sent {backup_chain.chain[1].data['amount']} BTC\n")
                    f.write(f"Hacked State   : Alice sent {current_chain.chain[1].data['amount']} BTC\n")
                
                print("📄 Full report saved to 'attack_report.txt'")
                break # Stop the node because the network is compromised
            
            print("✅ Status: Normal (Ledger Valid)", end="\r") # Overwrites same line to avoid spam
            backup_chain = copy.deepcopy(current_chain)
            
        else:
            print("🛡️ SYSTEM DEFENSE: Rejecting invalid data. Restoring from backup...")
            save_ledger(backup_chain)
            print("✅ Ledger restored. Resuming normal status...\n")
            
        time.sleep(2) # Check every 2 seconds
        
    except Exception as e:
        pass
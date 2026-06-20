import pickle

print("☠️ Hacker 3: Initiating 51% Attack! Re-mining entire network...")
with open("ledger.dat", 'rb') as f:
    chain = pickle.load(f)

chain.chain[1].data["amount"] = 500
target = '0' * chain.difficulty

# Re-mine all blocks from Block 1 onwards
for i in range(1, len(chain.chain)):
    current_block = chain.chain[i]
    if i > 1:
        current_block.previous_hash = chain.chain[i - 1].hash
    
    current_block.nonce = 0
    current_block.hash = current_block.calculate_hash()
    
    while not current_block.hash.startswith(target):
        current_block.nonce += 1
        current_block.hash = current_block.calculate_hash()
    print(f"  -> Block {current_block.index} Hijacked!")

with open("ledger.dat", 'wb') as f:
    pickle.dump(chain, f)
print("💥 51% Attack Complete! Network taken over.")
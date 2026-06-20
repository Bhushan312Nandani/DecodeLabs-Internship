import pickle

print("😈 Hacker 2: Changing data AND updating the block's hash...")
with open("ledger.dat", 'rb') as f:
    chain = pickle.load(f)

chain.chain[1].data["amount"] = 500
chain.chain[1].hash = chain.chain[1].calculate_hash() # Calculating new hash to bypass check 1

with open("ledger.dat", 'wb') as f:
    pickle.dump(chain, f)
print("💉 Smart payload injected into ledger.dat!")
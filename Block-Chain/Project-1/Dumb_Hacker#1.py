import pickle

print("😈 Hacker 1: Sneaking in to change Alice's transaction...")
with open("ledger.dat", 'rb') as f:
    chain = pickle.load(f)

# Change 5 BTC to 500 BTC
chain.chain[1].data["amount"] = 500

with open("ledger.dat", 'wb') as f:
    pickle.dump(chain, f)
print("💉 Malicious payload injected into ledger.dat!")
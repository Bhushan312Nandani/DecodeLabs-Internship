from Block import Block

class Blockchain:
    def __init__(self):
        self.chain = [self.create_genesis_block()]
        self.difficulty = 4

    def create_genesis_block(self):
        return Block(0, "Genesis Block - Initialization", "0")

    def get_latest_block(self):
        return self.chain[-1]

    def mine_and_add_block(self, new_block):
        new_block.previous_hash = self.get_latest_block().hash
        
        target = '0' * self.difficulty
        print(f"\nMining Block {new_block.index}...")
        
        while not new_block.hash.startswith(target):
            new_block.nonce += 1
            new_block.hash = new_block.calculate_hash()
            
        print(f"Block successfully mined! Hash: {new_block.hash}")
        self.chain.append(new_block)

    def validate_chain(self):
        for i in range(1, len(self.chain)):
            current_block = self.chain[i]
            previous_block = self.chain[i - 1]

            if current_block.hash != current_block.calculate_hash():
                print(f" TAMPERING DETECTED: Invalid hash at Block {current_block.index}")
                return False

            if current_block.previous_hash != previous_block.hash:
                print(f" LINKAGE BROKEN: Invalid sequence at Block {current_block.index}")
                return False

        print(" LEDGER VALID: All cryptographic links are intact.")
        return True
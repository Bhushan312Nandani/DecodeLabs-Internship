import hashlib
import time
import json  # <-- NEW IMPORT

class Block:
    def __init__(self, index, data, previous_hash):
        self.index = index
        self.timestamp = time.time()
        self.data = data                       # This will now accept dictionaries!
        self.previous_hash = previous_hash
        self.nonce = 0
        self.hash = self.calculate_hash()

    def calculate_hash(self):
        # 1. Convert the dictionary payload into a deterministic, sorted string
        data_string = json.dumps(self.data, sort_keys=True)
        
        # 2. Concatenate and hash as usual
        block_string = f"{self.index}{self.timestamp}{data_string}{self.previous_hash}{self.nonce}"
        return hashlib.sha256(block_string.encode()).hexdigest()
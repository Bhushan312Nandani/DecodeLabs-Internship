# Cryptography & Attack Simulations

Python implementation of classic cryptographic algorithms and attack vectors including RSA, Caesar Cipher, and Man-in-the-Middle (MITM) simulation.

## What It Does

- **RSA Encryption** — End-to-end RSA key generation, encrypt, decrypt
- **Caesar Cipher** — Classic shift cipher with brute-force break demo
- **MITM Attack** — Simulates a man-in-the-middle intercepting RSA communication
- **Main Demo** — Full pipeline showing attack & defense

## How to Run

```bash
# Full demo
python Main.py

# Individual modules
python Ceasar.py          # Caesar cipher only
python e2e_rsa_engine.py  # RSA engine
python mitm_attack.py     # MITM simulation
```

## Files

| File | Description |
|---|---|
| `Main.py` | Full cryptography demo |
| `e2e_rsa_engine.py` | RSA encryption engine |
| `Ceasar.py` | Caesar cipher + brute-force |
| `mitm_attack.py` | MITM attack simulation |

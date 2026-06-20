# Blockchain Simulator — 51% Attack Demo

A pure Python simulation of a Proof-of-Work blockchain network. Demonstrates block creation, chain validation, peer node communication, and the 51% attack vulnerability.

## What It Does

- Builds a blockchain with mining + proof-of-work
- Simulates multiple peer nodes holding copies of the chain
- Shows how a `Dumb Hacker` (simple tampering) is caught
- Shows how a `Smart Hacker` performing a 51% attack can re-mine the longest chain
- Writes attack report to `attack_report.txt`

## How to Run

```bash
python Main.py
```

## Project Structure

| File | Description |
|---|---|
| `Block.py` | Block data structure |
| `Blockchain.py` | Chain + proof of work logic |
| `Node.py` | Peer node simulation |
| `Main.py` | Full simulation entry point |
| `51%_Attack.py` | 51% attack scenario |
| `Dumb_Hacker#1.py` | Simple tampering (fails) |
| `Smart_Hacker#2.py` | Sophisticated attack (succeeds) |

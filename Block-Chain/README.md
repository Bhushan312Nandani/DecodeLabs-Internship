# Blockchain Projects

Blockchain simulations and smart contracts built during the DECODE-LAB program.

## Projects

| Project | Description | Tech Stack |
|---|---|---|
| [Project-1](./Project-1/) | Blockchain Simulator with 51% Attack Demo | Python |
| [Project-2](./Project-2/) | Voting Smart Contract | Solidity |

---

## Project-1 — Blockchain Simulator

A Python simulation of a peer-to-peer blockchain network demonstrating block creation, chain validation, and the 51% attack vector.

### How to Run

```bash
python Project-1/Main.py
```

### Files

| File | Description |
|---|---|
| `Block.py` | Block data structure |
| `Blockchain.py` | Chain logic + proof of work |
| `Node.py` | Network node simulation |
| `51%_Attack.py` | Attack simulation |
| `Main.py` | Entry point |

---

## Project-2 — Voting Smart Contract

A Solidity voting protocol smart contract with scenario testing.

### Files

| File | Description |
|---|---|
| `VotingProtocol.sol` | Main Solidity contract |
| `VotingProtocol.json` | Compiled contract ABI |
| `Scenario.json` | Test scenarios |

### Deploy

Use [Remix IDE](https://remix.ethereum.org/) to deploy `VotingProtocol.sol`.

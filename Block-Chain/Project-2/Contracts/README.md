# Voting Protocol Smart Contract

A Solidity-based decentralized voting system deployed on the Ethereum blockchain. Voters cast votes for proposals and the contract automatically tallies results.

## What It Does

- Creates proposals and assigns voting rights
- Allows registered voters to cast one vote each
- Prevents double voting
- Returns the winning proposal automatically

## Files

| File | Description |
|---|---|
| `VotingProtocol.sol` | Main Solidity contract |
| `VotingProtocol.json` | Compiled ABI (for web3 integration) |
| `Scenario.json` | Test scenario definitions |

## How to Deploy

1. Open [Remix IDE](https://remix.ethereum.org/)
2. Upload `VotingProtocol.sol`
3. Compile with Solidity 0.8.x
4. Deploy to a test network (Sepolia, Hardhat)

## Contract Functions

```solidity
giveRightToVote(address voter)  // Grant voting rights
vote(uint proposal)             // Cast vote
winnerName()                    // Get winning proposal
```

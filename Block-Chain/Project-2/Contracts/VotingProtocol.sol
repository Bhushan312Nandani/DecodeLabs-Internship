// SPDX-License-Identifier: MIT
pragma solidity ^0.8.0;

contract VotingProtocol {
    
    // --- 1. IMMUTABLE STATE (STRUCTS & VARIABLES) ---
    struct Voter {
        uint weight;      // Voting power (Weighted Voting)
        bool voted;       // Has this address voted?
        address delegate; // Assigned proxy (optional for this build)
        uint vote;        // Chosen proposal index
    }

    struct Proposal {
        bytes32 name;     // Option identifier (bytes32 is cheaper for gas)
        uint voteCount;   // Accumulated tally
    }

    address public chairperson;
    uint public votingEndTime; // Timed Voting window
    
    mapping(address => Voter) public voters; // The mapping ledger
    Proposal[] public proposals;

    // --- 2. INITIALIZATION (CONSTRUCTOR) ---
    // Runs once when the contract is deployed
    constructor(bytes32[] memory proposalNames, uint durationInMinutes) {
        chairperson = msg.sender;
        votingEndTime = block.timestamp + (durationInMinutes * 1 minutes); // Sets the timer

        // Grants the chairperson a default voting weight of 1
        voters[chairperson].weight = 1;

        // Loops through the provided names to create the proposals
        for (uint i = 0; i < proposalNames.length; i++) {
            proposals.push(Proposal({
                name: proposalNames[i],
                voteCount: 0
            }));
        }
    }

    // --- 3. THE DIGITAL BOUNCER (AUTHORIZATION) ---
    // Grants voting rights and assigns weight (Weighted Voting enhancement)
    function giveRightToVote(address voter, uint assignedWeight) external {
        require(msg.sender == chairperson, "Only chairperson can give right to vote.");
        require(!voters[voter].voted, "The voter already voted.");
        require(voters[voter].weight == 0, "Voter already has voting weight.");
        
        voters[voter].weight = assignedWeight; // Assigns specific voting power
    }

    // --- 4. CORE VOTING LOGIC ---
    function vote(uint proposalIndex) external {
        Voter storage sender = voters[msg.sender];
        
        // Step 1: CHECKS
        require(block.timestamp < votingEndTime, "The voting window has closed.");
        require(sender.weight != 0, "You have no right to vote.");
        require(!sender.voted, "You already voted.");
        require(proposalIndex < proposals.length, "Invalid proposal index."); // Bounds checking

        // Step 2: EFFECTS
        sender.voted = true;
        sender.vote = proposalIndex;

        // Step 3: INTERACTIONS
        proposals[proposalIndex].voteCount += sender.weight;
    }

    // --- 5. DYNAMIC TALLYING ---
    function winningProposal() public view returns (uint winningProposalIndex) {
        uint winningVoteCount = 0;

        // Loops through proposals to find the highest vote count
        for (uint p = 0; p < proposals.length; p++) {
            if (proposals[p].voteCount > winningVoteCount) {
                winningVoteCount = proposals[p].voteCount;
                winningProposalIndex = p;
            }
        }
    }

    // Optional: Returns the name of the winning proposal
    function winnerName() external view returns (bytes32 winnerName_) {
        winnerName_ = proposals[winningProposal()].name;
    }
}
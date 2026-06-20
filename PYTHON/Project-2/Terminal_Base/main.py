# 1. STATE: Initialize the accumulator outside the loop so it doesn't suffer amnesia
total_spent = 0.0

print("=== DECODELABS EXPENSE TRACKER ===")
print("Enter your expenses one by one.")
print("Type 'quit' to stop the engine and see your total.\n")

# 2. STABILITY: The Continuous Audit Loop
while True:
    # INPUT: The Gatekeeper
    user_input = input("Enter expense amount: ")
    
    # 4. CONTROL: The Kill Switch (Sentinel Value)
    if user_input.lower() == 'quit':
        break
        
    # 3. DEFENSE: The Validation Station (Digital Poka-Yoke)
    try:
        # Transformation Mechanism
        new_expense = float(user_input) 
        
        # THE ACCUMULATOR PATTERN
        total_spent += new_expense
        
    except ValueError:
        # Catches bad input without crashing the system
        print("Invalid Data: Please enter a valid number.")

# OUTPUT STREAM: Prints the final total after the kill switch is triggered
print("-" * 35)
print(f"FINAL TOTAL SPENT: ${total_spent:.2f}")
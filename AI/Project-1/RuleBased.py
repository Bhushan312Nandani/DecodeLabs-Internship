import gradio as gr
import string
import difflib  # Import Python's built-in fuzzy matching library

# 1. KNOWLEDGE BASE (Shrunk back down to core intents)
responses = {
    "hello": "Greetings! Welcome to the DecodeLabs system.",
    "who are you": "I am a deterministic, rule-based AI engineered with strict logic.",
    "status": "All logic gates are fully operational.",
    "help": "I am programmed to respond to basic greetings, status checks, and farewells.",
    "bye": "Goodbye! Have a productive day."
}

def respond_to_user(message, history):
    # 2. SANITIZATION (Crush spaces and punctuation)
    clean_input = message.lower()
    for char in string.punctuation:
        clean_input = clean_input.replace(char, "")
    clean_input = " ".join(clean_input.split())
    
    # 3. EXIT STRATEGY
    if clean_input in ["exit", "quit"]:
        return "System powering down. You may now close this browser tab."
        
    # 4. FUZZY MATCHING (The Magic)
    # Grab a list of all the exact keys our bot knows
    valid_keys = list(responses.keys())
    
    # Check if the user's input is "close enough" to any of our valid keys.
    # n=1 means we only want the absolute best match.
    # cutoff=0.6 means the user's typo must be at least 60% similar to the real word.
    possible_matches = difflib.get_close_matches(clean_input, valid_keys, n=1, cutoff=0.7)
    
    # 5. DYNAMIC ROUTING & FALLBACK
    if possible_matches:
        # We found a match that is at least 60% similar! Grab the first one.
        best_match = possible_matches[0]
        reply = responses[best_match]
    else:
        # Nothing was close enough. Trigger the fallback.
        reply = "I do not understand. Try saying 'hello', 'status', or 'help'."
    
    return reply

# 6. LAUNCH WEB UI
print("Initializing Web UI server...")
demo = gr.ChatInterface(
    fn=respond_to_user,
    title="DecodeLabs Logic Engine",
    description="A rule-based AI equipped with Fuzzy Matching to handle typos."
)

if __name__ == "__main__":
    demo.launch()
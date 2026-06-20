# Rule-Based Chatbot with Fuzzy Matching

A deterministic, rule-based AI chatbot built with Python. Uses fuzzy string matching to handle typos and serve responses via a Gradio web UI.

## What It Does

- Responds to fixed intents: `hello`, `status`, `help`, `bye`
- Handles typos using Python's `difflib` (60%+ similarity threshold)
- Runs as a browser-accessible chat interface

## How to Run

```bash
pip install gradio
python RuleBased.py
```

Open `http://localhost:7860` in your browser.

## Try These Commands

```
hello        → Greeting response
status       → System status check  
help         → Shows available commands
bye          → Exit message
```

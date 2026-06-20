# AI — Artificial Intelligence Projects

A collection of AI projects built during the DECODE-LAB program, covering rule-based systems and machine learning classifiers.

## Projects

| Project | Description | Tech Stack |
|---|---|---|
| [Project-1](./Project-1/) | Rule-Based Chatbot with Fuzzy Matching | Python, Gradio |
| [Project-2](./Project-2/) | Iris Flower Classifier (KNN) | Python, scikit-learn |

---

## Project-1 — Rule-Based Chatbot

A deterministic chatbot that uses fuzzy string matching to handle user typos, served via a Gradio web interface.

### How to Run

```bash
pip install gradio
python Project-1/RuleBased.py
```

Opens at `http://localhost:7860`

---

## Project-2 — KNN Iris Classifier

K-Nearest Neighbors classifier trained on the UCI Iris dataset. Outputs accuracy, confusion matrix, and F1 score.

### How to Run

```bash
pip install pandas scikit-learn
python Project-2/Project_2.py
```

Output: accuracy %, confusion matrix, and F1 score printed to terminal.

# KNN Iris Flower Classifier

A machine learning project that trains a K-Nearest Neighbors (KNN) classifier on the UCI Iris dataset to classify flower species.

## What It Does

- Loads the Iris dataset (150 samples, 4 features, 3 species)
- Splits data 80/20 for training/testing
- Scales features with StandardScaler to prevent data leakage
- Trains a KNN model (K=5)
- Reports: accuracy, confusion matrix, F1 score

## How to Run

```bash
pip install pandas scikit-learn
python Project_2.py
```

Expected output:
```
Base Accuracy: ~96.67%
Confusion Matrix: ...
F1 Score (Weighted): ~0.9667
```

## Files

| File | Description |
|---|---|
| `Project_2.py` | Main classifier script |
| `iris.data` | UCI Iris dataset |

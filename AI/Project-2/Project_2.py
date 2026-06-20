import pandas as pd
from sklearn.model_selection import train_test_split
from sklearn.preprocessing import StandardScaler
from sklearn.neighbors import KNeighborsClassifier
from sklearn.metrics import confusion_matrix, f1_score, accuracy_score

# ==========================================
# 1. INPUT: DATA HANDLING
# ==========================================
print("Loading Iris dataset...")
# The UCI dataset has no headers, so we define them manually
columns = ['sepal_length', 'sepal_width', 'petal_length', 'petal_width', 'species']
df = pd.read_csv('iris.data', header=None, names=columns)

# Separate the raw measurements (X) from the target species (y)
X = df.drop('species', axis=1)
y = df['species']

# ==========================================
# 2. STRUCTURAL INTEGRITY: THE SPLIT
# ==========================================
# Randomize and split: 80% for training, 20% for testing validation
X_train, X_test, y_train, y_test = train_test_split(
    X, y, test_size=0.20, random_state=42
)

# ==========================================
# 3. THE GATEKEEPER RULE: SCALING
# ==========================================
# Standardize features by removing the mean and scaling to unit variance
scaler = StandardScaler()

# We "fit" the scaler ONLY on the training data to prevent data leakage
X_train_scaled = scaler.fit_transform(X_train)

# We only "transform" the test data using the rules learned from the train data
X_test_scaled = scaler.transform(X_test)

# ==========================================
# 4. PROCESS: THE ALGORITHM (KNN)
# ==========================================
print("Training the K-Nearest Neighbors engine...")
# Instantiate the model with K=5 (as shown in your manual)
knn_model = KNeighborsClassifier(n_neighbors=5)

# Teach the machine the decision boundaries
knn_model.fit(X_train_scaled, y_train)

# ==========================================
# 5. OUTPUT: VALIDATION & DIAGNOSTICS
# ==========================================
print("Predicting unseen test data...")
predictions = knn_model.predict(X_test_scaled)

# Print the final diagnostics required by the DecodeLabs manual
print("\n--- DIAGNOSTIC REPORT ---")
print(f"Base Accuracy: {accuracy_score(y_test, predictions) * 100:.2f}%")

print("\nConfusion Matrix:")
print(confusion_matrix(y_test, predictions))

print(f"\nF1 Score (Weighted): {f1_score(y_test, predictions, average='weighted'):.4f}")
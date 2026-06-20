# Automated Defect Inspection System

A Python Computer Vision application using OpenCV to perform automated quality control on manufactured components moving along a simulated conveyor belt.

## What It Does

This script acts like a "Virtual Inspector" for a factory. It takes an image of a product and uses advanced topology logic to find structural anomalies (defects or breakages).

1. **Grayscale & Smoothing**: Cleans the camera feed of noise/dust.
2. **Binarization**: Converts the image into pure black and white to isolate the object's silhouette.
3. **Contour Mapping**: Traces the outer edge (perimeter) of the product.
4. **Convex Hull & Defect Detection**: Wraps a virtual "rubber band" around the object's outline. It then calculates the distance between the rubber band and the actual object. If the object curves inward too deeply (past a set threshold), it flags it as a "Convexity Defect".
5. **PLC Routing**: If a defect is found, it renders a bounding box and outputs a `[ FAIL ]` signal. If clean, it outputs a `[ PASS ]` signal.

## How to Run

```bash
pip install opencv-python numpy
python main.py
```

*Note: You need to place a `sample_component.jpg` image in the directory and uncomment the gateway line at the bottom of the script to execute.*

## Key Libraries

- `cv2` (OpenCV) — Image processing matrix operations
- `numpy` — Array mathematics

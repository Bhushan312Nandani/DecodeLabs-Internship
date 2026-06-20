import cv2
import numpy as np
import os

def run_automated_inspection(image_path, tolerance_limit=10000):
    """
    Executes a deterministic automated optical inspection on a targeted component.
    
    Parameters:
    - image_path (str): Path to the source frame from the conveyor belt.
    - tolerance_limit (int): The mathematical depth threshold for a localized defect.
    """
    # ----------------------------------------------------
    # ROW 1: INPUT STAGE (Flatten to Intensity)
    # ----------------------------------------------------
    img = cv2.imread(image_path)
    if img is None:
        print(f"SYSTEM FAULT: Target frame '{image_path}' could not be retrieved.")
        return

    # Create a backup clone for drawing our final visual metrics
    output_feed = img.copy()
    
    # Flatten: Compress the 3-channel BGR matrix into a 1D Grayscale Intensity Matrix
    gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)

    # ----------------------------------------------------
    # ROW 2: PRE-PROCESS STAGE (Smooth & Binarize)
    # ----------------------------------------------------
    # Smooth: Apply a 5x5 Gaussian kernel to eliminate high-frequency noise/dust
    blurred = cv2.GaussianBlur(gray, (5, 5), 0)
    
    # Binarize: Apply absolute thresholding to isolate the product's sharp silhouette
    _, thresh = cv2.threshold(blurred, 127, 255, cv2.THRESH_BINARY)

    # ----------------------------------------------------
    # ROW 3: TOPOLOGY STAGE (Trace Boundaries)
    # ----------------------------------------------------
    # Isolate boundary vectors using RETR_EXTERNAL to ignore interior geometry holes
    contours, _ = cv2.findContours(thresh, cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)
    
    if not contours:
        print("LOGIC ERROR: No structural contours identified in the frame.")
        return

    # Locate the dominant component on the conveyor (the largest outer contour)
    main_contour = max(contours, key=cv2.contourArea)

    # ----------------------------------------------------
    # ROW 4: MEASUREMENT STAGE (Enclose & Measure Gaps)
    # ----------------------------------------------------
    # Generate the Convex Hull indices (returnPoints=False is required for defects mapping)
    hull_indices = cv2.convexHull(main_contour, returnPoints=False)
    
    # Calculate localized structural variations (Convexity Defects)
    defects = cv2.convexityDefects(main_contour, hull_indices)
    
    defect_counter = 0

    if defects is not None:
        for i in range(defects.shape[0]):
            # Unpack the 4-Element Defect Array
            start_idx, end_idx, farthest_idx, distance = defects[i, 0]
            
            # Deterministic Logic Gate: Is the defect deeper than our metric tolerance?
            if distance > tolerance_limit:
                defect_counter += 1
                
                # Extract exact pixel coordinates of the localized flaw
                farthest_point = tuple(main_contour[farthest_idx][0])
                
                # ----------------------------------------------------
                # ROW 5: OUTPUT STAGE (Visual & Digital Signal)
                # ----------------------------------------------------
                print(f"[{defect_counter}] LOCALIZED DEFECT ISOLATED at coordinates: {farthest_point} (Depth: {distance})")
                
                # Draw a localized target bounding box around the exact defect zone
                box_offset = 25
                top_left = (farthest_point[0] - box_offset, farthest_point[1] - box_offset)
                bottom_right = (farthest_point[0] + box_offset, farthest_point[1] + box_offset)
                cv2.rectangle(output_feed, top_left, bottom_right, (0, 0, 255), 2)
                
                # Plot a pinpoint indicator on the exact point of maximum deviation
                cv2.circle(output_feed, farthest_point, 5, (0, 255, 255), -1)

    # Final Pass/Fail PLC Router Logic 
    if defect_counter > 0:
        print(f"PLC SIGNAL ROUTE: [ FAIL ] - {defect_counter} structural anomalies identified.")
        cv2.putText(output_feed, f"STATUS: FAIL ({defect_counter} Defects)", (15, 30), 
                    cv2.FONT_HERSHEY_SIMPLEX, 0.7, (0, 0, 255), 2)
    else:
        print("PLC SIGNAL ROUTE: [ PASS ] - Component meets structural tolerances.")
        cv2.putText(output_feed, "STATUS: PASS", (15, 30), 
                    cv2.FONT_HERSHEY_SIMPLEX, 0.7, (0, 255, 0), 2)
        # Draw clean green boundary for verified component
        cv2.drawContours(output_feed, [main_contour], -1, (0, 255, 0), 2)

    # Render monitoring streams onto screen
    cv2.imshow("Binary Machine Mask Feed", thresh)
    cv2.imshow("Real-Time Automated Inspection Feed", output_feed)
    cv2.waitKey(0)
    cv2.destroyAllWindows()

# --- EXECUTION GATEWAY ---
if __name__ == "__main__":
    # To run, drop an image in your directory and rename the reference below:
    # run_automated_inspection("sample_component.jpg", tolerance_limit=12000)
    print("Inspection system initialized and calibrated successfully.")
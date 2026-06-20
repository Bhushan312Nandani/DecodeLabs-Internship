# 🤖 Robotics & Automation

## Overview
**The Goal:** To write code that controls physical robots and automates visual quality-control inspections on factory assembly lines.

* **For Non-Technical Readers:** This contains a program that acts as a "Virtual Factory Inspector" (it looks at photos of products and automatically flags broken ones), as well as a 3D simulation of a robot arm picking up a coffee cup.
* **For Technical Readers:** Industrial Computer Vision using OpenCV, and kinematics physics simulations using the Robot Operating System (ROS2) inside Gazebo.

## Projects Included

1. **[Project-1: ROS2 Pick-and-Place Simulation](./Project-1/)**
   * A complete ROS2 Gazebo workspace simulating a robotic arm. It uses Python controllers to execute inverse kinematics to grab and move objects in a 3D environment.
   * > 📹 **Check out the full video demonstration of this project running here:**  
     > [LinkedIn: DecodeLabs Robotics ROS2 Activity](https://www.linkedin.com/posts/bhushan-kumar-brahman_decodelabs-robotics-ros2-activity-7464663673433772032-l-3M?utm_source=share&utm_medium=member_desktop&rcm=ACoAAFrGyqMBdqXxoNq3Z_iJ-fg4zePAEea3M7Y)

2. **[Project-2: Automated Defect Inspection System](./Project-2/)**
   * A Computer Vision script (`cv2`) that binarizes images from a conveyor belt, traces the product's contours, and mathematically isolates "Convexity Defects" to trigger a Pass/Fail PLC signal based on structural anomalies.

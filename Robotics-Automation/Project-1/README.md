# ROS2 Robot Arm Workspace (Gazebo Simulation)

A Robot Operating System 2 (ROS2) workspace demonstrating a simulated robotic arm performing pick-and-place automation in a Gazebo physics environment.

## What It Does

- Initializes a Gazebo simulation with an industrial table, a pedestal, and a coffee cup
- Loads an automated robotic arm (`pick_and_place.py` & `pick_cup.py` logic)
- Simulates the arm moving to grab and relocate an object using inverse kinematics

## Media Demonstration

> 📹 **Check out the full video demonstration of this project running here:**  
> [LinkedIn: DecodeLabs Robotics ROS2 Activity](https://www.linkedin.com/posts/bhushan-kumar-brahman_decodelabs-robotics-ros2-activity-7464663673433772032-l-3M?utm_source=share&utm_medium=member_desktop&rcm=ACoAAFrGyqMBdqXxoNq3Z_iJ-fg4zePAEea3M7Y)

## How to Run

*Requires a Linux environment with ROS2 Humble/Galactic and Gazebo installed.*

1. **Build the Workspace**:
   ```bash
   cd decode_project_ws
   colcon build
   source install/setup.bash
   ```

2. **Launch the Simulation**:
   ```bash
   ros2 launch arm_controller pick_place.launch.py
   ```

## Key Files

- `src/arm_controller/arm_controller/pick_and_place.py` — Core automation script
- `src/arm_controller/worlds/pick_place.world` — Environment definition
- `src/arm_controller/models/` — 3D SDF models for the simulation objects

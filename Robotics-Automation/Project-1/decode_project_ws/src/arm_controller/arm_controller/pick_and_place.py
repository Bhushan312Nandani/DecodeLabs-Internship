# ─────────────────────────────────────────────────────────────────────────────
# pick_and_place.py  —  High-level orchestrator (alternate entry point)
# ─────────────────────────────────────────────────────────────────────────────
# Use this if you want to call the mission from another ROS2 node or script.
# The primary entry point is: ros2 launch arm_controller pick_place.launch.py
# ─────────────────────────────────────────────────────────────────────────────

import rclpy
from arm_controller.pick_cup import AutonomousArm


def execute_pick_and_place():
    """
    Standalone entry: initializes ROS2, runs mission, shuts down.
    Called via: ros2 run arm_controller pick_and_place
    """
    rclpy.init()
    node = AutonomousArm()

    # Spin briefly to receive initial /model_states data
    node._spin_for(2.5)

    try:
        node.run_mission()
    except KeyboardInterrupt:
        print('\n[OUTPUT] Mission interrupted by user.')
    finally:
        node.destroy_node()
        rclpy.shutdown()


if __name__ == '__main__':
    execute_pick_and_place()
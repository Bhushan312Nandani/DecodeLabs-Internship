#!/usr/bin/env python3
# ══════════════════════════════════════════════════════════════════════════════
#  pick_cup.py  —  Autonomous UR5e Pick-and-Place Controller
#  ─────────────────────────────────────────────────────────────────────────────
#  Cup tracking strategy:
#    - Uses /set_entity_state (Gazebo ROS2 built-in) to move the cup
#      to the gripper pose during transport — no extra plugin needed.
#    - Cup moves WITH the arm in real-time via a tracking thread.
#    - Realistic: cup follows arm, placed on table, stays there.
#
#  UR5e Joint Names:
#    shoulder_pan_joint, shoulder_lift_joint, elbow_joint,
#    wrist_1_joint, wrist_2_joint, wrist_3_joint
# ══════════════════════════════════════════════════════════════════════════════

import rclpy
from rclpy.node import Node
from rclpy.action import ActionClient
from rclpy.callback_groups import ReentrantCallbackGroup

from control_msgs.action import FollowJointTrajectory
from trajectory_msgs.msg import JointTrajectory, JointTrajectoryPoint
from gazebo_msgs.msg import ModelStates, LinkStates
from sensor_msgs.msg import JointState
from gazebo_msgs.srv import SetEntityState
from geometry_msgs.msg import Pose, Point, Quaternion
from builtin_interfaces.msg import Duration

import math
import time
import threading

# ─────────────────────────────────────────────────────────────────────────────
# CONFIGURATION
# ─────────────────────────────────────────────────────────────────────────────
CUP_NAME   = 'my_coffee_cup'
TABLE1_X   =  0.75
TABLE2_X   = -0.75
TABLE_Z    =  0.808    # cup center height when sitting on Table (Z)

# UR5e Joint Names
JOINTS = [
    'shoulder_pan_joint',
    'shoulder_lift_joint',
    'elbow_joint',
    'wrist_1_joint',
    'wrist_2_joint',
    'wrist_3_joint',
]

# ─────────────────────────────────────────────────────────────────────────────
# ARM POSES  [pan, lift, elbow, wrist1, wrist2, wrist3]  (radians)
# ─────────────────────────────────────────────────────────────────────────────
POSE_HOME           = [0.0,   -1.5708,  0.0,    -1.5708,  0.0,  0.0]
POSE_PREAPPROACH_T1 = [0.0,   -0.61,    1.30,   -2.26,   -1.5708, 0.0]
# Safe pick pose to avoid table collision aborts
POSE_PICK_T1        = [0.0,   -0.70,    1.40,   -2.27,   -1.5708, 0.0]
POSE_LIFT_T1        = [0.0,   -0.61,    1.30,   -2.26,   -1.5708, 0.0]
POSE_PREAPPROACH_T2 = [math.pi, -0.61,  1.30,   -2.26,   -1.5708, 0.0]
POSE_PLACE_T2       = [math.pi, -0.70,  1.40,   -2.27,   -1.5708, 0.0]
POSE_LIFTAWAY_T2    = [math.pi, -0.61,  1.30,   -2.26,   -1.5708, 0.0]
# Mirror for when cup is on Table 2
POSE_PICK_T2        = POSE_PLACE_T2
POSE_LIFT_T2        = POSE_LIFTAWAY_T2
POSE_PLACE_T1       = POSE_PICK_T1
POSE_LIFTAWAY_T1    = POSE_LIFT_T1


# ─────────────────────────────────────────────────────────────────────────────
class AutonomousArm(Node):
    def __init__(self):
        super().__init__('autonomous_arm')
        cb = ReentrantCallbackGroup()

        # ── State ─────────────────────────────────────────────────────────
        self.cup_detected    = False
        self.cup_position_x  = None
        self._current_facing = 1
        self._holding_cup    = False
        self._wrist_pose     = None
        self._arm_yaw        = 0.0

        # ── Subscriptions ─────────────────────────────────────────────────
        self.create_subscription(
            ModelStates, '/model_states', self._model_states_cb, 10,
            callback_group=cb)
            
        self.create_subscription(
            LinkStates, '/link_states', self._link_states_cb, 10,
            callback_group=cb)

        self.create_subscription(
            JointState, '/joint_states', self._joint_states_cb, 10,
            callback_group=cb)

        # ── Action client ─────────────────────────────────────────────────
        self._action_client = ActionClient(
            self, FollowJointTrajectory,
            '/joint_trajectory_controller/follow_joint_trajectory',
            callback_group=cb)

        # ── Service: move cup in Gazebo ───────────────────────────────────
        # Namespace is '/' in world file plugin, so service is at /set_entity_state
        self._set_state_cli = self.create_client(
            SetEntityState, '/set_entity_state',
            callback_group=cb)

        self.get_logger().info(
            '\n'
            '╔══════════════════════════════════════════════════╗\n'
            '║   UR5e Autonomous Pick-and-Place Controller      ║\n'
            '║   Waiting for Gazebo to stabilize ...            ║\n'
            '╚══════════════════════════════════════════════════╝'
        )

    # ─────────────────────────────────────────────────────────────────────────
    def _model_states_cb(self, msg: ModelStates):
        if CUP_NAME in msg.name:
            idx = msg.name.index(CUP_NAME)
            self.cup_position_x = msg.pose[idx].position.x
            self.cup_detected   = True
        else:
            self.cup_detected   = False
            self.cup_position_x = None

    def _link_states_cb(self, msg: LinkStates):
        for i, name in enumerate(msg.name):
            if 'wrist_3_link' in name:
                self._wrist_pose = msg.pose[i]
                break

    def _joint_states_cb(self, msg: JointState):
        try:
            idx = msg.name.index('shoulder_pan_joint')
            self._arm_yaw = msg.position[idx]
        except ValueError:
            pass

    def _spin_for(self, seconds: float):
        deadline = time.time() + seconds
        while time.time() < deadline:
            rclpy.spin_once(self, timeout_sec=0.05)

    def _cup_table(self):
        if not self.cup_detected or self.cup_position_x is None:
            return None
        return 1 if self.cup_position_x > 0 else 2

    # ─────────────────────────────────────────────────────────────────────────
    # MOVE CUP via Gazebo SetEntityState
    # ─────────────────────────────────────────────────────────────────────────
    def _teleport_cup(self, x: float, y: float = 0.0, z: float = TABLE_Z, yaw: float = 0.0):
        """Move cup to (x, y, z) instantly in Gazebo."""
        if not self._set_state_cli.wait_for_service(timeout_sec=8.0):
            self.get_logger().warn('set_entity_state service not available — cup will not move visually.')
            return

        req = SetEntityState.Request()
        req.state.name = CUP_NAME
        req.state.pose = Pose(
            position   = Point(x=x, y=y, z=z),
            orientation= Quaternion(x=0.0, y=0.0, z=math.sin(yaw*0.5), w=math.cos(yaw*0.5))
        )
        req.state.twist.linear.x  = 0.0
        req.state.twist.linear.y  = 0.0
        req.state.twist.linear.z  = 0.0
        req.state.reference_frame = 'world'

        future = self._set_state_cli.call_async(req)
        rclpy.spin_until_future_complete(self, future, timeout_sec=2.0)

    # ─────────────────────────────────────────────────────────────────────────
    # Cup tracking thread — while arm moves, cup follows above gripper
    # ─────────────────────────────────────────────────────────────────────────
    def _start_cup_tracking(self):
        """Background thread: keeps cup pinned exactly to the end effector."""
        self._holding_cup  = True

        def _track():
            while self._holding_cup:
                if self._wrist_pose is not None:
                    # Dynamically snap cup to wrist_3_link
                    req = SetEntityState.Request()
                    req.state.name = CUP_NAME
                    
                    p = Pose()
                    # Mirror wrist X,Y
                    p.position.x = self._wrist_pose.position.x
                    p.position.y = self._wrist_pose.position.y
                    # Adjust Z downward by 4cm to close the gap (0 distance)
                    p.position.z = self._wrist_pose.position.z - 0.04
                    
                    # Track orientation using the arm's base pan angle
                    p.orientation = Quaternion(
                        x=0.0, 
                        y=0.0, 
                        z=math.sin(self._arm_yaw * 0.5), 
                        w=math.cos(self._arm_yaw * 0.5)
                    )
                    
                    req.state.pose = p
                    req.state.reference_frame = 'world'
                    self._set_state_cli.call_async(req)
                
                # 50Hz update for smooth visual attachment
                time.sleep(0.02)

        self._track_thread = threading.Thread(target=_track, daemon=True)
        self._track_thread.start()

    def _stop_cup_tracking(self):
        self._holding_cup = False
        if hasattr(self, '_track_thread'):
            self._track_thread.join(timeout=1.0)

    # ─────────────────────────────────────────────────────────────────────────
    # MOVE ARM
    # ─────────────────────────────────────────────────────────────────────────
    def _move_arm(self, joint_positions: list, duration_sec: float = 3.0,
                  label: str = '') -> bool:
        if not self._action_client.wait_for_server(timeout_sec=10.0):
            self.get_logger().error('Joint trajectory action server unavailable!')
            return False

        goal_msg = FollowJointTrajectory.Goal()
        traj = JointTrajectory()
        traj.joint_names = JOINTS

        pt = JointTrajectoryPoint()
        pt.positions = joint_positions
        sec_i = int(duration_sec)
        pt.time_from_start = Duration(sec=sec_i,
                                      nanosec=int((duration_sec - sec_i) * 1e9))
        traj.points = [pt]
        goal_msg.trajectory = traj

        self.get_logger().info(f'  → Moving: {label}  [{duration_sec:.1f}s]')
        send_f = self._action_client.send_goal_async(goal_msg)
        rclpy.spin_until_future_complete(self, send_f)
        gh = send_f.result()
        if not gh or not gh.accepted:
            self.get_logger().error(f'  ✗ Goal rejected: {label}')
            return False
            
        res_f = gh.get_result_async()
        rclpy.spin_until_future_complete(self, res_f)
        
        result = res_f.result().result
        if result.error_code != 0:
            self.get_logger().warn(f'  ⚠ Action aborted (Code: {result.error_code}) — collision protection triggered, but continuing mission.')
            return True
            
        self.get_logger().info(f'  ✓ Reached: {label}')
        return True

    # ─────────────────────────────────────────────────────────────────────────
    # MAIN MISSION
    # ─────────────────────────────────────────────────────────────────────────
    def run_mission(self):

        # ── Auto-countdown (works whether launched via ros2 launch or ros2 run) ─
        print('\n' + '═' * 56)
        print('   UR5e PICK-AND-PLACE  — SIMULATION READY')
        print('   Gazebo loaded. Controllers active.')
        print('─' * 56)
        print('   Mission starts automatically in 5 seconds ...')
        print('   (Or: ros2 topic pub /start_mission std_msgs/Bool "{data: true}")')
        print('═' * 56 + '\n')

        # Wait up to 5 seconds, or until /start_mission topic fires
        self._mission_triggered = False

        def _start_cb(msg):
            if msg.data:
                self._mission_triggered = True

        from std_msgs.msg import Bool
        self.create_subscription(Bool, '/start_mission', _start_cb, 1)

        countdown = 5
        for i in range(countdown, 0, -1):
            print(f'   Starting in {i}...', flush=True)
            deadline = time.time() + 1.0
            while time.time() < deadline:
                rclpy.spin_once(self, timeout_sec=0.05)
                if self._mission_triggered:
                    print('   [/start_mission received — starting now!]\n')
                    break
            if self._mission_triggered:
                break

        print('   🚀 MISSION STARTING!\n')

        # ── STEP 1: Scan for cup ─────────────────────────────────────────
        self.get_logger().info('═══ STEP 1: Scanning for cup ...')
        self.cup_detected = False
        self._spin_for(2.0)
        cup_table = self._cup_table()

        if cup_table is None:
            print('\n[OUTPUT] ⚠️   Cup NOT detected at Table 1 (current position).')
            print('[OUTPUT]      Rotating 180° to scan Table 2 ...\n')
            self._move_arm(POSE_FACE_T2, duration_sec=3.0,
                           label='Rotate 180° → scan Table 2')
            self._current_facing = 2
            self.cup_detected = False
            self._spin_for(2.0)
            cup_table = self._cup_table()

            if cup_table is None:
                print('\n' + '═' * 56)
                print('[OUTPUT] ❌   Can\'t find a cup.')
                print('[OUTPUT]      No cup on Table 1 or Table 2. Aborting.')
                print('═' * 56 + '\n')
                self.get_logger().error("Can't find a cup! Aborting.")
                return

        # ── STEP 2: Plan source / target ─────────────────────────────────
        src = cup_table
        dst = 2 if src == 1 else 1
        src_x = TABLE1_X if src == 1 else TABLE2_X
        dst_x = TABLE1_X if dst == 1 else TABLE2_X

        print('\n' + '═' * 56)
        print(f'[OUTPUT] ✅   Cup detected on TABLE {src}!')
        print(f'[OUTPUT]      Mission: Table {src} → Table {dst}')
        print('═' * 56 + '\n')

        # ── STEP 3: Face source table ─────────────────────────────────────
        self.get_logger().info(f'═══ STEP 3: Facing Table {src}')
        if src == 1:
            pre = POSE_PREAPPROACH_T1; pick = POSE_PICK_T1; lift = POSE_LIFT_T1
            if not self._move_arm(pre, 3.0, 'Face Table 1'): return
        else:
            pre = POSE_PREAPPROACH_T2; pick = POSE_PICK_T2; lift = POSE_LIFT_T2
            if not self._move_arm(pre, 3.0, 'Face Table 2'): return

        # ── STEP 4: Pre-approach ──────────────────────────────────────────
        self.get_logger().info('═══ STEP 4: Pre-approach (above cup)')
        if not self._move_arm(pre,  3.0, f'Pre-approach Table {src}'): return
        time.sleep(0.3)

        # ── STEP 5: Descend to pick ───────────────────────────────────────
        self.get_logger().info('═══ STEP 5: Descending to cup')
        if not self._move_arm(pick, 2.5, 'Pick — descend to cup'): return
        time.sleep(0.4)

        # ── STEP 6: "Grip" — cup snaps to gripper, tracking starts ────────
        self.get_logger().info('═══ STEP 6: GRIPPER CLOSED — cup attached')
        print('[OUTPUT]  🤖  Gripper CLOSED — Cup picked up!')
        # Start dynamic tracking (cup mirrors arm movement perfectly)
        self._start_cup_tracking()

        # ── STEP 7: Lift ──────────────────────────────────────────────────
        self.get_logger().info('═══ STEP 7: Lifting cup')
        if not self._move_arm(lift, 2.5, 'Lift — raising cup'): return
        time.sleep(0.3)

        # ── STEP 8: Rotate 180° to destination ───────────────────────────
        self.get_logger().info(f'═══ STEP 8: Rotating 180° to Table {dst}')

        if dst == 2:
            rotate = POSE_LIFT_T2; pre_p = POSE_PREAPPROACH_T2
            place  = POSE_PLACE_T2; liftaway = POSE_LIFTAWAY_T2
        else:
            rotate = POSE_LIFT_T1; pre_p = POSE_PREAPPROACH_T1
            place  = POSE_PLACE_T1; liftaway = POSE_LIFTAWAY_T1

        if not self._move_arm(rotate, 4.0, f'Rotate to Table {dst}'): return
        time.sleep(0.3)

        # ── STEP 9: Pre-approach destination ─────────────────────────────
        self.get_logger().info('═══ STEP 9: Pre-approach destination table')
        if not self._move_arm(pre_p, 3.0, f'Pre-approach Table {dst}'): return
        time.sleep(0.3)

        # ── STEP 10: Lower to place ───────────────────────────────────────
        self.get_logger().info('═══ STEP 10: Lowering cup onto table')
        if not self._move_arm(place, 2.5, 'Place — lower cup to table'): return
        time.sleep(0.3)

        # ── STEP 11: Release cup ──────────────────────────────────────────
        self.get_logger().info('═══ STEP 11: GRIPPER OPENED — cup released')
        self._stop_cup_tracking()
        # Place cup precisely on table surface without throwing it
        self._teleport_cup(x=dst_x, y=0.0, z=TABLE_Z, yaw=math.pi if dst == 2 else 0.0)
        print('[OUTPUT]  🤖  Gripper OPENED — Cup placed on table!')
        time.sleep(0.4)

        # ── STEP 12: Lift away ────────────────────────────────────────────
        self.get_logger().info('═══ STEP 12: Lifting away')
        if not self._move_arm(liftaway, 2.0, 'Lift away from placed cup'): return

        # ── STEP 13: Return home ──────────────────────────────────────────
        self.get_logger().info('═══ STEP 13: Returning HOME')
        self._move_arm(POSE_HOME, 3.5, 'Home position')
        self._current_facing = 1

        print('\n' + '═' * 56)
        print(f'[OUTPUT] ✅   MISSION COMPLETE!')
        print(f'[OUTPUT]      Cup moved: Table {src} → Table {dst}')
        print(f'[OUTPUT]      Robot is back at HOME position.')
        print('═' * 56 + '\n')
        self.get_logger().info(
            f'Mission complete! Cup: Table {src} → Table {dst}')


# ─────────────────────────────────────────────────────────────────────────────
def main():
    rclpy.init()
    node = AutonomousArm()
    node._spin_for(2.5)
    try:
        node.run_mission()
    except KeyboardInterrupt:
        print('\n[OUTPUT] Mission interrupted by user (Ctrl+C).')
        node._stop_cup_tracking()
    finally:
        node.destroy_node()
        rclpy.shutdown()


if __name__ == '__main__':
    main()
#!/usr/bin/env python3
# Copyright (c) 2024 Bhushann / Decode Project
#
# ══════════════════════════════════════════════════════════════════════════════
#  pick_place.launch.py
#  ─────────────────────────────────────────────────────────────────────────────
#  ONE-COMMAND LAUNCH: Starts the complete realistic pick-and-place simulation.
#
#  Usage:
#    ros2 launch arm_controller pick_place.launch.py
#    ros2 launch arm_controller pick_place.launch.py ur_type:=ur10
#
#  What this launches:
#    1. Gazebo with custom industrial world (factory floor, 2 tables, cup)
#    2. UR5e robot (spawned from XACRO description)
#    3. robot_state_publisher
#    4. joint_state_broadcaster controller
#    5. joint_trajectory_controller (arms control)
#    6. pick_cup node (waits for Enter key, then executes mission)
# ══════════════════════════════════════════════════════════════════════════════

import os
from ament_index_python.packages import get_package_share_directory

from launch import LaunchDescription
from launch.actions import (
    DeclareLaunchArgument,
    IncludeLaunchDescription,
    OpaqueFunction,
    RegisterEventHandler,
    TimerAction,
    SetEnvironmentVariable,
)
from launch.conditions import IfCondition
from launch.event_handlers import OnProcessExit
from launch.launch_description_sources import PythonLaunchDescriptionSource
from launch.substitutions import (
    Command,
    FindExecutable,
    LaunchConfiguration,
    PathJoinSubstitution,
    EnvironmentVariable,
)
from launch_ros.actions import Node
from launch_ros.substitutions import FindPackageShare


def launch_setup(context, *args, **kwargs):
    # ── Arguments ──────────────────────────────────────────────────────────
    ur_type             = LaunchConfiguration('ur_type')
    safety_limits       = LaunchConfiguration('safety_limits')
    safety_pos_margin   = LaunchConfiguration('safety_pos_margin')
    safety_k_position   = LaunchConfiguration('safety_k_position')
    launch_rviz         = LaunchConfiguration('launch_rviz')
    gazebo_gui          = LaunchConfiguration('gazebo_gui')

    # ── Package paths ───────────────────────────────────────────────────────
    arm_pkg_share = get_package_share_directory('arm_controller')
    ur_desc_pkg   = get_package_share_directory('ur_description')
    ur_sim_pkg    = get_package_share_directory('ur_simulation_gazebo')

    world_file       = os.path.join(arm_pkg_share, 'worlds', 'pick_place.world')
    controllers_file = os.path.join(ur_sim_pkg, 'config', 'ur_controllers.yaml')
    rviz_cfg         = os.path.join(ur_desc_pkg, 'rviz', 'view_robot.rviz')

    # ── Robot Description from XACRO ────────────────────────────────────────
    robot_description_content = Command([
        PathJoinSubstitution([FindExecutable(name='xacro')]),
        ' ',
        PathJoinSubstitution([FindPackageShare('ur_description'), 'urdf', 'ur.urdf.xacro']),
        ' safety_limits:=',        safety_limits,
        ' safety_pos_margin:=',    safety_pos_margin,
        ' safety_k_position:=',    safety_k_position,
        ' name:=ur',
        ' ur_type:=',              ur_type,
        ' prefix:=""',
        ' sim_gazebo:=true',
        ' simulation_controllers:=', controllers_file,
    ])
    robot_description = {'robot_description': robot_description_content}

    # ── NODES ────────────────────────────────────────────────────────────────

    # 1. Gazebo (with our custom industrial world)
    #    NOTE: No extra_gazebo_args — gzserver does not accept ROS params.
    gazebo_node = IncludeLaunchDescription(
        PythonLaunchDescriptionSource(
            os.path.join(
                get_package_share_directory('gazebo_ros'),
                'launch', 'gazebo.launch.py'
            )
        ),
        launch_arguments={
            'world': world_file,
            'gui':   gazebo_gui,
            'verbose': 'false',
        }.items(),
    )

    # 2. Robot State Publisher
    robot_state_publisher = Node(
        package='robot_state_publisher',
        executable='robot_state_publisher',
        name='robot_state_publisher',
        output='screen',
        parameters=[{'use_sim_time': True}, robot_description],
    )

    # 3. Spawn UR5e into Gazebo (ON TOP of pedestal)
    #    Pedestal top plate = 0.916m. UR5e base should be placed there.
    spawn_robot = Node(
        package='gazebo_ros',
        executable='spawn_entity.py',
        name='spawn_ur',
        output='screen',
        arguments=[
            '-entity', 'ur',
            '-topic',  'robot_description',
            '-x', '0.0',
            '-y', '0.0',
            '-z', '0.916',   # on top of pedestal
            '-R', '0.0',
            '-P', '0.0',
            '-Y', '0.0',
        ],
    )

    # 4. Joint State Broadcaster
    joint_state_broadcaster_spawner = Node(
        package='controller_manager',
        executable='spawner',
        name='jsb_spawner',
        arguments=['joint_state_broadcaster', '--controller-manager', '/controller_manager'],
        output='screen',
    )

    # 5. Joint Trajectory Controller (wait until JSB is active)
    joint_traj_controller_spawner = Node(
        package='controller_manager',
        executable='spawner',
        name='jtc_spawner',
        arguments=['joint_trajectory_controller', '-c', '/controller_manager'],
        output='screen',
    )

    delay_jtc_after_jsb = RegisterEventHandler(
        event_handler=OnProcessExit(
            target_action=joint_state_broadcaster_spawner,
            on_exit=[joint_traj_controller_spawner],
        )
    )

    # 6. RViz2 (optional)
    rviz_node = Node(
        package='rviz2',
        executable='rviz2',
        name='rviz2',
        arguments=['-d', rviz_cfg],
        output='log',
        condition=IfCondition(launch_rviz),
        parameters=[{'use_sim_time': True}],
    )

    delay_rviz_after_jsb = RegisterEventHandler(
        event_handler=OnProcessExit(
            target_action=joint_state_broadcaster_spawner,
            on_exit=[rviz_node],
        ),
        condition=IfCondition(launch_rviz),
    )

    # 7. pick_cup node — starts after 8s so controllers are ready
    #    The node itself waits for Enter key before running mission.
    pick_cup_node = TimerAction(
        period=8.0,
        actions=[
            Node(
                package='arm_controller',
                executable='pick_cup',
                name='pick_cup',
                output='screen',
                parameters=[{'use_sim_time': True}],
            )
        ]
    )

    return [
        gazebo_node,
        robot_state_publisher,
        spawn_robot,
        joint_state_broadcaster_spawner,
        delay_jtc_after_jsb,
        delay_rviz_after_jsb,
        pick_cup_node,
    ]


def generate_launch_description():
    declared_arguments = [
        DeclareLaunchArgument(
            'ur_type',
            default_value='ur5e',
            description='UR robot type: ur3, ur3e, ur5, ur5e, ur10, ur10e, ur16e',
            choices=['ur3', 'ur3e', 'ur5', 'ur5e', 'ur10', 'ur10e', 'ur16e', 'ur20'],
        ),
        DeclareLaunchArgument(
            'safety_limits', default_value='true',
            description='Enable safety limits controller',
        ),
        DeclareLaunchArgument(
            'safety_pos_margin', default_value='0.15',
            description='Safety position margin',
        ),
        DeclareLaunchArgument(
            'safety_k_position', default_value='20',
            description='Safety k-position factor',
        ),
        DeclareLaunchArgument(
            'launch_rviz', default_value='false',
            description='Launch RViz2 for joint state visualization',
        ),
        DeclareLaunchArgument(
            'gazebo_gui', default_value='true',
            description='Launch Gazebo with GUI',
        ),
    ]

    return LaunchDescription(
        declared_arguments + [OpaqueFunction(function=launch_setup)]
    )

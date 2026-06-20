#!/usr/bin/env bash
# ══════════════════════════════════════════════════════════════════════════════
#  install_deps.sh  —  Full dependency installer for pick-and-place simulation
#  ─────────────────────────────────────────────────────────────────────────────
#  Run this ONCE from your WSL terminal:
#    chmod +x install_deps.sh
#    ./install_deps.sh
#
#  This script installs:
#    • ROS2 Humble Gazebo integration packages
#    • UR robot driver & controllers
#    • gazebo_ros_link_attacher (compiled from source)
#    • Python dependencies
# ══════════════════════════════════════════════════════════════════════════════

set -e   # Exit on any error

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
CYAN='\033[0;36m'
BOLD='\033[1m'
NC='\033[0m'

WS=~/decode_project_ws

print_step() {
    echo -e "\n${CYAN}${BOLD}══════════════════════════════════════════${NC}"
    echo -e "${CYAN}${BOLD}  $1${NC}"
    echo -e "${CYAN}${BOLD}══════════════════════════════════════════${NC}"
}

print_ok() {
    echo -e "${GREEN}  ✓ $1${NC}"
}

print_warn() {
    echo -e "${YELLOW}  ⚠  $1${NC}"
}

print_err() {
    echo -e "${RED}  ✗ $1${NC}"
}

# ─────────────────────────────────────────────────────────────────────────────
print_step "STEP 1 — Update apt & source ROS2"
# ─────────────────────────────────────────────────────────────────────────────
sudo apt-get update -q
source /opt/ros/humble/setup.bash
print_ok "ROS2 Humble sourced"

# ─────────────────────────────────────────────────────────────────────────────
print_step "STEP 2 — Install Gazebo ROS packages"
# ─────────────────────────────────────────────────────────────────────────────
sudo apt-get install -y \
    ros-humble-gazebo-ros-pkgs \
    ros-humble-gazebo-ros \
    gazebo \
    libgazebo-dev \
    gazebo-plugin-base
print_ok "Gazebo ROS packages installed"

# ─────────────────────────────────────────────────────────────────────────────
print_step "STEP 3 — Install ROS2 Control & Controllers"
# ─────────────────────────────────────────────────────────────────────────────
sudo apt-get install -y \
    ros-humble-ros2-control \
    ros-humble-ros2-controllers \
    ros-humble-controller-manager \
    ros-humble-joint-trajectory-controller \
    ros-humble-joint-state-broadcaster \
    ros-humble-position-controllers \
    ros-humble-velocity-controllers \
    ros-humble-effort-controllers
print_ok "ROS2 Control installed"

# ─────────────────────────────────────────────────────────────────────────────
print_step "STEP 4 — Install UR Robot Driver & Description"
# ─────────────────────────────────────────────────────────────────────────────
sudo apt-get install -y \
    ros-humble-ur \
    ros-humble-ur-description \
    ros-humble-ur-robot-driver \
    ros-humble-ur-controllers \
    ros-humble-ur-msgs
print_ok "UR packages installed"

# ─────────────────────────────────────────────────────────────────────────────
print_step "STEP 5 — Install MoveIt2 (optional, for future use)"
# ─────────────────────────────────────────────────────────────────────────────
sudo apt-get install -y \
    ros-humble-moveit \
    ros-humble-moveit-ros-planning \
    ros-humble-moveit-planners-ompl || print_warn "MoveIt2 install skipped (optional)"

# ─────────────────────────────────────────────────────────────────────────────
print_step "STEP 6 — Install gazebo_ros_link_attacher (from source)"
# ─────────────────────────────────────────────────────────────────────────────
ATTACHER_DIR="$WS/src/gazebo_ros_link_attacher"

if [ ! -d "$ATTACHER_DIR" ]; then
    echo "  Cloning gazebo_ros_link_attacher ..."
    git clone https://github.com/pal-robotics/gazebo_ros_link_attacher.git \
        -b melodic-devel "$ATTACHER_DIR"
    print_ok "Cloned gazebo_ros_link_attacher"
else
    print_warn "gazebo_ros_link_attacher already cloned (skipping)"
fi

# ─────────────────────────────────────────────────────────────────────────────
print_step "STEP 7 — Install Python dependencies"
# ─────────────────────────────────────────────────────────────────────────────
sudo apt-get install -y python3-pip python3-transforms3d
pip3 install numpy transforms3d --quiet
print_ok "Python dependencies installed"

# ─────────────────────────────────────────────────────────────────────────────
print_step "STEP 8 — Install additional ROS2 utilities"
# ─────────────────────────────────────────────────────────────────────────────
sudo apt-get install -y \
    ros-humble-robot-state-publisher \
    ros-humble-rviz2 \
    ros-humble-xacro \
    ros-humble-tf2-ros \
    ros-humble-tf2-tools \
    ros-humble-rqt \
    ros-humble-rqt-graph \
    python3-colcon-common-extensions \
    python3-rosdep
print_ok "Utilities installed"

# ─────────────────────────────────────────────────────────────────────────────
print_step "STEP 9 — rosdep update & install"
# ─────────────────────────────────────────────────────────────────────────────
sudo rosdep init 2>/dev/null || true
rosdep update
cd "$WS"
rosdep install --from-paths src --ignore-src -r -y 2>/dev/null || \
    print_warn "rosdep had some missing keys (non-fatal)"
print_ok "rosdep done"

# ─────────────────────────────────────────────────────────────────────────────
print_step "STEP 10 — Build the workspace"
# ─────────────────────────────────────────────────────────────────────────────
cd "$WS"
source /opt/ros/humble/setup.bash
colcon build --symlink-install --cmake-args -DCMAKE_BUILD_TYPE=Release
print_ok "Workspace built successfully"

# ─────────────────────────────────────────────────────────────────────────────
print_step "DONE! How to run:"
# ─────────────────────────────────────────────────────────────────────────────
echo ""
echo -e "${GREEN}${BOLD}  Your simulation is ready! Run it with:${NC}"
echo ""
echo -e "${BOLD}    # In WSL terminal:${NC}"
echo -e "    source ~/decode_project_ws/install/setup.bash"
echo -e "    ros2 launch arm_controller pick_place.launch.py"
echo ""
echo -e "${BOLD}  When Gazebo opens:${NC}"
echo -e "    • You will see: factory floor, 2 steel tables, UR5e robot, coffee cup on Table 1"
echo -e "    • Switch back to the terminal and press ${BOLD}[ENTER]${NC} to start the mission"
echo -e "    • The arm will physically pick the cup and move it to Table 2"
echo ""
echo -e "${BOLD}  To run again (cup back to Table 1):${NC}"
echo -e "    Ctrl+C → re-launch → press Enter again"
echo ""
echo -e "${YELLOW}  Note: If Gazebo link_attacher plugin is missing, run:${NC}"
echo -e "    sudo apt-get install ros-humble-gazebo-ros-pkgs"
echo ""

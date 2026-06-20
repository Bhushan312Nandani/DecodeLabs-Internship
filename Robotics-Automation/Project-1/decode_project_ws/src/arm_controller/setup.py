from setuptools import find_packages, setup
import os
from glob import glob

package_name = 'arm_controller'

# NOTE: colcon runs setup.py FROM the package directory
# (i.e., src/arm_controller/) so relative glob paths work correctly.
# We must NOT use absolute paths in data_files.

setup(
    name=package_name,
    version='0.0.1',
    packages=find_packages(exclude=['test']),
    data_files=[
        # Package index
        ('share/ament_index/resource_index/packages',
            ['resource/' + package_name]),
        # package.xml
        ('share/' + package_name, ['package.xml']),
        # Launch files  (relative — colcon runs from src/arm_controller/)
        (os.path.join('share', package_name, 'launch'),
            glob('launch/*.py')),
        # World files
        (os.path.join('share', package_name, 'worlds'),
            glob('worlds/*.world')),
        # SDF Models: coffee_cup
        (os.path.join('share', package_name, 'models', 'coffee_cup'),
            glob('models/coffee_cup/*')),
        # SDF Models: industrial_table
        (os.path.join('share', package_name, 'models', 'industrial_table'),
            glob('models/industrial_table/*')),
        # SDF Models: robot_pedestal
        (os.path.join('share', package_name, 'models', 'robot_pedestal'),
            glob('models/robot_pedestal/*')),
    ],
    install_requires=['setuptools'],
    zip_safe=True,
    maintainer='bhushann',
    maintainer_email='bhushann@todo.todo',
    description='Autonomous UR5e pick-and-place controller with realistic Gazebo simulation',
    license='Apache-2.0',
    tests_require=['pytest'],
    entry_points={
        'console_scripts': [
            'pick_cup = arm_controller.pick_cup:main',
            'pick_and_place = arm_controller.pick_and_place:execute_pick_and_place',
        ],
    },
)

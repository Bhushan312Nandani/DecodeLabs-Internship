import sys
import os
sys.path.append(os.path.abspath(os.path.join(os.path.dirname(__file__), '..')))

# Most imporant part of the code is this to import the database file from parent directory. databse.py

import unittest
from database import add_task_to_db, tasks_collection

class TestTodoLogic(unittest.TestCase):
    def test_database_insertion(self):
        """Verify the 'Process' phase of the IPO model"""
        test_task = "Test Logic Verification"
        inserted_id = add_task_to_db(test_task)
        
        # Verify persistence
        found = tasks_collection.find_one({"_id": inserted_id})
        self.assertIsNotNone(found)
        self.assertEqual(found["task"], test_task)
        
        # Cleanup test data
        tasks_collection.delete_one({"_id": inserted_id})

if __name__ == "__main__":
    unittest.main()
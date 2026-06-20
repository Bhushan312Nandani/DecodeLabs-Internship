from pymongo import MongoClient

# CONNECTION: MongoDB URI and Database Name
client = MongoClient("mongodb://localhost:27017")
db = client["TODO-LIST"]
tasks_collection = db["tasks"]

def add_task_to_db(task_description):
    """PROCESS: Logic/Modification"""
    task_document = {"task": task_description}
    result = tasks_collection.insert_one(task_document)
    return result.inserted_id

def get_all_tasks():
    """STORAGE: Retrieving the collection"""
    return list(tasks_collection.find())
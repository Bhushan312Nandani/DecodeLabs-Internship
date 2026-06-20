from database import add_task_to_db, get_all_tasks

def view_tasks():
    """DISPLAY: The Read Operation"""
    tasks = get_all_tasks()
    if not tasks:
        print("\nNo tasks found in TODO-LIST database.")
        return

    print("\n--- CURRENT TASKS (DB PERSISTED) ---")
    # Professional Polish: Simultaneous access to ID and Value
    for index, doc in enumerate(tasks, start=1):
        print(f"{index}. {doc['task']} (DB_ID: {doc['_id']})")
    print("------------------------------------\n")

def main():
    """The Entry Point"""
    while True:
        print("1. Add Task to MongoDB")
        print("2. View Tasks from MongoDB")
        print("3. Exit")
        choice = input("Select an option: ")

        if choice == '1':
            desc = input("Enter task: ")
            add_task_to_db(desc)
            print("Task saved to permanent storage.")
        elif choice == '2':
            view_tasks()
        elif choice == '3':
            break

if __name__ == "__main__":
    main()
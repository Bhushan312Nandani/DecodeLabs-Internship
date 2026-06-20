from fastapi import FastAPI, HTTPException
from pydantic import BaseModel

# Initialize the FastAPI engine
app = FastAPI()

# MOCK DATABASE
users = []

# DATA VALIDATION (The FastAPI Superpower)
# Unlike Express where we manually checked `if (!incomingData.name)`, 
# FastAPI uses Pydantic models to strictly enforce the JSON contract automatically.
class UserPayload(BaseModel):
    name: str


# GET Route: Retrieve all users
@app.get("/users", status_code=200)
def get_users():
    return {
        "status": "ok",
        "data": users
    }

# POST Route: Create a new user
@app.post("/users", status_code=201)
def create_user(incoming_data: UserPayload):
    # Because of UserPayload, FastAPI already knows 'incoming_data' has a 'name' string.
    # If the user forgets to send it, FastAPI automatically throws a 400 error!
    
    new_user = {
        "id": len(users) + 1,
        "name": incoming_data.name
    }
    users.append(new_user)
    
    return {
        "status": "success",
        "data": new_user
    }


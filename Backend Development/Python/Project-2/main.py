from fastapi import FastAPI, Depends, HTTPException, status
from fastapi.middleware.cors import CORSMiddleware
from sqlalchemy import create_engine, Column, Integer, String, Boolean, DateTime
from sqlalchemy.orm import declarative_base, sessionmaker, Session
from sqlalchemy.exc import IntegrityError
from pydantic import BaseModel
import datetime

# ==========================================
# 1. ORM & DATABASE SETUP (The SQLAlchemy equivalent of Prisma)
# ==========================================
# Using your exact same database URL!
DATABASE_URL = "postgresql://postgres:BhushanN1234%233@localhost:5432/decodelabs_db"

engine = create_engine(DATABASE_URL)
SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)
Base = declarative_base()

# The Database Model
class User(Base):
    __tablename__ = "User"  # Matches the table Prisma already created!
    __table_args__ = {'extend_existing': True}
    
    id = Column(Integer, primary_key=True, index=True)
    email = Column(String(255), unique=True, index=True, nullable=False)
    age = Column(Integer, nullable=False)
    is_active = Column(Boolean, default=True)
    created_at = Column(DateTime, default=datetime.datetime.utcnow)

# ==========================================
# 2. INPUT VALIDATION (Pydantic)
# ==========================================
class UserCreate(BaseModel):
    email: str
    age: int

class UserUpdate(BaseModel):
    email: str = None
    age: int = None
    is_active: bool = None

# ==========================================
# 3. API & GATEKEEPER LOGIC (FastAPI)
# ==========================================
app = FastAPI()

# Allow your HTML file to talk to this API
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Dependency to get the DB session
def get_db():
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()

# --- CREATE (POST) ---
@app.post("/api/users", status_code=status.HTTP_201_CREATED)
def create_user(user: UserCreate, db: Session = Depends(get_db)):
    # Gatekeeper 1: Data Validation
    if user.age < 0:
        raise HTTPException(status_code=400, detail="Validation Failed: Age must be 0 or greater.")
    
    db_user = User(email=user.email, age=user.age)
    db.add(db_user)
    
    try:
        db.commit()
        db.refresh(db_user)
        return db_user
    except IntegrityError:
        db.rollback() # Undo the failed attempt
        # Gatekeeper 2: The 409 Conflict Rule
        raise HTTPException(status_code=409, detail="HTTP 409 Conflict: Email already exists!")

# --- READ (GET) ---
@app.get("/api/users")
def get_users(db: Session = Depends(get_db)):
    return db.query(User).all()

# --- UPDATE (PATCH) ---
@app.patch("/api/users/{user_id}")
def update_user(user_id: int, user_update: UserUpdate, db: Session = Depends(get_db)):
    db_user = db.query(User).filter(User.id == user_id).first()
    if not db_user:
        raise HTTPException(status_code=404, detail="User not found")
    
    if user_update.email is not None: db_user.email = user_update.email
    if user_update.age is not None: db_user.age = user_update.age
    if user_update.is_active is not None: db_user.is_active = user_update.is_active
    
    db.commit()
    db.refresh(db_user)
    return db_user

# --- DELETE (DELETE) ---
@app.delete("/api/users/{user_id}", status_code=status.HTTP_204_NO_CONTENT)
def delete_user(user_id: int, db: Session = Depends(get_db)):
    db_user = db.query(User).filter(User.id == user_id).first()
    if db_user:
        db.delete(db_user)
        db.commit()
    return {"message": "User deleted"}
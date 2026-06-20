from fastapi import FastAPI, HTTPException
from fastapi.middleware.cors import CORSMiddleware # Add this import
from pydantic import BaseModel
from prisma import Prisma
import math

app = FastAPI()

# Add the CORS configuration
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"], # In production, replace "*" with your React app's URL
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

db = Prisma()

# Database Connection Lifecycle
@app.on_event("startup")
async def startup():
    await db.connect()

@app.on_event("shutdown")
async def shutdown():
    await db.disconnect()

# Validation Station
class TransactionRequest(BaseModel):
    amount: float
    description: str
    idempotencyKey: str

class UpdateRequest(BaseModel):
    description: str

@app.post("/api/expenses")
async def add_expense(req: TransactionRequest):
    """THE BANK STRATEGY: Strict PostgreSQL ACID Transactions"""
    if req.amount <= 0:
        raise HTTPException(status_code=400, detail="Invalid Data: Amount must be positive.")

    amount_in_cents = math.floor(req.amount * 100)

    try:
        # We open a strict Postgres transaction block!
        async with db.tx() as transaction:
            
            # Step 1: Isolation/Idempotency Check
            existing_tx = await transaction.transaction.find_unique(
                where={"idempotencyKey": req.idempotencyKey}
            )
            
            if existing_tx:
                raise ValueError("Duplicate request. Transaction already processed.")

            # Step 2: Write to Postgres
            new_record = await transaction.transaction.create(
                data={
                    "amountInCents": amount_in_cents,
                    "description": req.description,
                    "idempotencyKey": req.idempotencyKey,
                    "status": "POSTED"
                }
            )
            
            return new_record

    except ValueError as e:
        raise HTTPException(status_code=409, detail=str(e))
    except Exception as e:
        print(f"POSTGRES ERROR: {e}") 
        raise HTTPException(status_code=500, detail="Transaction failed. State rolled back.")

@app.put("/api/expenses/{tx_id}")
async def update_expense(tx_id: str, req: UpdateRequest):
    """UPDATE: Modifying the description (but keeping the financial amount immutable)"""
    try:
        updated_record = await db.transaction.update(
            where={"id": tx_id},
            data={"description": req.description}
        )
        return updated_record
    except Exception as e:
        raise HTTPException(status_code=404, detail="Transaction not found.")

@app.delete("/api/expenses/{tx_id}")
async def void_expense(tx_id: str):
    """DELETE (Soft Delete): Voiding the transaction to maintain the audit trail"""
    try:
        # We don't delete the row; we just change the status to VOIDED
        voided_record = await db.transaction.update(
            where={"id": tx_id},
            data={"status": "VOIDED"}
        )
        return voided_record
    except Exception as e:
        raise HTTPException(status_code=404, detail="Transaction not found.")

@app.get("/api/ledger")
async def get_ledger():
    """THE ACCUMULATOR PATTERN"""
    transactions = await db.transaction.find_many(
        where={"status": "POSTED"},
        order={"createdAt": "desc"}
    )
    
    total_in_cents = sum(tx.amountInCents for tx in transactions)

    return {
        "transactions": transactions,
        "summary": {"totalSpentCents": total_in_cents}
    }
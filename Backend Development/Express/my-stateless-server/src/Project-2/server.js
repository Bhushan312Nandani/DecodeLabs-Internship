const express = require('express');
const { PrismaClient } = require('@prisma/client');
const { Pool } = require('pg');
const { PrismaPg } = require('@prisma/adapter-pg');
const cors = require('cors');

// 1. Configure the Prisma v7 Driver Adapter
const connectionString = "postgresql://postgres:BhushanN1234%233@localhost:5432/decodelabs_db?schema=public";
const pool = new Pool({ connectionString });
const adapter = new PrismaPg(pool);

// 2. Pass the adapter into the Prisma Client
const prisma = new PrismaClient({ adapter });
const app = express();

app.use(cors());
app.use(express.json());

// --- CREATE (POST) ---
app.post('/api/users', async (req, res) => {
    const { email, age } = req.body;

    // Gatekeeper: Validate age before hitting the database
    if (age < 0) {
        return res.status(400).json({ error: "Validation Failed: Age must be 0 or greater." });
    }

    try {
        const newUser = await prisma.user.create({
            data: { email, age: parseInt(age) }
        });
        res.status(201).json(newUser);
    } catch (error) {
        // ORM Intercept: Catch unique constraint violation (Prisma error code P2002)
        if (error.code === 'P2002') {
            return res.status(409).json({ error: "Conflict: Email already exists in the Vault." });
        }
        res.status(500).json({ error: "Internal Server Error" });
    }
});

// --- READ (GET) ---
app.get('/api/users', async (req, res) => {
    const users = await prisma.user.findMany();
    res.status(200).json(users);
});

// --- UPDATE (PUT/PATCH) ---
app.patch('/api/users/:id', async (req, res) => {
    const { id } = req.params;
    const { email, age, is_active } = req.body;

    try {
        const updatedUser = await prisma.user.update({
            where: { id: parseInt(id) },
            data: { email, age, is_active }
        });
        res.status(200).json(updatedUser);
    } catch (error) {
        res.status(400).json({ error: "Could not update user." });
    }
});

// --- DELETE (DELETE) ---
app.delete('/api/users/:id', async (req, res) => {
    const { id } = req.params;
    try {
        await prisma.user.delete({
            where: { id: parseInt(id) }
        });
        res.status(204).send(); 
    } catch (error) {
        res.status(400).json({ error: "Could not delete user." });
    }
});

app.listen(3000, () => console.log('Memory Vault API running on http://localhost:3000'));
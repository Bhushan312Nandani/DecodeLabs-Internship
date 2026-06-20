import express from 'express';

const app = express();
const PORT = 3000;

// Middleware to parse incoming JSON payloads (The Data Ingress Point)
app.use(express.json());

// Mock Memory Storage (Temporal Lobe)
let users = [
    { id: 1, name: "Martina Plantijn", email: "martina@decodelabs.com" },
    { id: 2, name: "Davu", email: "davu@decodelabs.com" }
];

// --- API ENDPOINTS ---

/**
 * 1. GET /users 
 * Purpose: Retrieval (Safe & Idempotent)
 */
app.get('/users', (req, res) => {
    // Return the current list of users with a 200 OK status
    res.status(200).json(users);
});

/**
 * 2. POST /users
 * Purpose: Creation (Unsafe & Non-idempotent)
 */
app.post('/users', (req, res) => {
    const { name, email } = req.body;

    // --- INPUT VALIDATION ---
    if (!name || !email) {
        return res.status(400).json({ 
            error: "Bad Request", 
            message: "Data validation failed. Both 'name' and 'email' are required fields." 
        });
    }

    // --- PROCESSING & PERSISTENCE ---
    const newUser = {
        id: users.length + 1,
        name: name,
        email: email
    };
    
    users.push(newUser);

    // --- OUTPUT ---
    // Return 21 Created along with the successfully created object
    res.status(201).json({
        message: "User created successfully",
        data: newUser
    });
});

// --- GLOBAL ERROR HANDLING PROTOCOL ---
app.use((req, res) => {
    res.status(404).json({ error: "Not Found", message: "The requested architectural route does not exist." });
});

// Start the network backbone
app.listen(PORT, () => {
    console.log(`🚀 DecodeLabs Backend Engine active on: http://localhost:${PORT}`);
});
require('dotenv').config();
const express = require('express');
const bcrypt = require('bcryptjs');
const jwt = require('jsonwebtoken');
const verifyToken = require('./middleware/auth');

const app = express();
app.use(express.json()); // Allows parsing JSON payloads

// Mock Database Array
const users = [];

// ==========================================
// REQUIREMENT 1: Password Hashing (Register)
// ==========================================
app.post('/api/register', async (req, res) => {
    try {
        const { username, password, role } = req.body;

        // Check if user already exists
        const userExists = users.find(u => u.username === username);
        if (userExists) return res.status(400).json({ message: 'Username taken.' });

        // Generate Salt & Hash with a deliberate work factor (10 rounds)
        const salt = await bcrypt.genSalt(10);
        const hashedPassword = await bcrypt.hash(password, salt);

        // Store the user with the irreversible hashed password
        const newUser = {
            id: Date.now(),
            username,
            password: hashedPassword,
            role: role || 'user' // Default role if none provided
        };
        users.push(newUser);

        res.status(201).json({ message: 'User registered successfully!' });
    } catch (error) {
        res.status(500).json({ message: 'Server error during registration.' });
    }
});

// ==========================================
// REQUIREMENT 2: JWT Generation (Login)
// ==========================================
app.post('/api/login', async (req, res) => {
    try {
        const { username, password } = req.body;

        // Find the user
        const user = users.find(u => u.username === username);
        if (!user) return res.status(400).json({ message: 'Invalid credentials.' });

        // Compare incoming password with the stored hash
        const isMatch = await bcrypt.compare(password, user.password);
        if (!isMatch) return res.status(400).json({ message: 'Invalid credentials.' });

        // Issue the VIP Wristband (JWT) - Expires in 1 hour
        const token = jwt.sign(
            { id: user.id, username: user.username, role: user.role },
            process.env.JWT_SECRET,
            { expiresIn: '1h' }
        );

        // Return the token to the client
        res.status(200).json({ token });
    } catch (error) {
        res.status(500).json({ message: 'Server error during login.' });
    }
});

// ==========================================
// REQUIREMENT 3: Protected Route
// ==========================================
// Setting verifyToken as the bouncer gatekeeper for this route
app.get('/api/protected/dashboard', verifyToken, (req, res) => {
    // Access user context safely attached by the middleware layer
    res.status(200).json({
        message: `Welcome to the secure vault dashboard, ${req.user.username}!`,
        secretData: "This data is stateless, cryptographically secured, and database-lookup free.",
        userContext: req.user
    });
});

// Run the Server
const PORT = process.env.PORT || 3000;
app.listen(PORT, () => console.log(`Vault server securely spinning on port ${PORT}`));
const express = require('express');
const app = express();
const PORT = 3000;

// MIDDLEWARE: This is required to process structured JSON payloads over the HTTP wire.
// Without this, your POST requests will have an undefined body!
app.use(express.json());

// MOCK DATABASE: Since we don't have a real database yet, we will use an array.
// Remember, the server is "stateless" regarding the user's session, but it still 
// acts as the messenger to whatever data store we have here.
const users = [];

// GET Route: Retrieve all users
app.get('/users', (req, res) => {
    // Explicitly return a 200 OK status code and structured JSON
    res.status(200).json({
        status: "ok",
        data: users
    });
});


// POST Route: Create a new user
app.post('/users', (req, res) => {
    // 1. Extract the JSON payload from the request body
    const incomingData = req.body;

    // 2. Validate the data (If they mess up the order, send a 400 Bad Request)
    if (!incomingData || !incomingData.name) {
        return res.status(400).json({
            status: "error",
            message: "Invalid syntax: 'name' is required."
        });
    }

    // 3. Process the order (Add to our mock database)
    const newUser = {
        id: users.length + 1,
        name: incomingData.name
    };
    users.push(newUser);

    // 4. Return a 201 Created status code and the new resource
    res.status(201).json({
        status: "success",
        data: newUser
    });
});


app.listen(PORT, () => {
    console.log(`Stateless server is running on http://localhost:${PORT}`);
});
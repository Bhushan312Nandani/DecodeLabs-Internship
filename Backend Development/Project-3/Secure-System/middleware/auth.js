const jwt = require('jsonwebtoken');

const verifyToken = (req, res, next) => {
    // 1. Check if the token arrives in the Authorization header
    const authHeader = req.headers['authorization'];
    const token = authHeader && authHeader.split(' ')[1]; // Expecting "Bearer <token>"

    if (!token) {
        return res.status(401).json({ message: 'Access Denied: No Token Provided.' });
    }

    try {
        // 2 & 3. Cryptographic Validation (Signature & Expiration Check)
        const verified = jwt.verify(token, process.env.JWT_SECRET);

        // 5. Context Attachment: Attach user payload to the request object
        req.user = verified;

        // Pass control to the next route handler
        next();
    } catch (error) {
        // Catches expired or tampered signatures
        res.status(401).json({ message: 'Invalid or Expired Token.' });
    }
};

module.exports = verifyToken;
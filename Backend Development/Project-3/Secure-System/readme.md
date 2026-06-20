# Secure JWT Authentication API

A Node.js/Express REST API with role-based access control using JWT tokens. Demonstrates middleware-based authentication and authorization patterns.

## What It Does

- User login with JWT token issuance
- Protected routes with middleware JWT verification
- Role-based access: `user` vs `admin` roles
- Secure header-based token passing

## How to Run

```bash
cd Secure-System
npm install
cp .env.example .env      # Fill in your values
node server.js
```

Server starts on `http://localhost:3000`

## API Endpoints

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| `POST` | `/login` | None | Get JWT token |
| `GET` | `/dashboard` | JWT | Protected user route |
| `GET` | `/admin` | JWT + Admin role | Admin-only route |

## Environment Variables

Copy `.env.example` to `.env` and set:

```env
PORT=3000
JWT_SECRET=your_secret_key_here
```

## Project Structure

```
Secure-System/
├── server.js          # Express server + routes
├── middleware/        # JWT auth middleware
├── .env.example       # Environment variable template
└── README.md
```

# Shared Policies

## 1. Security & Privacy
- **Never commit sensitive data:** Do not upload AWS credentials, secret keys, or specific account IDs to the repository.
- **Environment Variables:** Use a `.env` file for local development and ensure it is included in `.gitignore`.
- **Sanitization:** All example configurations must use placeholders like `<YOUR_ACCOUNT_ID>` or `<YOUR_API_ID>`.

## 2. Contribution Guidelines
- Fork the repository and create a feature branch.
- Ensure all Lambda functions have appropriate minimal IAM permissions.
- Write tests for any new endpoints or functions.
- Submit a pull request with a detailed summary of changes.

## 3. License
This project is open-source and available under the MIT License. Feel free to use, modify, and distribute it as per the license terms.

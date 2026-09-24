# Password Reset Setup

The backend supports password reset by email OTP and phone OTP.

## Email OTP
Set these environment variables before starting Spring Boot:
- MAIL_HOST (example: smtp.gmail.com)
- MAIL_PORT (example: 587)
- MAIL_USERNAME
- MAIL_PASSWORD (use an SMTP/app password, not a normal account password when the provider requires an app password)
- MAIL_FROM

## Phone OTP
Phone OTP uses Twilio SMS. Set:
- TWILIO_ACCOUNT_SID
- TWILIO_AUTH_TOKEN
- TWILIO_FROM (your Twilio SMS-capable number)

The application expects Indian 10-digit mobile numbers beginning with 6-9 and sends them as +91XXXXXXXXXX.

## Password reset API flow
1. POST /api/auth/password/forgot/email {"identifier":"user@example.com"}
2. POST /api/auth/password/forgot/phone {"identifier":"9876543210"}
3. POST /api/auth/password/verify {"identifier":"...","otp":"123456"}
4. POST /api/auth/password/reset {"resetToken":"...","newPassword":"newpassword"}

OTP expires in 10 minutes and is limited to 5 verification attempts.

## Validation
- Email must be lowercase only and must match a valid email pattern.
- New phone numbers must be exactly 10 digits and start with 6-9.
- Admin-only document viewing is available through /api/documents?path=...

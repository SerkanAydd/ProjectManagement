# Student Registration API

## Base URL
```
http://localhost:8080/api
```

---

## 📝 Student Registration Endpoints

### Option 1: Two-Step Registration (Recommended)

#### Step 1: Initiate Registration
**POST** `/register_student/initiate`

**Parameters:**
- `mail` (required): Student email
- `name` (required): Student full name
- `faculty` (required): Faculty name
- `department` (required): Department name
- `password` (required): Password
- `studentno` (required): Student number

**Request:**
```bash
curl -X POST http://localhost:8080/api/register_student/initiate \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "mail=john@university.edu&name=John Doe&faculty=Engineering&department=Computer Science&password=password123&studentno=20241001"
```

**Success Response:**
```json
{
  "Successful": "Pending",
  "Message": "Verification code sent to email."
}
```

**Error Response:**
```json
{
  "Successful": "False",
  "Message": "Student number already exists."
}
```

#### Step 2: Confirm Registration
**POST** `/register_student/confirm`

**Parameters:**
- `mail` (required): Same email from step 1
- `code` (required): 6-digit verification code from email

**Request:**
```bash
curl -X POST http://localhost:8080/api/register_student/confirm \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "mail=john@university.edu&code=123456"
```

**Success Response:**
```json
{
  "Successful": "True",
  "Message": "User successfully registered."
}
```

---

### Option 2: Direct Registration (No Email Verification)

**POST** `/register_student`

**Parameters:**
- `mail` (required): Student email
- `name` (required): Student full name
- `faculty` (required): Faculty name
- `department` (required): Department name
- `password` (required): Password
- `studentno` (required): Student number

**Request:**
```bash
curl -X POST http://localhost:8080/api/register_student \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "mail=john@university.edu&name=John Doe&faculty=Engineering&department=Computer Science&password=password123&studentno=20241001"
```

**Success Response:**
```json
{
  "Successful": "True",
  "Message": "User successfully registered."
}
```

**Error Responses:**
```json
{
  "Successful": "False",
  "Message": "Student number already exists."
}
```

```json
{
  "Successful": "False",
  "Message": "User mail is not in database"
}
```

---

## ⚠️ Requirements

1. **Email must be in approved student list** (server-side file: `student_emails.txt`)
2. **Student number must be unique**
3. **All fields are required**

## 🔧 JavaScript Example

```javascript
// Simple registration function
const registerStudent = async (studentData) => {
  const formData = new URLSearchParams();
  Object.keys(studentData).forEach(key => {
    formData.append(key, studentData[key]);
  });

  const response = await fetch('/api/register_student', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded',
    },
    body: formData,
  });

  return await response.json();
};

// Usage
const result = await registerStudent({
  mail: 'john@university.edu',
  name: 'John Doe',
  faculty: 'Engineering',
  department: 'Computer Science',
  password: 'password123',
  studentno: '20241001'
});

if (result.Successful === 'True') {
  console.log('Registration successful');
} else {
  console.log('Error:', result.Message);
}
``` 
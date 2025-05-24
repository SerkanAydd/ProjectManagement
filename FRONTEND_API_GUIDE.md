# Frontend Developer API Guide

This guide covers the API endpoints for **ZIP Transcript Import** and **Student Registration** functionalities.

## Base URL
```
http://localhost:8080/api
```

---

## 📁 ZIP Transcript Import API

### Endpoint: Upload Multiple Transcripts via ZIP

**POST** `/import-transcripts-zip`

Upload a ZIP file containing multiple student transcript PDF files.

#### Request Format

**Content-Type:** `multipart/form-data`

**Parameters:**
- `file` (required): ZIP file containing transcript PDFs

#### Request Example

```javascript
// JavaScript/React Example
const uploadZipTranscripts = async (zipFile) => {
  const formData = new FormData();
  formData.append('file', zipFile);

  try {
    const response = await fetch('/api/import-transcripts-zip', {
      method: 'POST',
      body: formData,
    });

    const result = await response.json();
    return result;
  } catch (error) {
    console.error('Upload failed:', error);
  }
};

// Usage
const handleFileUpload = (event) => {
  const file = event.target.files[0];
  if (file && file.name.endsWith('.zip')) {
    uploadZipTranscripts(file).then(result => {
      console.log('Upload results:', result);
    });
  }
};
```

#### HTML Form Example
```html
<form enctype="multipart/form-data">
  <input type="file" name="file" accept=".zip" required>
  <button type="submit">Upload ZIP Transcripts</button>
</form>
```

#### cURL Example
```bash
curl -X POST http://localhost:8080/api/import-transcripts-zip \
  -F "file=@student_transcripts.zip"
```

#### Success Response (200 OK)

**All files processed successfully:**
```json
{
  "totalFiles": 3,
  "successCount": 3,
  "failureCount": 0,
  "results": [
    {
      "filename": "12345_transcript.pdf",
      "success": true,
      "message": "Successfully imported transcript",
      "studentId": 12345
    },
    {
      "filename": "67890_transcript.pdf",
      "success": true,
      "message": "Successfully imported transcript",
      "studentId": 67890
    },
    {
      "filename": "54321_transcript.pdf",
      "success": true,
      "message": "Successfully imported transcript",
      "studentId": 54321
    }
  ]
}
```

**Partial success (some files failed):**
```json
{
  "totalFiles": 3,
  "successCount": 2,
  "failureCount": 1,
  "results": [
    {
      "filename": "12345_transcript.pdf",
      "success": true,
      "message": "Successfully imported transcript",
      "studentId": 12345
    },
    {
      "filename": "67890_transcript.pdf",
      "success": true,
      "message": "Successfully imported transcript",
      "studentId": 67890
    },
    {
      "filename": "11111_transcript.pdf",
      "success": false,
      "message": "Transcript for student 11111 already exists"
    }
  ]
}
```

#### Error Responses

**400 Bad Request - Invalid ZIP file:**
```json
"Error: Only ZIP files are allowed. Received file type: application/pdf"
```

**400 Bad Request - No valid transcripts:**
```json
"Error: No valid transcript PDF files found in the ZIP archive"
```

**400 Bad Request - All files failed:**
```json
{
  "totalFiles": 2,
  "successCount": 0,
  "failureCount": 2,
  "results": [
    {
      "filename": "invalid_file.pdf",
      "success": false,
      "message": "Failed to parse transcript: Student number not found"
    },
    {
      "filename": "99999_transcript.pdf",
      "success": false,
      "message": "Transcript for student 99999 already exists"
    }
  ]
}
```

**500 Internal Server Error:**
```json
"Unexpected error: Database connection failed"
```

#### ZIP File Requirements

1. **ZIP Format**: Standard ZIP archive
2. **Size Limit**: Maximum 100MB
3. **Content**: PDF files with specific naming pattern
4. **PDF Naming**: `{studentId}_transcript.pdf` (e.g., `12345_transcript.pdf`)
5. **Directory Structure**: PDFs can be in subdirectories within ZIP

#### Frontend Implementation Tips

```javascript
// Validation before upload
const validateZipFile = (file) => {
  if (!file.name.endsWith('.zip')) {
    throw new Error('Please select a ZIP file');
  }
  
  if (file.size > 100 * 1024 * 1024) { // 100MB
    throw new Error('File size must be less than 100MB');
  }
  
  return true;
};

// Progress tracking
const uploadWithProgress = async (zipFile, onProgress) => {
  const formData = new FormData();
  formData.append('file', zipFile);

  return new Promise((resolve, reject) => {
    const xhr = new XMLHttpRequest();
    
    xhr.upload.addEventListener('progress', (e) => {
      if (e.lengthComputable) {
        const percentComplete = (e.loaded / e.total) * 100;
        onProgress(percentComplete);
      }
    });

    xhr.addEventListener('load', () => {
      if (xhr.status === 200) {
        resolve(JSON.parse(xhr.responseText));
      } else {
        reject(new Error(`Upload failed: ${xhr.statusText}`));
      }
    });

    xhr.open('POST', '/api/import-transcripts-zip');
    xhr.send(formData);
  });
};

// Results display helper
const displayResults = (result) => {
  console.log(`📊 Summary: ${result.successCount}/${result.totalFiles} successful`);
  
  result.results.forEach(item => {
    if (item.success) {
      console.log(`✅ ${item.filename}: ${item.message} (Student ID: ${item.studentId})`);
    } else {
      console.error(`❌ ${item.filename}: ${item.message}`);
    }
  });
};
```

---

## 👤 Student Registration API

### Two-Step Registration Process

The student registration uses a **two-step verification process**:
1. **Initiate Registration** - Submit details and receive verification code
2. **Confirm Registration** - Submit verification code to complete registration

### Step 1: Initiate Student Registration

**POST** `/register_student/initiate`

Start the registration process and send verification code to email.

#### Request Format

**Content-Type:** `application/x-www-form-urlencoded`

**Parameters:**
- `mail` (required): Student email address
- `name` (required): Student full name  
- `faculty` (required): Faculty name
- `department` (required): Department name
- `password` (required): Student password
- `studentno` (required): Unique student number

#### Request Example

```javascript
// JavaScript/React Example
const initiateStudentRegistration = async (studentData) => {
  const formData = new URLSearchParams();
  formData.append('mail', studentData.mail);
  formData.append('name', studentData.name);
  formData.append('faculty', studentData.faculty);
  formData.append('department', studentData.department);
  formData.append('password', studentData.password);
  formData.append('studentno', studentData.studentno);

  try {
    const response = await fetch('/api/register_student/initiate', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded',
      },
      body: formData,
    });

    const result = await response.json();
    return result;
  } catch (error) {
    console.error('Registration initiation failed:', error);
  }
};

// Usage
const handleInitiateRegistration = async () => {
  const studentData = {
    mail: 'john.doe@university.edu',
    name: 'John Doe',
    faculty: 'Engineering',
    department: 'Computer Science',
    password: 'securePassword123',
    studentno: '20241001'
  };

  const result = await initiateStudentRegistration(studentData);
  
  if (result.Successful === 'Pending') {
    // Show verification code input form
    showVerificationForm();
  } else {
    // Show error message
    showError(result.Message);
  }
};
```

#### HTML Form Example
```html
<form id="registrationForm">
  <input type="email" name="mail" placeholder="Email" required>
  <input type="text" name="name" placeholder="Full Name" required>
  <input type="text" name="faculty" placeholder="Faculty" required>
  <input type="text" name="department" placeholder="Department" required>
  <input type="password" name="password" placeholder="Password" required>
  <input type="text" name="studentno" placeholder="Student Number" required>
  <button type="submit">Send Verification Code</button>
</form>
```

#### cURL Example
```bash
curl -X POST http://localhost:8080/api/register_student/initiate \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "mail=john.doe@university.edu&name=John Doe&faculty=Engineering&department=Computer Science&password=securePassword123&studentno=20241001"
```

#### Success Response (200 OK)
```json
{
  "Successful": "Pending",
  "Message": "Verification code sent to email."
}
```

#### Error Responses (400 Bad Request)

**Student number already exists:**
```json
{
  "Successful": "False",
  "Message": "Student number already exists."
}
```

**Email not eligible:**
```json
{
  "Successful": "False",
  "Message": "Email not eligible for registration (not in student list)."
}
```

**User already registered:**
```json
{
  "Successful": "False",
  "Message": "User already registered."
}
```

### Step 2: Confirm Student Registration

**POST** `/register_student/confirm`

Complete registration by providing the verification code sent to email.

#### Request Format

**Content-Type:** `application/x-www-form-urlencoded`

**Parameters:**
- `mail` (required): Student email address (same as step 1)
- `code` (required): 6-digit verification code from email

#### Request Example

```javascript
// JavaScript/React Example
const confirmStudentRegistration = async (mail, verificationCode) => {
  const formData = new URLSearchParams();
  formData.append('mail', mail);
  formData.append('code', verificationCode);

  try {
    const response = await fetch('/api/register_student/confirm', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded',
      },
      body: formData,
    });

    const result = await response.json();
    return result;
  } catch (error) {
    console.error('Registration confirmation failed:', error);
  }
};

// Usage
const handleConfirmRegistration = async () => {
  const result = await confirmStudentRegistration(
    'john.doe@university.edu', 
    '123456'
  );
  
  if (result.Successful === 'True') {
    // Registration completed successfully
    showSuccessMessage('Registration completed!');
    redirectToLogin();
  } else {
    // Show error message
    showError(result.Message);
  }
};
```

#### HTML Form Example
```html
<form id="verificationForm">
  <input type="hidden" name="mail" value="john.doe@university.edu">
  <input type="text" name="code" placeholder="6-digit verification code" maxlength="6" required>
  <button type="submit">Complete Registration</button>
</form>
```

#### cURL Example
```bash
curl -X POST http://localhost:8080/api/register_student/confirm \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "mail=john.doe@university.edu&code=123456"
```

#### Success Response (200 OK)
```json
{
  "Successful": "True",
  "Message": "User successfully registered."
}
```

#### Error Responses (400 Bad Request)

**Invalid verification code:**
```json
{
  "Successful": "False",
  "Message": "Invalid or expired verification code."
}
```

**No pending registration:**
```json
{
  "Successful": "False",
  "Message": "No pending registration found."
}
```

**Database error:**
```json
{
  "Successful": "False",
  "Message": "Database error during registration."
}
```

### Complete Registration Flow Example

```javascript
// Complete registration component
class StudentRegistration {
  constructor() {
    this.currentStep = 1;
    this.studentData = {};
  }

  async startRegistration(formData) {
    this.studentData = formData;
    
    try {
      const result = await this.initiateStudentRegistration(formData);
      
      if (result.Successful === 'Pending') {
        this.currentStep = 2;
        this.showVerificationStep();
        this.showMessage('Verification code sent to ' + formData.mail, 'info');
      } else {
        this.showMessage(result.Message, 'error');
      }
    } catch (error) {
      this.showMessage('Registration failed. Please try again.', 'error');
    }
  }

  async completeRegistration(verificationCode) {
    try {
      const result = await this.confirmStudentRegistration(
        this.studentData.mail, 
        verificationCode
      );
      
      if (result.Successful === 'True') {
        this.showMessage('Registration completed successfully!', 'success');
        this.redirectToLogin();
      } else {
        this.showMessage(result.Message, 'error');
      }
    } catch (error) {
      this.showMessage('Verification failed. Please try again.', 'error');
    }
  }

  showVerificationStep() {
    // Hide registration form, show verification form
    document.getElementById('registrationForm').style.display = 'none';
    document.getElementById('verificationForm').style.display = 'block';
  }

  showMessage(message, type) {
    // Display user-friendly messages
    const messageDiv = document.getElementById('message');
    messageDiv.textContent = message;
    messageDiv.className = `message ${type}`;
  }

  redirectToLogin() {
    setTimeout(() => {
      window.location.href = '/login';
    }, 2000);
  }
}
```

### Frontend Validation Guidelines

```javascript
// Input validation helpers
const validateStudentData = (data) => {
  const errors = [];

  // Email validation
  if (!data.mail || !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(data.mail)) {
    errors.push('Valid email address is required');
  }

  // Student number validation
  if (!data.studentno || !/^\d+$/.test(data.studentno)) {
    errors.push('Student number must contain only digits');
  }

  // Password validation
  if (!data.password || data.password.length < 8) {
    errors.push('Password must be at least 8 characters long');
  }

  // Required fields
  ['name', 'faculty', 'department'].forEach(field => {
    if (!data[field] || data[field].trim() === '') {
      errors.push(`${field} is required`);
    }
  });

  return errors;
};

// Verification code validation
const validateVerificationCode = (code) => {
  if (!code || !/^\d{6}$/.test(code)) {
    return 'Verification code must be 6 digits';
  }
  return null;
};
```

---

## 🔧 Common Error Handling

### HTTP Status Codes
- **200 OK**: Request successful
- **400 Bad Request**: Invalid input or business logic error
- **500 Internal Server Error**: Unexpected server error

### Generic Error Response Handler

```javascript
const handleApiResponse = async (response) => {
  if (response.ok) {
    return await response.json();
  }
  
  const errorText = await response.text();
  
  switch (response.status) {
    case 400:
      throw new Error(`Invalid request: ${errorText}`);
    case 500:
      throw new Error('Server error. Please try again later.');
    default:
      throw new Error(`Request failed: ${response.status}`);
  }
};
```

---

## 📝 Integration Checklist

### For ZIP Transcript Upload:
- [ ] File input accepts only `.zip` files
- [ ] File size validation (max 100MB)
- [ ] Progress indicator for large uploads
- [ ] Display detailed results for each transcript
- [ ] Handle partial success scenarios
- [ ] Show user-friendly error messages

### For Student Registration:
- [ ] Two-step form implementation
- [ ] Input validation on frontend
- [ ] Email format validation
- [ ] Password strength requirements
- [ ] Verification code input (6 digits)
- [ ] Step-by-step progress indicator
- [ ] Session management between steps
- [ ] Proper error message display

### General:
- [ ] Loading states for all API calls
- [ ] Timeout handling for slow connections  
- [ ] Retry mechanism for failed requests
- [ ] User feedback for all actions
- [ ] Mobile-responsive forms 
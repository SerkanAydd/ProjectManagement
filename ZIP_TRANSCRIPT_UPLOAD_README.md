# ZIP Transcript Upload Feature

This feature allows you to upload multiple transcript PDF files at once using a ZIP archive.

## New Endpoint

### POST `/api/import-transcripts-zip`

Upload a ZIP file containing multiple transcript PDF files.

**Request:**
- Method: `POST`
- Content-Type: `multipart/form-data`
- Parameter: `file` (ZIP file)

**Response:**
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

## Requirements

### ZIP File Structure
The ZIP file can contain:
- PDF files with naming pattern: `{studentId}_transcript.pdf`
- Files can be in subdirectories within the ZIP
- Non-PDF files and incorrectly named files will be ignored

### Individual PDF Requirements
Each PDF file must:
1. **Filename Format**: `{studentId}_transcript.pdf` (e.g., `12345_transcript.pdf`)
2. **Content Type**: PDF format
3. **Size**: Each file should be under 50MB
4. **Content**: Valid transcript with student information

### ZIP File Requirements
- **Format**: Standard ZIP archive
- **Size**: Maximum 100MB total
- **Content**: Can contain directories and other files (will be filtered)

## Usage Examples

### Curl Example
```bash
curl -X POST http://localhost:8080/api/import-transcripts-zip \
  -F "file=@transcripts.zip" \
  -H "Content-Type: multipart/form-data"
```

### Frontend JavaScript Example
```javascript
const formData = new FormData();
formData.append('file', zipFileInput.files[0]);

fetch('/api/import-transcripts-zip', {
  method: 'POST',
  body: formData
})
.then(response => response.json())
.then(data => {
  console.log('Upload results:', data);
  
  // Show summary
  alert(`Processed ${data.totalFiles} files. 
         Success: ${data.successCount}, 
         Failed: ${data.failureCount}`);
  
  // Show detailed results
  data.results.forEach(result => {
    if (result.success) {
      console.log(`✓ ${result.filename}: ${result.message}`);
    } else {
      console.error(`✗ ${result.filename}: ${result.message}`);
    }
  });
});
```

## Response Status Codes

- **200 OK**: All files processed successfully OR partial success (some files failed)
- **400 Bad Request**: 
  - Invalid ZIP file
  - No valid transcript files found
  - All files failed to process
- **500 Internal Server Error**: Unexpected server error

## Error Handling

The system processes each transcript individually and continues even if some fail. Common failure reasons:

1. **Student already exists**: Transcript for this student ID already in database
2. **Invalid PDF**: Corrupted or unreadable PDF file
3. **Parsing errors**: Transcript format doesn't match expected pattern
4. **Student ID mismatch**: Student ID in filename doesn't match content
5. **Database errors**: Failed to save to database

## Compatibility

- The existing single transcript upload endpoint `/api/import-transcript` remains unchanged
- All validation and parsing logic is consistent between single and batch upload
- Same database structure and storage location

## File Storage

- Extracted PDF files are saved to: `src/main/resources/transcripts/`
- Original ZIP file is not stored, only extracted PDFs
- Directory is created automatically if it doesn't exist

## Performance Considerations

- Large ZIP files (approaching 100MB) may take longer to process
- Processing is sequential, not parallel
- Consider the number of database transactions for large batches
- Memory usage depends on ZIP file size and number of PDFs

## Tips for Success

1. **Prepare ZIP properly**: Ensure all PDFs follow naming convention
2. **Test with small batches**: Start with a few files to verify format
3. **Check results**: Always review the response to see which files succeeded/failed
4. **Handle duplicates**: The system will skip students that already exist
5. **Validate PDFs first**: Ensure individual PDFs work with single upload before batching 
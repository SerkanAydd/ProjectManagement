package store.model;

import java.util.List;
import java.util.ArrayList;

public class BatchTranscriptResult {
    private int totalFiles;
    private int successCount;
    private int failureCount;
    private List<TranscriptProcessResult> results;

    public BatchTranscriptResult() {
        this.results = new ArrayList<>();
    }

    public BatchTranscriptResult(int totalFiles, int successCount, int failureCount, List<TranscriptProcessResult> results) {
        this.totalFiles = totalFiles;
        this.successCount = successCount;
        this.failureCount = failureCount;
        this.results = results;
    }

    public int getTotalFiles() {
        return totalFiles;
    }

    public void setTotalFiles(int totalFiles) {
        this.totalFiles = totalFiles;
    }

    public int getSuccessCount() {
        return successCount;
    }

    public void setSuccessCount(int successCount) {
        this.successCount = successCount;
    }

    public int getFailureCount() {
        return failureCount;
    }

    public void setFailureCount(int failureCount) {
        this.failureCount = failureCount;
    }

    public List<TranscriptProcessResult> getResults() {
        return results;
    }

    public void setResults(List<TranscriptProcessResult> results) {
        this.results = results;
    }

    public void addResult(TranscriptProcessResult result) {
        this.results.add(result);
    }

    public static class TranscriptProcessResult {
        private String filename;
        private boolean success;
        private String message;
        private Integer studentId;

        public TranscriptProcessResult(String filename, boolean success, String message) {
            this.filename = filename;
            this.success = success;
            this.message = message;
        }

        public TranscriptProcessResult(String filename, boolean success, String message, Integer studentId) {
            this.filename = filename;
            this.success = success;
            this.message = message;
            this.studentId = studentId;
        }

        public String getFilename() {
            return filename;
        }

        public void setFilename(String filename) {
            this.filename = filename;
        }

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public Integer getStudentId() {
            return studentId;
        }

        public void setStudentId(Integer studentId) {
            this.studentId = studentId;
        }
    }
} 
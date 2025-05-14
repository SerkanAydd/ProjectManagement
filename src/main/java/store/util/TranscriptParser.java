package store.util;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import store.model.ParsedTranscript;

public class TranscriptParser {

    public static ParsedTranscript parseTranscript(String text) {
        int studentId = extractStudentNumber(text);
        List<String> courseCodes = extractCourseCodes(text);
        int totalCredits = extractTotalCredits(text);
        double gpa = extractGPA(text);
        int semesterCount = countSemesters(text);

        return new ParsedTranscript(studentId, courseCodes, totalCredits, gpa, semesterCount);
    }

    private static int extractStudentNumber(String text) {
        Matcher matcher = Pattern.compile("Öğrenci No\\s*:\\s*(\\d+)").matcher(text);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        }
        throw new IllegalArgumentException("Student number not found");
    }

    private static List<String> extractCourseCodes(String text) {
        Matcher matcher = Pattern.compile("\\b[A-Z]{3,4}\\d{3}\\b").matcher(text);
        Set<String> codes = new LinkedHashSet<>();
        while (matcher.find()) {
            codes.add(matcher.group());
        }
        return new ArrayList<>(codes);
    }

    private static int extractTotalCredits(String text) {
        Matcher matcher = Pattern.compile("Genel\\s*:\\s*\\d+\\s*\\|\\s*\\d+\\s*\\d+\\s*\\|\\s*\\d+\\s*(\\d+)").matcher(text);
        int last = 0;
        while (matcher.find()) {
            last = Integer.parseInt(matcher.group(1));
        }
        return last;
    }

    private static double extractGPA(String text) {
        Matcher matcher = Pattern.compile("Genel\\s*:\\s*.*?-\\s*(\\d,\\d{1,2})").matcher(text);
        String lastGpa = null;
        while (matcher.find()) {
            lastGpa = matcher.group(1);
        }
        if (lastGpa == null) {
            throw new IllegalArgumentException("GPA not found");
        }
        return Double.parseDouble(lastGpa.replace(',', '.'));
    }

    private static int countSemesters(String text) {
        int count = 0;
        Pattern pattern = Pattern.compile("(Güz|Bahar) Dönemi", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            count++;
        }
        return count;
    }
}

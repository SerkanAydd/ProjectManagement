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
        List<String> courseCodes = extractPassedCourseCodes(text);
        int totalCredits = extractTotalCredits(text);
        double gpa = extractGPA(text);
        int semesterCount = countSemesters(text);

        // ✅ DEBUG LOG
        System.out.println("========== Parsed Transcript ==========");
        System.out.println("Student ID     : " + studentId);
        System.out.println("Course Codes   : " + courseCodes);
        System.out.println("Total Credits  : " + totalCredits);
        System.out.println("GPA            : " + gpa);
        System.out.println("Semester Count : " + semesterCount);
        System.out.println("========================================");

        return new ParsedTranscript(studentId, courseCodes, totalCredits, gpa, semesterCount);
    }

    private static int extractStudentNumber(String text) {
        Matcher matcher = Pattern.compile("Öğrenci No\\s*:\\s*(\\d+)").matcher(text);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        }
        throw new IllegalArgumentException("Student number not found");
    }

    private static List<String> extractPassedCourseCodes(String text) {
        String[] lines = text.split("\r?\n");
        Set<String> passedCourses = new LinkedHashSet<>();

        Pattern courseCodePattern = Pattern.compile("([A-Z]{3,4}\\d{3})");
        Pattern gradePattern = Pattern.compile("\\b(AA|BA|BB|CB|CC|DC|DD|S|FD|FF|NA|DZ|FG)\\b");

        // Known kredisiz dersler
        Set<String> nonCreditCourses = Set.of("TURK201", "TURK202", "HIST201", "HIST202", "GCC101");

        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].trim();
            Matcher codeMatcher = courseCodePattern.matcher(line);

            if (codeMatcher.find()) {
                String currentCourseCode = codeMatcher.group(1);
                String grade = null;
                int courseIndex = line.indexOf(currentCourseCode);
                int gradeIndex = -1;

                // Same line grade check
                Matcher gradeMatcher = gradePattern.matcher(line);
                if (gradeMatcher.find()) {
                    grade = gradeMatcher.group(1);
                    gradeIndex = line.indexOf(grade);
                } else if (i + 1 < lines.length) {
                    // Next line grade check
                    String nextLine = lines[i + 1].trim();
                    Matcher nextCodeMatcher = courseCodePattern.matcher(nextLine);
                    if (!nextCodeMatcher.lookingAt()) {
                        Matcher nextGradeMatcher = gradePattern.matcher(nextLine);
                        if (nextGradeMatcher.find()) {
                            grade = nextGradeMatcher.group(1);
                            gradeIndex = nextLine.indexOf(grade);
                            courseIndex = -1; // for isRealGrade logic
                        }
                    }
                }

                if (grade != null) {
                    boolean isRealGrade = gradeIndex > courseIndex;
                    if (!isRealGrade) {
                        System.out.println("⚠️ Skipped false match: " + currentCourseCode + " (grade guess: " + grade + ")");
                        continue;
                    }

                    if (grade.equals("S")) {
                        if (nonCreditCourses.contains(currentCourseCode)) {
                            System.out.println("✅ Passed kredisiz course: " + currentCourseCode + " (grade: S)");
                            passedCourses.add(currentCourseCode);
                        } else {
                            System.out.println("⚠️ Ignored suspicious S grade for: " + currentCourseCode);
                        }
                    } else if (grade.equals("FF") || grade.equals("FD") || grade.equals("NA") || grade.equals("DZ") || grade.equals("FG")) {
                        System.out.println("⚠️ Skipped failed course: " + currentCourseCode + " (grade: " + grade + ")");
                    } else {
                        System.out.println("✅ Passed course: " + currentCourseCode + " (grade: " + grade + ")");
                        passedCourses.add(currentCourseCode);
                    }
                }
            }
        }

        return new ArrayList<>(passedCourses);
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

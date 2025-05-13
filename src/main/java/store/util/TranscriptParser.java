package store.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

public class TranscriptParser {

    public static void main(String[] args) throws IOException {
        File file = new File("src/main/resources/290201051_transcript.pdf"); // path to your PDF
        String text = extractTextFromPdf(file);
        System.out.println("=== Raw Extracted Text ===\n" + text);

        System.out.println("\n=== Parsed Transcript Data ===");
        System.out.println("Student No      : " + extractStudentNumber(text));
        System.out.println("Course Codes    : " + extractCourseCodes(text));
        System.out.println("Total Credits   : " + extractTotalCredits(text));
        System.out.println("GPA (GNO)       : " + extractGPA(text));
        System.out.println("Date            : " + LocalDate.now());
        System.out.println("Semester Number : " + countSemesters(text));
    }

    private static String extractTextFromPdf(File file) throws IOException {
        try (PDDocument document = PDDocument.load(new FileInputStream(file))) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        }
    }

    private static String extractStudentNumber(String text) {
        Matcher matcher = Pattern.compile("Öğrenci No\\s*:\\s*(\\d+)").matcher(text);
        return matcher.find() ? matcher.group(1) : "Not found";
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
            return -1;
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

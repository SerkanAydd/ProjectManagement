package store.service;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import store.entity.Student;
import java.io.File;
import java.io.InputStream;
import java.util.List;

public class PdfGenerator {

    public static boolean createStudentPdf(List<Student> students) {

        String folderName = "diplomas";
        File folder = new File(folderName);
        
        if (folder.exists() && folder.isDirectory()) {
            deleteFolderRecursively(folder);
            System.out.println("Existing folder deleted.");
        } else {
            System.out.println("No existing folder found.");
        }

        String folderNameWithZip = "diplomas.zip";
        File folderWithZip = new File(folderNameWithZip);

        if (folderWithZip.exists()) {
            folderWithZip.delete();
            System.out.println("Existing ZIP file deleted.");
        }


        boolean success = folder.mkdir();   
        if (!success) {
            System.out.println("Does here return ?");
            return false;
        }

        for (Student student : students) {
            String outputPath_ =  "diplomas/" + student.getStudentid() + ".pdf";

            try {
                PdfWriter writer = new PdfWriter(outputPath_);
                PdfDocument pdfDoc = new PdfDocument(writer);
                PageSize customSize = new PageSize(600, 425);
                pdfDoc.setDefaultPageSize(customSize);
                Document document = new Document(pdfDoc);

                InputStream imageStream = PdfGenerator.class.getClassLoader().getResourceAsStream("diploma.png");
                ImageData imageData = ImageDataFactory.create(imageStream.readAllBytes());
                Image background = new Image(imageData);
                background.scaleToFit(PageSize.A4.getWidth(), PageSize.A4.getHeight());
                background.setFixedPosition(0, 0);
                document.add(background);

                String student_name = student.getName();
                int name_length = student_name.length();
                int name_halfLength = name_length / 2;
                Paragraph name = new Paragraph(student_name).setFontSize(16);
                name.setFixedPosition(260 - name_halfLength, 285, 400);  // adjust X, Y, Width
                document.add(name);

                String faculty_name_tr = student.getFaculty();
                if (faculty_name_tr.equals("Engineering")) {
                    faculty_name_tr = "Muhendislik";
                } else if (faculty_name_tr.equals("Science")) {
                    faculty_name_tr = "Bilim";
                } else if (faculty_name_tr.equals("Architect")) {
                    faculty_name_tr = "Mimarlik";
                } else {
                }

                Paragraph faculty = new Paragraph(faculty_name_tr).setFontSize(9);
                faculty.setFixedPosition(248, 242, 400);  // change coordinates accordingly
                document.add(faculty);

                Paragraph faculty_eng = new Paragraph(student.getFaculty()).setFontSize(9);
                faculty_eng.setFixedPosition(267, 220, 400);  // change coordinates accordingly
                document.add(faculty_eng);

                Paragraph faculty_tr = new Paragraph(faculty_name_tr).setFontSize(15);
                faculty_tr.setFixedPosition(248, 198, 400);  // change coordinates accordingly
                document.add(faculty_tr);

                Paragraph faculty_eng2 = new Paragraph(student.getFaculty()).setFontSize(7);
                faculty_eng2.setFixedPosition(255, 178, 400);  // change coordinates accordingly
                document.add(faculty_eng2);

                String title_ = student.getFaculty();
                if (title_.equals("Engineering")) {
                    title_ = "Muhendis";
                } else if (title_.equals("Science")) {
                    title_ = "Bilim Insani";
                } else if (title_.equals("Architect")) {
                    title_ = "Mimar";
                } else {

                }

                Paragraph title = new Paragraph(title_).setFontSize(15);
                title.setFixedPosition(190, 150, 400);  // change coordinates accordingly
                document.add(title);

                document.close();
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        try {
            String zipFileName = "diplomas.zip";
            String command = String.format(
                "powershell Compress-Archive -Path diplomas -DestinationPath %s -Force", zipFileName
            );

            Process zipProcess = Runtime.getRuntime().exec(command);
            zipProcess.waitFor(); // Wait for zipping to complete
            System.out.println("Folder successfully zipped to " + zipFileName);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to zip the folder.");
            return false;
        }       
    }

    public static void deleteFolderRecursively(File folder) {
        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteFolderRecursively(file);
                } else {
                    file.delete();
                }
            }
        }
        folder.delete();
    }
}

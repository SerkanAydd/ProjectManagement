package store.service;

import java.util.List;

import java.util.zip.*;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.io.font.PdfEncodings;

import java.io.*;

import store.entity.Studentt;

public class HonorGenerator {
    public static boolean createHonorCertificates(List<Studentt> studentList) {

        String folderName = "honor_certificates";
        File folder = new File(folderName);
        
        if (folder.exists() && folder.isDirectory()) {
            deleteFolderRecursively(folder);
            System.out.println("Existing folder deleted.");
        } else {
            System.out.println("No existing folder found.");
        }

        String folderNameWithZip = "honor_certificates.zip";
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

        for (Studentt student : studentList) {
            String outputPath_ =  "honor_certificates/" + student.getStudentid() + ".pdf";

            try {
                PdfWriter writer = new PdfWriter(outputPath_);
                PdfDocument pdfDoc = new PdfDocument(writer);
                PageSize customSize = new PageSize(600, 425);
                pdfDoc.setDefaultPageSize(customSize);
                Document document = new Document(pdfDoc);

                InputStream imageStream = HonorGenerator.class.getClassLoader().getResourceAsStream("honor_certificate.png");
                ImageData imageData = ImageDataFactory.create(imageStream.readAllBytes());
                Image background = new Image(imageData);
                background.scaleToFit(PageSize.A4.getWidth(), PageSize.A4.getHeight());
                background.setFixedPosition(0, 0);
                document.add(background);

                String text = "Sayın " + student.getName() + ", " + student.getFaculty() + " Fakültesi " + student.getDepartment() + " Bölümünde göstermiş olduğunuz akademik başarı nedeniyle, İzmir Yüksek Teknoloji Enstitüsü Rektörlüğü Onur Listesi’ne alınmış bulunmaktasınız. Tebrik eder, başarılarınızın devamını dilerim.";
                Paragraph faculty = new Paragraph(text).setFontSize(15);
                faculty.setFixedPosition(100, 150, 400);
                document.add(faculty);
                
                document.close();
                
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }

        }

        boolean successful = zipDiplomasFolder();
        
        return successful;

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

    public static boolean zipDiplomasFolder() {
        String sourceFolder = "honor_certificates";
        String zipFilePath = "honor_certificates.zip";

        File folder = new File(sourceFolder);
        File[] files = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".pdf"));

        if (files == null || files.length == 0) {
            System.out.println("No PDF files found in the honor_certificates folder.");
            return false;
        }

        try (
            FileOutputStream fos = new FileOutputStream(zipFilePath);
            ZipOutputStream zos = new ZipOutputStream(fos)
        ) {
            for (File file : files) {
                try (FileInputStream fis = new FileInputStream(file)) {
                    ZipEntry zipEntry = new ZipEntry(file.getName());
                    zos.putNextEntry(zipEntry);

                    byte[] buffer = new byte[1024];
                    int len;
                    while ((len = fis.read(buffer)) > 0) {
                        zos.write(buffer, 0, len);
                    }

                    zos.closeEntry();
                }
            }

            System.out.println("Successfully zipped PDFs into " + zipFilePath);
            return true;

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Zipping failed.");
            return false;
        }
    }

}

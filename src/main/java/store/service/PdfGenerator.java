package store.service;

import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import store.entity.Student;

import java.util.List;

public class PdfGenerator {

    public static void createStudentPdf(List<Student> students, String outputPath) {

        for (Student student : students) {
            String outputPath_ =  "diplomas/" + student.getStudentid() + ".pdf";

            System.out.println(outputPath_);

            try {
                PageSize customSize = new PageSize(842, 500);
                PdfWriter writer = new PdfWriter(outputPath_);
                PdfDocument pdf = new PdfDocument(writer);
                pdf.setDefaultPageSize(customSize);
                Document document = new Document(pdf);

                Paragraph p = new Paragraph("Türkiye Cumhuriyeti");
                p.setFirstLineIndent(310);
                document.add(p);

                Paragraph p1 = new Paragraph("REPUBLIC OF TURKIYE");
                p1.setFirstLineIndent(300);
                document.add(p1);

                Paragraph p2 = new Paragraph("IZMIR YUKSEK TEKNOLOJI ENSTITUSU");
                p2.setFirstLineIndent(250);
                document.add(p2);

                Paragraph p3 = new Paragraph("IZMIR INSTITUTE OF TECHNOLOGY");
                p3.setFirstLineIndent(255);
                document.add(p3);

                Paragraph p4 = new Paragraph(student.getName());
                p4.setFirstLineIndent(320);
                document.add(p4);

                Paragraph p5 = new Paragraph(student.getFaculty());
                p5.setFirstLineIndent(300);
                document.add(p4);

                Paragraph p6 = new Paragraph(student.getDepartment());
                p6.setFirstLineIndent(320);
                document.add(p6);

                /*
                document.add(new Paragraph("ID: " + student.getStudentid()));
                document.add(new Paragraph("Name: " + student.getName()));
                document.add(new Paragraph("Faculty: " + student.getFaculty()));
                document.add(new Paragraph("Department: " + student.getDepartment()));
                */
                document.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

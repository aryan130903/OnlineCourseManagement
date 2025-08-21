package com.project.onlinecoursemanagement.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.TextAlignment;

import com.project.onlinecoursemanagement.config.cloud.CloudMediaClient;
import com.project.onlinecoursemanagement.dto.CertificateDto;
import com.project.onlinecoursemanagement.model.Certificate;
import com.project.onlinecoursemanagement.repository.CertificateRepository;
import com.project.onlinecoursemanagement.service.CertificateService;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class CertificateServiceImpl implements CertificateService {

    private final Cloudinary cloudinary;
    CertificateRepository certificateRepository;

    public CertificateServiceImpl(CloudMediaClient mediaClient,CertificateRepository certificateRepository) {
        this.cloudinary = (Cloudinary) mediaClient.getClient();
        this.certificateRepository=certificateRepository;
    }

    public String generateCertificate(String studentName, String studentEmail, String courseName, double score) throws Exception {
        // --- Create PDF in memory ---
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(outputStream);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf, PageSize.A4);

        float pageWidth = PageSize.A4.getWidth();
        float pageHeight = PageSize.A4.getHeight();

        // --- Load template ---
        String templatePath = getClass().getClassLoader().getResource("templates/certificate_template.png").getPath();
        Image template = new Image(ImageDataFactory.create(templatePath));

        // Scale template to page width, maintain aspect ratio
        float templateOriginalWidth = template.getImageScaledWidth();
        float templateOriginalHeight = template.getImageScaledHeight();
        float scaleFactor = pageWidth / templateOriginalWidth;
        float templateWidth = pageWidth;
        float templateHeight = templateOriginalHeight * scaleFactor;

        // Center vertically
        float templateY = (pageHeight - templateHeight) / 2;
        template.scaleToFit(templateWidth, templateHeight);
        template.setFixedPosition(0, templateY);

        document.add(template);

        // --- Fonts ---
        PdfFont titleFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
        PdfFont textFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);

        // --- Absolute positions ---
        float textYPosition = pageHeight - 350; // "Certificate of Completion" Y position
        float lineSpacing = 40;

        // --- Title ---
        document.add(new Paragraph("Certificate of Completion")
                .setFont(titleFont)
                .setFontSize(36)
                .setFontColor(ColorConstants.DARK_GRAY)
                .setTextAlignment(TextAlignment.CENTER)
                .setFixedPosition(0, textYPosition, pageWidth));

        // --- Subtitle ---
        textYPosition -= lineSpacing * 1.5;
        document.add(new Paragraph("This is to certify that")
                .setFont(textFont)
                .setFontSize(18)
                .setTextAlignment(TextAlignment.CENTER)
                .setFixedPosition(0, textYPosition, pageWidth));

        // --- Student Name ---
        textYPosition -= lineSpacing;
        document.add(new Paragraph(studentName)
                .setFont(titleFont)
                .setFontSize(28)
                .setTextAlignment(TextAlignment.CENTER)
                .setFixedPosition(0, textYPosition, pageWidth));

        // --- Course completion text ---
        textYPosition -= lineSpacing;
        document.add(new Paragraph("has successfully completed the course")
                .setFont(textFont)
                .setFontSize(18)
                .setTextAlignment(TextAlignment.CENTER)
                .setFixedPosition(0, textYPosition, pageWidth));

        // --- Course Name ---
        textYPosition -= lineSpacing;
        document.add(new Paragraph(courseName)
                .setFont(titleFont)
                .setFontSize(24)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER)
                .setFixedPosition(0, textYPosition, pageWidth));

        // --- Score ---
        textYPosition -= lineSpacing;
        document.add(new Paragraph("with a score of " + score + "%")
                .setFont(textFont)
                .setFontSize(18)
                .setTextAlignment(TextAlignment.CENTER)
                .setFixedPosition(0, textYPosition, pageWidth));

        // --- Date ---
        document.add(new Paragraph("Date: " + LocalDate.now())
                .setFont(textFont)
                .setFontSize(14)
                .setTextAlignment(TextAlignment.CENTER)
                .setFixedPosition(0, 150, pageWidth));

        // --- Optional signature ---
        try {
            String signPath = getClass().getClassLoader().getResource("templates/signaturewbg.png").getPath();
            Image signature = new Image(ImageDataFactory.create(signPath));
            float signatureWidth = 180;
            float signatureHeight = 95;
            float signatureX = pageWidth - signatureWidth ;
            float signatureY = 150;
            signature.setFixedPosition(signatureX, signatureY);
            signature.scaleToFit(signatureWidth, signatureHeight);
            document.add(signature);
        } catch (Exception ignored) {}

        document.close();

        // --- Upload PDF to Cloudinary ---
        String folderName = "certificates/" + studentEmail.replaceAll("[^a-zA-Z0-9]", "_") + "/" + LocalDate.now();
        String publicId = folderName + "/" + studentName.replaceAll(" ", "_") + "_" + courseName.replaceAll(" ", "_") + ".pdf";

        byte[] pdfBytes = outputStream.toByteArray();

        Map<String, Object> uploadResult = cloudinary.uploader().upload(
                pdfBytes,
                ObjectUtils.asMap(
                        "resource_type", "raw",
                        "public_id", publicId,
                        "format", "pdf"
                )
        );

        return uploadResult.get("secure_url").toString();
    }


    public List<CertificateDto> getCertificatesForStudent(String studentEmail) {
        List<Certificate> certificates = certificateRepository.findAllByStudentEmail(studentEmail);

        return certificates.stream().map(c -> {
            CertificateDto dto = new CertificateDto();
            dto.setCourseName(c.getCourseName());
            dto.setFileUrl(c.getFilePath()); // Cloudinary PDF URL
            dto.setIssueDate(c.getIssueDate());
            return dto;
        }).toList();
    }
}

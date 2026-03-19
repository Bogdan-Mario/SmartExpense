package com.portfolio.smartexpense.services;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.InputStream;

@Service
public class PdfService {

    public String extractTextFromPdf(MultipartFile file) {
        // We use a try-with-resources block so Java automatically closes the file when it's done!
        try (InputStream inputStream = file.getInputStream();
             PDDocument document = PDDocument.load(inputStream)) {

            PDFTextStripper pdfStripper = new PDFTextStripper();
            return pdfStripper.getText(document);

        } catch (Exception e) {
            System.out.println("PDF Error: " + e.getMessage());
            return "Error reading PDF.";
        }
    }
}
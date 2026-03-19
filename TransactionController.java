package com.portfolio.smartexpense.controllers;
import com.portfolio.smartexpense.models.CategoryStats;

import com.portfolio.smartexpense.models.Transaction;
import com.portfolio.smartexpense.services.PdfService;
import com.portfolio.smartexpense.services.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private PdfService pdfService; // <-- Added your new PDF worker here!

    @GetMapping
    public List<Transaction> getAllTransactions() {
        return transactionService.getAllTransactions();
    }

    @PostMapping
    public Transaction createTransaction(@RequestBody Transaction transaction) {
        // Hand the transaction to the Service to be cleaned and saved
        return transactionService.processAndSave(transaction);
    }

    // NEW ENDPOINT: Uploading a PDF file
    @PostMapping("/upload")
    public String uploadBankStatement(@RequestParam("file") MultipartFile file) {

        // 1. Get the raw text from the PDF
        String rawPdfText = pdfService.extractTextFromPdf(file);

        // 2. Hand it to the TransactionService to parse, AI categorize, and save!
        int savedCount = transactionService.parseAndSavePdfText(rawPdfText);

        return "SUCCESS! Read PDF and saved " + savedCount + " transactions to the database.";
    }

    // NEW ENDPOINT: Get spending statistics
    @GetMapping("/stats")
    public List<CategoryStats> getStatistics() {
        return transactionService.getCategoryStatistics();
    }
}
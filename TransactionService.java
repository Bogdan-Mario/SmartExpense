package com.portfolio.smartexpense.services;
import com.portfolio.smartexpense.models.CategoryStats;

import com.portfolio.smartexpense.models.Transaction;
import com.portfolio.smartexpense.repositories.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AiService aiService;

    public Transaction processAndSave(Transaction transaction) {

        String raw = transaction.getRawDescription().toUpperCase();

        if (raw.contains("NETFLIX")) {
            transaction.setCleanName("Netflix");
            transaction.setCategory("Entertainment");
        }
        else if (raw.contains("AMZN")) {
            transaction.setCleanName("Amazon");
            transaction.setCategory("Shopping");
        }
        else {
            String aiResponse = aiService.askAi(transaction.getRawDescription());

            String[] parts = aiResponse.split(",");

            if (parts.length >= 2) {
                transaction.setCleanName(parts[0].trim());
                transaction.setCategory(parts[1].trim());
            } else {
                transaction.setCleanName("Unknown AI response");
                transaction.setCategory("General");
            }
        }

        return transactionRepository.save(transaction);
    }

    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    // This reads the giant PDF text, chops it up, and sends it to the AI
    public int parseAndSavePdfText(String pdfText) {
        int successfulSaves = 0;

        // 1. Split the giant text into an array of individual lines
        String[] lines = pdfText.split("\\r?\\n");

        for (String line : lines) {
            // 2. Check if the line starts with a date pattern (like 03/01/2026)
            // \d{2} means "two digits". So this looks for: 2 digits / 2 digits / 4 digits
            if (line.trim().matches("^\\d{2}/\\d{2}/\\d{4}.*")) {
                try {
                    // Extract Date: "03/01/2026"
                    String rawDate = line.substring(0, 10);
                    // Reformat to standard database format: "2026-03-01"
                    String dbDate = rawDate.substring(6, 10) + "-" + rawDate.substring(0, 2) + "-" + rawDate.substring(3, 5);

                    // Extract Amount: Find the '$' and grab everything after it
                    int dollarIndex = line.lastIndexOf('$');
                    String amountStr = line.substring(dollarIndex + 1).trim();
                    double amount = Double.parseDouble(amountStr);

                    // Extract Description: Grab everything between the Date and the '$'
                    String description = line.substring(10, dollarIndex).trim();

                    // 3. Create a new Transaction object
                    Transaction t = new Transaction();
                    t.setDate(LocalDate.parse(dbDate));
                    t.setAmount(amount);
                    t.setRawDescription(description);

                    // 4. Send it through our existing AI pipeline!
                    processAndSave(t);
                    successfulSaves++;

                } catch (Exception e) {
                    System.out.println("Could not parse this line: " + line);
                }
            }
        }
        return successfulSaves; // Return how many we successfully saved
    }

    // Calculates total spent and percentages per category
    public List<CategoryStats> getCategoryStatistics() {
        List<Transaction> allTransactions = transactionRepository.findAll();

        if (allTransactions.isEmpty()) {
            return new ArrayList<>(); // Return empty if no data
        }

        // 1. Find the Grand Total of all expenses
        double grandTotal = 0;
        for (Transaction t : allTransactions) {
            grandTotal += t.getAmount();
        }

        // 2. Group by Category and add up the amounts using a HashMap
        Map<String, Double> categoryTotals = new HashMap<>();
        for (Transaction t : allTransactions) {
            String cat = t.getCategory();
            if (cat == null) cat = "Uncategorized"; // Safety check

            // If category exists, add to it. If not, start it at 0.
            double currentTotal = categoryTotals.getOrDefault(cat, 0.0);
            categoryTotals.put(cat, currentTotal + t.getAmount());
        }

        // 3. Calculate Percentages and build our final list
        List<CategoryStats> statsList = new ArrayList<>();
        for (Map.Entry<String, Double> entry : categoryTotals.entrySet()) {
            String categoryName = entry.getKey();
            double amountSpent = entry.getValue();

            // The Math: (Part / Whole) * 100
            double percentage = (amountSpent / grandTotal) * 100;

            statsList.add(new CategoryStats(categoryName, amountSpent, percentage));
        }

        return statsList;
    }
}
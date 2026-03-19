SmartExpense
SmartExpense is a Spring Boot-based backend application designed to automate personal finance management. It allows users to track transactions by uploading bank statements in PDF format, which are then parsed and automatically categorized using AI.

Features
PDF Statement Parsing: Automatically extracts text from uploaded bank statement PDF files using Apache PDFBox.

AI-Powered Categorization: Utilizes the Google Gemini API to analyze raw transaction descriptions, generating clean merchant names and assigning spending categories (e.g., Food, Shopping, Utilities).

Rule-Based Processing: Includes a logic layer to immediately recognize and categorize known vendors like Netflix and Amazon.

Spending Analytics: Provides an endpoint to retrieve spending statistics, including total amounts per category and their respective percentage of total spending.

RESTful API: Exposes endpoints for manual transaction creation, viewing all transactions, and uploading files.

Data Persistence: Uses Spring Data JPA to store and manage transaction records in a database.

Tech Stack
Framework: Spring Boot

Language: Java

PDF Processing: Apache PDFBox

AI Integration: Google Gemini API

Database: Spring Data JPA (compatible with H2, MySQL, PostgreSQL, etc.)

JSON Handling: Jackson

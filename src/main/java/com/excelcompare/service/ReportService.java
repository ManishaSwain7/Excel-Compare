package com.excelcompare.service;

import com.excelcompare.dto.ComparisonResult;
import com.excelcompare.dto.ComparisonResult.DocumentMismatch;
import com.excelcompare.model.DocumentMetadata;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

@Service
public class ReportService {

    public byte[] generateCsvReport(ComparisonResult result) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try (OutputStreamWriter writer = new OutputStreamWriter(outputStream);
             CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT)) {

            // Write summary statistics
            csvPrinter.printRecord("COMPARISON SUMMARY");
            csvPrinter.printRecord("Total Documents in File A", result.getStatistics().getTotalDocumentsA());
            csvPrinter.printRecord("Total Documents in File B", result.getStatistics().getTotalDocumentsB());
            csvPrinter.printRecord("Successful Migrations", result.getStatistics().getSuccessfulMigrations());
            csvPrinter.printRecord("Failed Migrations", result.getStatistics().getFailedMigrations());
            csvPrinter.printRecord("Metadata Mismatches", result.getStatistics().getMetadataMismatches());
            csvPrinter.printRecord("Unmatched Documents", result.getStatistics().getUnmatchedDocuments());
            csvPrinter.printRecord("");

            // Write mismatch type counts
            csvPrinter.printRecord("MISMATCH TYPES");
            result.getStatistics().getMismatchTypesCounts().forEach((field, count) -> {
                try {
                    csvPrinter.printRecord(field, count);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            csvPrinter.printRecord("");

            // Write detailed mismatches
            csvPrinter.printRecord("DETAILED MISMATCHES");
            csvPrinter.printRecord("Policy No", "Field Name", "Value in File A", "Value in File B");
            for (DocumentMismatch mismatch : result.getMetadataMismatches()) {
                csvPrinter.printRecord(
                        mismatch.getPolicyNo(),
                        mismatch.getFieldName(),
                        mismatch.getValueA(),
                        mismatch.getValueB()
                );
            }
            csvPrinter.printRecord("");

            // Write missing documents
            csvPrinter.printRecord("DOCUMENTS MISSING IN FILE B (POST-MIGRATION)");
            csvPrinter.printRecord("Policy No", "Document Name", "Claim No", "Status");
            for (DocumentMetadata doc : result.getMissingInB()) {
                csvPrinter.printRecord(
                        doc.getPolicyNo(),
                        doc.getDocumentName(),
                        doc.getClaimNo(),
                        doc.getStatus()
                );
            }
            csvPrinter.printRecord("");

            csvPrinter.printRecord("DOCUMENTS MISSING IN FILE A (PRE-MIGRATION)");
            csvPrinter.printRecord("Policy No", "Document Name", "Claim No", "Status");
            for (DocumentMetadata doc : result.getMissingInA()) {
                csvPrinter.printRecord(
                        doc.getPolicyNo(),
                        doc.getDocumentName(),
                        doc.getClaimNo(),
                        doc.getStatus()
                );
            }

            csvPrinter.flush();
        }

        return outputStream.toByteArray();
    }

    public byte[] generateDetailedCsvReport(List<DocumentMismatch> mismatches) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try (OutputStreamWriter writer = new OutputStreamWriter(outputStream);
             CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT)) {

            // Write header
            csvPrinter.printRecord(
                    "Policy No", "Field Name", "Value Before Migration", "Value After Migration",
                    "Document Name", "Claim No", "Document Type", "Migration Status"
            );

            // Write mismatch details
            for (DocumentMismatch mismatch : mismatches) {
                csvPrinter.printRecord(
                        mismatch.getPolicyNo(),
                        mismatch.getFieldName(),
                        mismatch.getValueA(),
                        mismatch.getValueB(),
                        mismatch.getDocumentA().getDocumentName(),
                        mismatch.getDocumentA().getClaimNo(),
                        mismatch.getDocumentA().getDocumentType(),
                        mismatch.getDocumentB().getMigration()
                );
            }

            csvPrinter.flush();
        }

        return outputStream.toByteArray();
    }

    public String generateHtmlSummary(ComparisonResult result) {
        StringBuilder html = new StringBuilder();
        
        html.append("<html><head><title>Excel Comparison Report</title>");
        html.append("<style>");
        html.append("body { font-family: Arial, sans-serif; margin: 20px; }");
        html.append("table { border-collapse: collapse; width: 100%; margin: 10px 0; }");
        html.append("th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }");
        html.append("th { background-color: #f2f2f2; }");
        html.append(".summary { background-color: #f9f9f9; padding: 15px; margin: 10px 0; }");
        html.append("</style></head><body>");
        
        html.append("<h1>Excel Comparison Report</h1>");
        
        // Summary section
        html.append("<div class='summary'>");
        html.append("<h2>Summary Statistics</h2>");
        html.append("<p><strong>Total Documents in Pre-Migration File:</strong> ")
            .append(result.getStatistics().getTotalDocumentsA()).append("</p>");
        html.append("<p><strong>Total Documents in Post-Migration File:</strong> ")
            .append(result.getStatistics().getTotalDocumentsB()).append("</p>");
        html.append("<p><strong>Successful Migrations:</strong> ")
            .append(result.getStatistics().getSuccessfulMigrations()).append("</p>");
        html.append("<p><strong>Failed Migrations:</strong> ")
            .append(result.getStatistics().getFailedMigrations()).append("</p>");
        html.append("<p><strong>Metadata Mismatches:</strong> ")
            .append(result.getStatistics().getMetadataMismatches()).append("</p>");
        html.append("<p><strong>Unmatched Documents:</strong> ")
            .append(result.getStatistics().getUnmatchedDocuments()).append("</p>");
        html.append("</div>");
        
        // Mismatch types
        if (!result.getStatistics().getMismatchTypesCounts().isEmpty()) {
            html.append("<h2>Mismatch Types</h2>");
            html.append("<table>");
            html.append("<tr><th>Field Name</th><th>Count</th></tr>");
            result.getStatistics().getMismatchTypesCounts().forEach((field, count) -> {
                html.append("<tr><td>").append(field).append("</td><td>").append(count).append("</td></tr>");
            });
            html.append("</table>");
        }
        
        html.append("</body></html>");
        
        return html.toString();
    }
}
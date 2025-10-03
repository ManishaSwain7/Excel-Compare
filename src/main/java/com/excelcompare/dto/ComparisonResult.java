package com.excelcompare.dto;

import com.excelcompare.model.DocumentMetadata;
import java.util.List;
import java.util.Map;

public class ComparisonResult {
    private ComparisonStatistics statistics;
    private List<DocumentMetadata> missingInB;
    private List<DocumentMetadata> missingInA;
    private List<DocumentMismatch> metadataMismatches;
    private List<DocumentMetadata> matchedDocuments;

    public ComparisonResult() {}

    public ComparisonResult(ComparisonStatistics statistics, List<DocumentMetadata> missingInB,
                          List<DocumentMetadata> missingInA, List<DocumentMismatch> metadataMismatches,
                          List<DocumentMetadata> matchedDocuments) {
        this.statistics = statistics;
        this.missingInB = missingInB;
        this.missingInA = missingInA;
        this.metadataMismatches = metadataMismatches;
        this.matchedDocuments = matchedDocuments;
    }

    // Getters and Setters
    public ComparisonStatistics getStatistics() { return statistics; }
    public void setStatistics(ComparisonStatistics statistics) { this.statistics = statistics; }

    public List<DocumentMetadata> getMissingInB() { return missingInB; }
    public void setMissingInB(List<DocumentMetadata> missingInB) { this.missingInB = missingInB; }

    public List<DocumentMetadata> getMissingInA() { return missingInA; }
    public void setMissingInA(List<DocumentMetadata> missingInA) { this.missingInA = missingInA; }

    public List<DocumentMismatch> getMetadataMismatches() { return metadataMismatches; }
    public void setMetadataMismatches(List<DocumentMismatch> metadataMismatches) { this.metadataMismatches = metadataMismatches; }

    public List<DocumentMetadata> getMatchedDocuments() { return matchedDocuments; }
    public void setMatchedDocuments(List<DocumentMetadata> matchedDocuments) { this.matchedDocuments = matchedDocuments; }

    public static class ComparisonStatistics {
        private int totalDocumentsA;
        private int totalDocumentsB;
        private int successfulMigrations;
        private int failedMigrations;
        private int metadataMismatches;
        private int unmatchedDocuments;
        private Map<String, Integer> mismatchTypesCounts;

        public ComparisonStatistics() {}

        public ComparisonStatistics(int totalDocumentsA, int totalDocumentsB, int successfulMigrations,
                                  int failedMigrations, int metadataMismatches, int unmatchedDocuments,
                                  Map<String, Integer> mismatchTypesCounts) {
            this.totalDocumentsA = totalDocumentsA;
            this.totalDocumentsB = totalDocumentsB;
            this.successfulMigrations = successfulMigrations;
            this.failedMigrations = failedMigrations;
            this.metadataMismatches = metadataMismatches;
            this.unmatchedDocuments = unmatchedDocuments;
            this.mismatchTypesCounts = mismatchTypesCounts;
        }

        // Getters and Setters
        public int getTotalDocumentsA() { return totalDocumentsA; }
        public void setTotalDocumentsA(int totalDocumentsA) { this.totalDocumentsA = totalDocumentsA; }

        public int getTotalDocumentsB() { return totalDocumentsB; }
        public void setTotalDocumentsB(int totalDocumentsB) { this.totalDocumentsB = totalDocumentsB; }

        public int getSuccessfulMigrations() { return successfulMigrations; }
        public void setSuccessfulMigrations(int successfulMigrations) { this.successfulMigrations = successfulMigrations; }

        public int getFailedMigrations() { return failedMigrations; }
        public void setFailedMigrations(int failedMigrations) { this.failedMigrations = failedMigrations; }

        public int getMetadataMismatches() { return metadataMismatches; }
        public void setMetadataMismatches(int metadataMismatches) { this.metadataMismatches = metadataMismatches; }

        public int getUnmatchedDocuments() { return unmatchedDocuments; }
        public void setUnmatchedDocuments(int unmatchedDocuments) { this.unmatchedDocuments = unmatchedDocuments; }

        public Map<String, Integer> getMismatchTypesCounts() { return mismatchTypesCounts; }
        public void setMismatchTypesCounts(Map<String, Integer> mismatchTypesCounts) { this.mismatchTypesCounts = mismatchTypesCounts; }
    }

    public static class DocumentMismatch {
        private String policyNo;
        private String fieldName;
        private String valueA;
        private String valueB;
        private DocumentMetadata documentA;
        private DocumentMetadata documentB;

        public DocumentMismatch() {}

        public DocumentMismatch(String policyNo, String fieldName, String valueA, String valueB,
                              DocumentMetadata documentA, DocumentMetadata documentB) {
            this.policyNo = policyNo;
            this.fieldName = fieldName;
            this.valueA = valueA;
            this.valueB = valueB;
            this.documentA = documentA;
            this.documentB = documentB;
        }

        // Getters and Setters
        public String getPolicyNo() { return policyNo; }
        public void setPolicyNo(String policyNo) { this.policyNo = policyNo; }

        public String getFieldName() { return fieldName; }
        public void setFieldName(String fieldName) { this.fieldName = fieldName; }

        public String getValueA() { return valueA; }
        public void setValueA(String valueA) { this.valueA = valueA; }

        public String getValueB() { return valueB; }
        public void setValueB(String valueB) { this.valueB = valueB; }

        public DocumentMetadata getDocumentA() { return documentA; }
        public void setDocumentA(DocumentMetadata documentA) { this.documentA = documentA; }

        public DocumentMetadata getDocumentB() { return documentB; }
        public void setDocumentB(DocumentMetadata documentB) { this.documentB = documentB; }
    }
}
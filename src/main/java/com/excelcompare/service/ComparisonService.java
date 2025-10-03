package com.excelcompare.service;

import com.excelcompare.dto.ComparisonResult;
import com.excelcompare.dto.ComparisonResult.ComparisonStatistics;
import com.excelcompare.dto.ComparisonResult.DocumentMismatch;
import com.excelcompare.model.DocumentMetadata;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ComparisonService {

    public ComparisonResult compareDocuments(List<DocumentMetadata> documentsA, List<DocumentMetadata> documentsB) {
        // Create maps for quick lookup by policy number
        Map<String, DocumentMetadata> mapA = documentsA.stream()
                .collect(Collectors.toMap(DocumentMetadata::getPolicyNo, doc -> doc, (existing, replacement) -> existing));
        
        Map<String, DocumentMetadata> mapB = documentsB.stream()
                .collect(Collectors.toMap(DocumentMetadata::getPolicyNo, doc -> doc, (existing, replacement) -> existing));

        // Find missing documents
        List<DocumentMetadata> missingInB = findMissingDocuments(mapA, mapB);
        List<DocumentMetadata> missingInA = findMissingDocuments(mapB, mapA);

        // Find metadata mismatches for common documents
        List<DocumentMismatch> metadataMismatches = new ArrayList<>();
        List<DocumentMetadata> matchedDocuments = new ArrayList<>();

        for (String policyNo : mapA.keySet()) {
            if (mapB.containsKey(policyNo)) {
                DocumentMetadata docA = mapA.get(policyNo);
                DocumentMetadata docB = mapB.get(policyNo);
                
                List<DocumentMismatch> mismatches = compareDocumentFields(docA, docB);
                metadataMismatches.addAll(mismatches);
                
                if (mismatches.isEmpty()) {
                    matchedDocuments.add(docA);
                }
            }
        }

        // Calculate statistics
        ComparisonStatistics statistics = calculateStatistics(documentsA, documentsB, 
                missingInA, missingInB, metadataMismatches, matchedDocuments);

        return new ComparisonResult(statistics, missingInB, missingInA, metadataMismatches, matchedDocuments);
    }

    private List<DocumentMetadata> findMissingDocuments(Map<String, DocumentMetadata> sourceMap, 
                                                       Map<String, DocumentMetadata> targetMap) {
        return sourceMap.keySet().stream()
                .filter(policyNo -> !targetMap.containsKey(policyNo))
                .map(sourceMap::get)
                .collect(Collectors.toList());
    }

    private List<DocumentMismatch> compareDocumentFields(DocumentMetadata docA, DocumentMetadata docB) {
        List<DocumentMismatch> mismatches = new ArrayList<>();
        String policyNo = docA.getPolicyNo();

        // Compare each field
        compareField(mismatches, policyNo, "documentLocation", docA.getDocumentLocation(), docB.getDocumentLocation(), docA, docB);
        compareField(mismatches, policyNo, "folderHierarchy", docA.getFolderHierarchy(), docB.getFolderHierarchy(), docA, docB);
        compareField(mismatches, policyNo, "documentName", docA.getDocumentName(), docB.getDocumentName(), docA, docB);
        compareField(mismatches, policyNo, "claimNo", docA.getClaimNo(), docB.getClaimNo(), docA, docB);
        compareField(mismatches, policyNo, "claimant", docA.getClaimant(), docB.getClaimant(), docA, docB);
        compareField(mismatches, policyNo, "dateOfLoss", docA.getDateOfLoss(), docB.getDateOfLoss(), docA, docB);
        compareField(mismatches, policyNo, "adjustorName", docA.getAdjustorName(), docB.getAdjustorName(), docA, docB);
        compareField(mismatches, policyNo, "documentType", docA.getDocumentType(), docB.getDocumentType(), docA, docB);
        compareField(mismatches, policyNo, "securityType", docA.getSecurityType(), docB.getSecurityType(), docA, docB);
        compareField(mismatches, policyNo, "status", docA.getStatus(), docB.getStatus(), docA, docB);
        compareField(mismatches, policyNo, "draft", docA.getDraft(), docB.getDraft(), docA, docB);
        compareField(mismatches, policyNo, "migration", docA.getMigration(), docB.getMigration(), docA, docB);

        return mismatches;
    }

    private void compareField(List<DocumentMismatch> mismatches, String policyNo, String fieldName, 
                            String valueA, String valueB, DocumentMetadata docA, DocumentMetadata docB) {
        // Normalize values for comparison (handle null, empty, and whitespace)
        String normalizedA = normalizeValue(valueA);
        String normalizedB = normalizeValue(valueB);

        if (!normalizedA.equals(normalizedB)) {
            mismatches.add(new DocumentMismatch(policyNo, fieldName, valueA, valueB, docA, docB));
        }
    }

    private String normalizeValue(String value) {
        return value == null ? "" : value.trim();
    }

    private ComparisonStatistics calculateStatistics(List<DocumentMetadata> documentsA, 
                                                   List<DocumentMetadata> documentsB,
                                                   List<DocumentMetadata> missingInA,
                                                   List<DocumentMetadata> missingInB,
                                                   List<DocumentMismatch> metadataMismatches,
                                                   List<DocumentMetadata> matchedDocuments) {
        
        // Count successful and failed migrations based on Migration field
        int successfulMigrations = (int) documentsB.stream()
                .filter(doc -> "Y".equalsIgnoreCase(doc.getMigration()))
                .count();
        
        int failedMigrations = (int) documentsB.stream()
                .filter(doc -> !"Y".equalsIgnoreCase(doc.getMigration()))
                .count();

        // Count mismatch types
        Map<String, Integer> mismatchTypesCounts = metadataMismatches.stream()
                .collect(Collectors.groupingBy(
                    DocumentMismatch::getFieldName,
                    Collectors.collectingAndThen(Collectors.counting(), Math::toIntExact)
                ));

        int unmatchedDocuments = missingInA.size() + missingInB.size();

        return new ComparisonStatistics(
                documentsA.size(),
                documentsB.size(),
                successfulMigrations,
                failedMigrations,
                metadataMismatches.size(),
                unmatchedDocuments,
                mismatchTypesCounts
        );
    }

    public List<DocumentMismatch> filterMismatches(List<DocumentMismatch> mismatches, String fieldName) {
        if (fieldName == null || fieldName.trim().isEmpty()) {
            return mismatches;
        }
        
        return mismatches.stream()
                .filter(mismatch -> fieldName.equalsIgnoreCase(mismatch.getFieldName()))
                .collect(Collectors.toList());
    }

    public List<DocumentMetadata> filterByMigrationStatus(List<DocumentMetadata> documents, String migrationStatus) {
        if (migrationStatus == null || migrationStatus.trim().isEmpty()) {
            return documents;
        }
        
        return documents.stream()
                .filter(doc -> migrationStatus.equalsIgnoreCase(doc.getMigration()))
                .collect(Collectors.toList());
    }
}
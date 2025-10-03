package com.excelcompare.service;

import com.excelcompare.dto.ComparisonResult;
import com.excelcompare.model.DocumentMetadata;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ComparisonServiceTest {

    private ComparisonService comparisonService;

    @BeforeEach
    void setUp() {
        comparisonService = new ComparisonService();
    }

    @Test
    void testCompareDocuments_WithIdenticalDocuments() {
        // Arrange
        DocumentMetadata doc1 = new DocumentMetadata(
                "POL001", "Location1", "Folder1", "Document1.pdf",
                "CLAIM001", "John Doe", "2023-01-01", "Adjustor1",
                "PDF", "Restricted", "Active", "No", "Y"
        );

        DocumentMetadata doc2 = new DocumentMetadata(
                "POL001", "Location1", "Folder1", "Document1.pdf",
                "CLAIM001", "John Doe", "2023-01-01", "Adjustor1",
                "PDF", "Restricted", "Active", "No", "Y"
        );

        List<DocumentMetadata> documentsA = Arrays.asList(doc1);
        List<DocumentMetadata> documentsB = Arrays.asList(doc2);

        // Act
        ComparisonResult result = comparisonService.compareDocuments(documentsA, documentsB);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getStatistics().getTotalDocumentsA());
        assertEquals(1, result.getStatistics().getTotalDocumentsB());
        assertEquals(0, result.getMetadataMismatches().size());
        assertEquals(0, result.getMissingInA().size());
        assertEquals(0, result.getMissingInB().size());
        assertEquals(1, result.getMatchedDocuments().size());
    }

    @Test
    void testCompareDocuments_WithMismatches() {
        // Arrange
        DocumentMetadata doc1 = new DocumentMetadata(
                "POL001", "Location1", "Folder1", "Document1.pdf",
                "CLAIM001", "John Doe", "2023-01-01", "Adjustor1",
                "PDF", "Restricted", "Active", "No", "Y"
        );

        DocumentMetadata doc2 = new DocumentMetadata(
                "POL001", "Location2", "Folder2", "Document1.pdf", // Different location and folder
                "CLAIM001", "John Doe", "2023-01-01", "Adjustor1",
                "PDF", "Restricted", "Active", "No", "Y"
        );

        List<DocumentMetadata> documentsA = Arrays.asList(doc1);
        List<DocumentMetadata> documentsB = Arrays.asList(doc2);

        // Act
        ComparisonResult result = comparisonService.compareDocuments(documentsA, documentsB);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getMetadataMismatches().size()); // documentLocation and folderHierarchy
        assertEquals(0, result.getMissingInA().size());
        assertEquals(0, result.getMissingInB().size());
        assertEquals(0, result.getMatchedDocuments().size());
    }

    @Test
    void testCompareDocuments_WithMissingDocuments() {
        // Arrange
        DocumentMetadata doc1 = new DocumentMetadata(
                "POL001", "Location1", "Folder1", "Document1.pdf",
                "CLAIM001", "John Doe", "2023-01-01", "Adjustor1",
                "PDF", "Restricted", "Active", "No", "Y"
        );

        DocumentMetadata doc2 = new DocumentMetadata(
                "POL002", "Location2", "Folder2", "Document2.pdf",
                "CLAIM002", "Jane Smith", "2023-01-02", "Adjustor2",
                "PDF", "Not Restricted", "Active", "No", "N"
        );

        List<DocumentMetadata> documentsA = Arrays.asList(doc1);
        List<DocumentMetadata> documentsB = Arrays.asList(doc2);

        // Act
        ComparisonResult result = comparisonService.compareDocuments(documentsA, documentsB);

        // Assert
        assertNotNull(result);
        assertEquals(0, result.getMetadataMismatches().size());
        assertEquals(1, result.getMissingInA().size()); // doc2 is missing in A
        assertEquals(1, result.getMissingInB().size()); // doc1 is missing in B
        assertEquals(0, result.getMatchedDocuments().size());
    }
}
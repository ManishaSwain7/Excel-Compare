package com.excelcompare.controller;

import com.excelcompare.dto.ComparisonResult;
import com.excelcompare.dto.ComparisonResult.DocumentMismatch;
import com.excelcompare.model.DocumentMetadata;
import com.excelcompare.service.ComparisonService;
import com.excelcompare.service.ExcelService;
import com.excelcompare.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/compare")
public class ExcelComparisonController {

    @Autowired
    private ExcelService excelService;

    @Autowired
    private ComparisonService comparisonService;

    @Autowired
    private ReportService reportService;

    // Store comparison results temporarily (in production, use proper storage)
    private Map<String, ComparisonResult> comparisonResults = new HashMap<>();
    private String lastComparisonId = null;

    @PostMapping("/upload")
    public ResponseEntity<?> compareFiles(
            @RequestParam("fileA") MultipartFile fileA,
            @RequestParam("fileB") MultipartFile fileB) {
        
        try {
            // Validate files
            if (!excelService.isValidExcelFile(fileA) || !excelService.isValidExcelFile(fileB)) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Please upload valid Excel files (.xlsx or .xls)"));
            }

            // Read Excel files
            List<DocumentMetadata> documentsA = excelService.readExcelFile(fileA);
            List<DocumentMetadata> documentsB = excelService.readExcelFile(fileB);

            if (documentsA.isEmpty() && documentsB.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Both files appear to be empty or have no valid data"));
            }

            // Perform comparison
            ComparisonResult result = comparisonService.compareDocuments(documentsA, documentsB);

            // Store result for later export
            String comparisonId = String.valueOf(System.currentTimeMillis());
            comparisonResults.put(comparisonId, result);
            lastComparisonId = comparisonId;

            // Return results with comparison ID
            Map<String, Object> response = new HashMap<>();
            response.put("comparisonId", comparisonId);
            response.put("result", result);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error processing files: " + e.getMessage()));
        }
    }

    @GetMapping("/result/{comparisonId}")
    public ResponseEntity<?> getComparisonResult(@PathVariable String comparisonId) {
        ComparisonResult result = comparisonResults.get(comparisonId);
        if (result == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/result/{comparisonId}/filter")
    public ResponseEntity<?> getFilteredMismatches(
            @PathVariable String comparisonId,
            @RequestParam(required = false) String fieldName,
            @RequestParam(required = false) String migrationStatus) {
        
        ComparisonResult result = comparisonResults.get(comparisonId);
        if (result == null) {
            return ResponseEntity.notFound().build();
        }

        List<DocumentMismatch> filteredMismatches = comparisonService.filterMismatches(
                result.getMetadataMismatches(), fieldName);

        Map<String, Object> response = new HashMap<>();
        response.put("mismatches", filteredMismatches);
        
        if (migrationStatus != null) {
            List<DocumentMetadata> filteredDocs = comparisonService.filterByMigrationStatus(
                    result.getMatchedDocuments(), migrationStatus);
            response.put("filteredDocuments", filteredDocs);
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping("/export/csv/{comparisonId}")
    public ResponseEntity<byte[]> exportCsv(@PathVariable String comparisonId) {
        try {
            ComparisonResult result = comparisonResults.get(comparisonId);
            if (result == null) {
                return ResponseEntity.notFound().build();
            }

            byte[] csvData = reportService.generateCsvReport(result);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", "comparison_report.csv");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(csvData);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/export/csv/detailed/{comparisonId}")
    public ResponseEntity<byte[]> exportDetailedCsv(
            @PathVariable String comparisonId,
            @RequestParam(required = false) String fieldName) {
        try {
            ComparisonResult result = comparisonResults.get(comparisonId);
            if (result == null) {
                return ResponseEntity.notFound().build();
            }

            List<DocumentMismatch> mismatches = fieldName != null ?
                    comparisonService.filterMismatches(result.getMetadataMismatches(), fieldName) :
                    result.getMetadataMismatches();

            byte[] csvData = reportService.generateDetailedCsvReport(mismatches);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", "detailed_mismatches.csv");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(csvData);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/export/html/{comparisonId}")
    public ResponseEntity<String> exportHtml(@PathVariable String comparisonId) {
        ComparisonResult result = comparisonResults.get(comparisonId);
        if (result == null) {
            return ResponseEntity.notFound().build();
        }

        String htmlContent = reportService.generateHtmlSummary(result);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_HTML);

        return ResponseEntity.ok()
                .headers(headers)
                .body(htmlContent);
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("status", "UP", "service", "Excel Comparator"));
    }
}
package com.excelcompare.service;

import com.excelcompare.model.DocumentMetadata;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
public class ExcelService {

    private static final String[] EXPECTED_HEADERS = {
            "Document location", "Folder hierarchy", "Document name", "Claim no", 
            "Claimant", "Date of loss", "Adjustor name", "Policy no", 
            "Document type", "Security type", "Status", "Draft", "Migration"
    };

    public List<DocumentMetadata> readExcelFile(MultipartFile file) throws IOException {
        List<DocumentMetadata> documents = new ArrayList<>();
        
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            
            if (sheet.getPhysicalNumberOfRows() == 0) {
                return documents;
            }

            // Read header row to map column positions
            Row headerRow = sheet.getRow(0);
            Map<String, Integer> columnMap = createColumnMap(headerRow);

            // Read data rows
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row != null) {
                    DocumentMetadata doc = createDocumentFromRow(row, columnMap);
                    if (doc.getPolicyNo() != null && !doc.getPolicyNo().trim().isEmpty()) {
                        documents.add(doc);
                    }
                }
            }
        }

        return documents;
    }

    private Map<String, Integer> createColumnMap(Row headerRow) {
        Map<String, Integer> columnMap = new HashMap<>();
        
        for (int i = 0; i < headerRow.getLastCellNum(); i++) {
            Cell cell = headerRow.getCell(i);
            if (cell != null) {
                String header = getCellValueAsString(cell).trim().toLowerCase();
                
                // Map various possible header names to standard fields
                switch (header) {
                    case "document location":
                        columnMap.put("documentLocation", i);
                        break;
                    case "folder hierarchy":
                        columnMap.put("folderHierarchy", i);
                        break;
                    case "document name":
                        columnMap.put("documentName", i);
                        break;
                    case "claim no":
                        columnMap.put("claimNo", i);
                        break;
                    case "claimant":
                        columnMap.put("claimant", i);
                        break;
                    case "date of loss":
                        columnMap.put("dateOfLoss", i);
                        break;
                    case "adjustor name":
                        columnMap.put("adjustorName", i);
                        break;
                    case "policy no":
                        columnMap.put("policyNo", i);
                        break;
                    case "document type":
                        columnMap.put("documentType", i);
                        break;
                    case "security type":
                        columnMap.put("securityType", i);
                        break;
                    case "status":
                        columnMap.put("status", i);
                        break;
                    case "draft":
                        columnMap.put("draft", i);
                        break;
                    case "migration":
                        columnMap.put("migration", i);
                        break;
                }
            }
        }
        
        return columnMap;
    }

    private DocumentMetadata createDocumentFromRow(Row row, Map<String, Integer> columnMap) {
        DocumentMetadata doc = new DocumentMetadata();
        
        doc.setDocumentLocation(getCellValue(row, columnMap, "documentLocation"));
        doc.setFolderHierarchy(getCellValue(row, columnMap, "folderHierarchy"));
        doc.setDocumentName(getCellValue(row, columnMap, "documentName"));
        doc.setClaimNo(getCellValue(row, columnMap, "claimNo"));
        doc.setClaimant(getCellValue(row, columnMap, "claimant"));
        doc.setDateOfLoss(getCellValue(row, columnMap, "dateOfLoss"));
        doc.setAdjustorName(getCellValue(row, columnMap, "adjustorName"));
        doc.setPolicyNo(getCellValue(row, columnMap, "policyNo"));
        doc.setDocumentType(getCellValue(row, columnMap, "documentType"));
        doc.setSecurityType(getCellValue(row, columnMap, "securityType"));
        doc.setStatus(getCellValue(row, columnMap, "status"));
        doc.setDraft(getCellValue(row, columnMap, "draft"));
        doc.setMigration(getCellValue(row, columnMap, "migration"));
        
        return doc;
    }

    private String getCellValue(Row row, Map<String, Integer> columnMap, String fieldName) {
        Integer columnIndex = columnMap.get(fieldName);
        if (columnIndex != null && columnIndex < row.getLastCellNum()) {
            Cell cell = row.getCell(columnIndex);
            return getCellValueAsString(cell);
        }
        return "";
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }
        
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    return String.valueOf((long) cell.getNumericCellValue());
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
    }

    public boolean isValidExcelFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return false;
        }
        
        String contentType = file.getContentType();
        return contentType != null && (
            contentType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet") ||
            contentType.equals("application/vnd.ms-excel")
        );
    }
}
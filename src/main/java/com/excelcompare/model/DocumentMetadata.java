package com.excelcompare.model;

import java.util.Objects;

public class DocumentMetadata {
    private String policyNo;
    private String documentLocation;
    private String folderHierarchy;
    private String documentName;
    private String claimNo;
    private String claimant;
    private String dateOfLoss;
    private String adjustorName;
    private String documentType;
    private String securityType;
    private String status;
    private String draft;
    private String migration;

    // Default constructor
    public DocumentMetadata() {}

    // Constructor with all fields
    public DocumentMetadata(String policyNo, String documentLocation, String folderHierarchy,
                          String documentName, String claimNo, String claimant, String dateOfLoss,
                          String adjustorName, String documentType, String securityType,
                          String status, String draft, String migration) {
        this.policyNo = policyNo;
        this.documentLocation = documentLocation;
        this.folderHierarchy = folderHierarchy;
        this.documentName = documentName;
        this.claimNo = claimNo;
        this.claimant = claimant;
        this.dateOfLoss = dateOfLoss;
        this.adjustorName = adjustorName;
        this.documentType = documentType;
        this.securityType = securityType;
        this.status = status;
        this.draft = draft;
        this.migration = migration;
    }

    // Getters and Setters
    public String getPolicyNo() { return policyNo; }
    public void setPolicyNo(String policyNo) { this.policyNo = policyNo; }

    public String getDocumentLocation() { return documentLocation; }
    public void setDocumentLocation(String documentLocation) { this.documentLocation = documentLocation; }

    public String getFolderHierarchy() { return folderHierarchy; }
    public void setFolderHierarchy(String folderHierarchy) { this.folderHierarchy = folderHierarchy; }

    public String getDocumentName() { return documentName; }
    public void setDocumentName(String documentName) { this.documentName = documentName; }

    public String getClaimNo() { return claimNo; }
    public void setClaimNo(String claimNo) { this.claimNo = claimNo; }

    public String getClaimant() { return claimant; }
    public void setClaimant(String claimant) { this.claimant = claimant; }

    public String getDateOfLoss() { return dateOfLoss; }
    public void setDateOfLoss(String dateOfLoss) { this.dateOfLoss = dateOfLoss; }

    public String getAdjustorName() { return adjustorName; }
    public void setAdjustorName(String adjustorName) { this.adjustorName = adjustorName; }

    public String getDocumentType() { return documentType; }
    public void setDocumentType(String documentType) { this.documentType = documentType; }

    public String getSecurityType() { return securityType; }
    public void setSecurityType(String securityType) { this.securityType = securityType; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getDraft() { return draft; }
    public void setDraft(String draft) { this.draft = draft; }

    public String getMigration() { return migration; }
    public void setMigration(String migration) { this.migration = migration; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DocumentMetadata that = (DocumentMetadata) o;
        return Objects.equals(policyNo, that.policyNo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(policyNo);
    }

    @Override
    public String toString() {
        return "DocumentMetadata{" +
                "policyNo='" + policyNo + '\'' +
                ", documentName='" + documentName + '\'' +
                ", migration='" + migration + '\'' +
                '}';
    }
}
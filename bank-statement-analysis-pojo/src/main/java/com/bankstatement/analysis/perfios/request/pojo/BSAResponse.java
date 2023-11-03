package com.bankstatement.analysis.perfios.request.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BSAResponse implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -3716898238805866989L;

    @JsonProperty("account_holder_name")
    private String accountHolderName;

    @JsonProperty("bank_name")
    private String bankName;

    @JsonProperty("branch_name")
    private String branchName;

    @JsonProperty("account_type")
    private String accountType;

    @JsonProperty("bank_statement_file_name")
    private String bankStatementFileName;

    @JsonProperty("account_number")
    private String accountNumber;

    @JsonProperty("monthly_details")
    private List<MonthlyDetails> monthlyDetails;


    /**
     * @return the accountNumber
     */
    public String getAccountNumber() {
        return accountNumber;
    }

    /**
     * @param accountNumber the accountNumber to set
     */
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    /**
     * @return the accountHolderName
     */
    public String getAccountHolderName() {
        return accountHolderName;
    }

    /**
     * @param accountHolderName the accountHolderName to set
     */
    public void setAccountHolderName(String accountHolderName) {
        this.accountHolderName = accountHolderName;
    }

    /**
     * @return the bankName
     */
    public String getBankName() {
        return bankName;
    }

    /**
     * @param bankName the bankName to set
     */
    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    /**
     * @return the branchName
     */
    public String getBranchName() {
        return branchName;
    }

    /**
     * @param branchName the branchName to set
     */
    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    /**
     * @return the accountType
     */
    public String getAccountType() {
        return accountType;
    }

    /**
     * @param accountType the accountType to set
     */
    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    /**
     * @return the bankStatementFileName
     */
    public String getBankStatementFileName() {
        return bankStatementFileName;
    }

    /**
     * @param bankStatementFileName the bankStatementFileName to set
     */
    public void setBankStatementFileName(String bankStatementFileName) {
        this.bankStatementFileName = bankStatementFileName;
    }

    /**
     * @return the monthlyDetails
     */
    public List<MonthlyDetails> getMonthlyDetails() {
        return monthlyDetails;
    }

    /**
     * @param monthlyDetails the monthlyDetails to set
     */
    public void setMonthlyDetails(List<MonthlyDetails> monthlyDetails) {
        this.monthlyDetails = monthlyDetails;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((accountHolderName == null) ? 0 : accountHolderName.hashCode());
        result = prime * result + ((accountNumber == null) ? 0 : accountNumber.hashCode());
        result = prime * result + ((accountType == null) ? 0 : accountType.hashCode());
        result = prime * result + ((bankName == null) ? 0 : bankName.hashCode());
        result = prime * result + ((bankStatementFileName == null) ? 0 : bankStatementFileName.hashCode());
        result = prime * result + ((branchName == null) ? 0 : branchName.hashCode());
        result = prime * result + ((monthlyDetails == null) ? 0 : monthlyDetails.hashCode());
        return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        BSAResponse other = (BSAResponse) obj;
        if (accountHolderName == null) {
            if (other.accountHolderName != null)
                return false;
        } else if (!accountHolderName.equals(other.accountHolderName))
            return false;
        if (accountNumber == null) {
            if (other.accountNumber != null)
                return false;
        } else if (!accountNumber.equals(other.accountNumber))
            return false;
        if (accountType == null) {
            if (other.accountType != null)
                return false;
        } else if (!accountType.equals(other.accountType))
            return false;
        if (bankName == null) {
            if (other.bankName != null)
                return false;
        } else if (!bankName.equals(other.bankName))
            return false;
        if (bankStatementFileName == null) {
            if (other.bankStatementFileName != null)
                return false;
        } else if (!bankStatementFileName.equals(other.bankStatementFileName))
            return false;
        if (branchName == null) {
            if (other.branchName != null)
                return false;
        } else if (!branchName.equals(other.branchName))
            return false;
        if (monthlyDetails == null) {
            if (other.monthlyDetails != null)
                return false;
        } else if (!monthlyDetails.equals(other.monthlyDetails))
            return false;
        return true;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "BSAResponse [accountHolderName=" + accountHolderName + ", bankName=" + bankName + ", branchName="
                + branchName + ", accountType=" + accountType + ", bankStatementFileName=" + bankStatementFileName
                + ", accountNumber=" + accountNumber + ", monthlyDetails=" + monthlyDetails + "]";
    }


}

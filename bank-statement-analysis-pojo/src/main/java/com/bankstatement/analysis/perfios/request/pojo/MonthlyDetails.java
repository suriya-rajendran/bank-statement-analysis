package com.bankstatement.analysis.perfios.request.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MonthlyDetails implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -1412106660598528752L;

    @JsonProperty("month_name")
    private String monthName;

    @JsonProperty("total_credits")
    private String totalCredits;

    @JsonProperty("total_debits")
    private String totalDebits;

    @JsonProperty("total_credit_amounts")
    private String totalCreditAmounts;

    @JsonProperty("total_debit_amounts")
    private String totalDebitAmounts;

    @JsonProperty("bal5")
    private String balance5;

    @JsonProperty("bal15")
    private String balance15;

    @JsonProperty("bal25")
    private String balance25;

    @JsonProperty("bal_last")
    private String balanceLast;

    @JsonProperty("bal_avg")
    private String balanceAverage;

    @JsonProperty("inw_cheque_bounce")
    private String inwardChequeBounce;

    @JsonProperty("otw_cheque_bounce")
    private String outwardChequeBounce;

    @JsonProperty("avg_limit_utilization")
    private String averageLimitUtlization;

    /**
     * @return the monthName
     */
    public String getMonthName() {
        return monthName;
    }

    /**
     * @param monthName the monthName to set
     */
    public void setMonthName(String monthName) {
        this.monthName = monthName;
    }

    /**
     * @return the totalCredits
     */
    public String getTotalCredits() {
        return totalCredits;
    }

    /**
     * @param totalCredits the totalCredits to set
     */
    public void setTotalCredits(String totalCredits) {
        this.totalCredits = totalCredits;
    }

    /**
     * @return the totalDebits
     */
    public String getTotalDebits() {
        return totalDebits;
    }

    /**
     * @param totalDebits the totalDebits to set
     */
    public void setTotalDebits(String totalDebits) {
        this.totalDebits = totalDebits;
    }

    /**
     * @return the totalCreditAmounts
     */
    public String getTotalCreditAmounts() {
        return totalCreditAmounts;
    }

    /**
     * @param totalCreditAmounts the totalCreditAmounts to set
     */
    public void setTotalCreditAmounts(String totalCreditAmounts) {
        this.totalCreditAmounts = totalCreditAmounts;
    }

    /**
     * @return the totalDebitAmounts
     */
    public String getTotalDebitAmounts() {
        return totalDebitAmounts;
    }

    /**
     * @param totalDebitAmounts the totalDebitAmounts to set
     */
    public void setTotalDebitAmounts(String totalDebitAmounts) {
        this.totalDebitAmounts = totalDebitAmounts;
    }

    /**
     * @return the balance5
     */
    public String getBalance5() {
        return balance5;
    }

    /**
     * @param balance5 the balance5 to set
     */
    public void setBalance5(String balance5) {
        this.balance5 = balance5;
    }

    /**
     * @return the balance15
     */
    public String getBalance15() {
        return balance15;
    }

    /**
     * @param balance15 the balance15 to set
     */
    public void setBalance15(String balance15) {
        this.balance15 = balance15;
    }

    /**
     * @return the balance25
     */
    public String getBalance25() {
        return balance25;
    }

    /**
     * @param balance25 the balance25 to set
     */
    public void setBalance25(String balance25) {
        this.balance25 = balance25;
    }

    /**
     * @return the balanceLast
     */
    public String getBalanceLast() {
        return balanceLast;
    }

    /**
     * @param balanceLast the balanceLast to set
     */
    public void setBalanceLast(String balanceLast) {
        this.balanceLast = balanceLast;
    }

    /**
     * @return the balanceAverage
     */
    public String getBalanceAverage() {
        return balanceAverage;
    }

    /**
     * @param balanceAverage the balanceAverage to set
     */
    public void setBalanceAverage(String balanceAverage) {
        this.balanceAverage = balanceAverage;
    }

    /**
     * @return the inwardChequeBounce
     */
    public String getInwardChequeBounce() {
        return inwardChequeBounce;
    }

    /**
     * @param inwardChequeBounce the inwardChequeBounce to set
     */
    public void setInwardChequeBounce(String inwardChequeBounce) {
        this.inwardChequeBounce = inwardChequeBounce;
    }

    /**
     * @return the outwardChequeBounce
     */
    public String getOutwardChequeBounce() {
        return outwardChequeBounce;
    }

    /**
     * @param outwardChequeBounce the outwardChequeBounce to set
     */
    public void setOutwardChequeBounce(String outwardChequeBounce) {
        this.outwardChequeBounce = outwardChequeBounce;
    }

    /**
     * @return the averageLimitUtlization
     */
    public String getAverageLimitUtlization() {
        return averageLimitUtlization;
    }

    /**
     * @param averageLimitUtlization the averageLimitUtlization to set
     */
    public void setAverageLimitUtlization(String averageLimitUtlization) {
        this.averageLimitUtlization = averageLimitUtlization;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "MonthlyDetails [monthName=" + monthName + ", totalCredits=" + totalCredits + ", totalDebits="
                + totalDebits + ", totalCreditAmounts=" + totalCreditAmounts + ", totalDebitAmounts="
                + totalDebitAmounts + ", balance5=" + balance5 + ", balance15=" + balance15 + ", balance25=" + balance25
                + ", balanceLast=" + balanceLast + ", balanceAverage=" + balanceAverage + ", inwardChequeBounce="
                + inwardChequeBounce + ", outwardChequeBounce=" + outwardChequeBounce + ", averageLimitUtlization="
                + averageLimitUtlization + "]";
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((averageLimitUtlization == null) ? 0 : averageLimitUtlization.hashCode());
        result = prime * result + ((balance15 == null) ? 0 : balance15.hashCode());
        result = prime * result + ((balance25 == null) ? 0 : balance25.hashCode());
        result = prime * result + ((balance5 == null) ? 0 : balance5.hashCode());
        result = prime * result + ((balanceAverage == null) ? 0 : balanceAverage.hashCode());
        result = prime * result + ((balanceLast == null) ? 0 : balanceLast.hashCode());
        result = prime * result + ((inwardChequeBounce == null) ? 0 : inwardChequeBounce.hashCode());
        result = prime * result + ((monthName == null) ? 0 : monthName.hashCode());
        result = prime * result + ((outwardChequeBounce == null) ? 0 : outwardChequeBounce.hashCode());
        result = prime * result + ((totalCreditAmounts == null) ? 0 : totalCreditAmounts.hashCode());
        result = prime * result + ((totalCredits == null) ? 0 : totalCredits.hashCode());
        result = prime * result + ((totalDebitAmounts == null) ? 0 : totalDebitAmounts.hashCode());
        result = prime * result + ((totalDebits == null) ? 0 : totalDebits.hashCode());
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
        MonthlyDetails other = (MonthlyDetails) obj;
        if (averageLimitUtlization == null) {
            if (other.averageLimitUtlization != null)
                return false;
        } else if (!averageLimitUtlization.equals(other.averageLimitUtlization))
            return false;
        if (balance15 == null) {
            if (other.balance15 != null)
                return false;
        } else if (!balance15.equals(other.balance15))
            return false;
        if (balance25 == null) {
            if (other.balance25 != null)
                return false;
        } else if (!balance25.equals(other.balance25))
            return false;
        if (balance5 == null) {
            if (other.balance5 != null)
                return false;
        } else if (!balance5.equals(other.balance5))
            return false;
        if (balanceAverage == null) {
            if (other.balanceAverage != null)
                return false;
        } else if (!balanceAverage.equals(other.balanceAverage))
            return false;
        if (balanceLast == null) {
            if (other.balanceLast != null)
                return false;
        } else if (!balanceLast.equals(other.balanceLast))
            return false;
        if (inwardChequeBounce == null) {
            if (other.inwardChequeBounce != null)
                return false;
        } else if (!inwardChequeBounce.equals(other.inwardChequeBounce))
            return false;
        if (monthName == null) {
            if (other.monthName != null)
                return false;
        } else if (!monthName.equals(other.monthName))
            return false;
        if (outwardChequeBounce == null) {
            if (other.outwardChequeBounce != null)
                return false;
        } else if (!outwardChequeBounce.equals(other.outwardChequeBounce))
            return false;
        if (totalCreditAmounts == null) {
            if (other.totalCreditAmounts != null)
                return false;
        } else if (!totalCreditAmounts.equals(other.totalCreditAmounts))
            return false;
        if (totalCredits == null) {
            if (other.totalCredits != null)
                return false;
        } else if (!totalCredits.equals(other.totalCredits))
            return false;
        if (totalDebitAmounts == null) {
            if (other.totalDebitAmounts != null)
                return false;
        } else if (!totalDebitAmounts.equals(other.totalDebitAmounts))
            return false;
        if (totalDebits == null) {
            if (other.totalDebits != null)
                return false;
        } else if (!totalDebits.equals(other.totalDebits))
            return false;
        return true;
    }


}

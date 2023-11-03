package com.bankstatement.analysis.perfios.request.pojo;


import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BankStatement {
	private long id;

	@JsonProperty("unique_id")
	private String uniqueId;

	@JsonProperty("application_reference_no")
	private String applicationReferenceNo;

	@JsonProperty("customer_reference_no")
	private String customerReferenceNo;

	@JsonProperty("customer_role")
	private String customerRole;

	@JsonProperty("source")
	private Character source;

	@JsonProperty("consider_for_eligibility")
	private Character considerForEligibility;

	@JsonProperty("account_holder_name")
	private String accountHolderName;

	@JsonProperty("bank_name")
	private String bankName;

	@JsonProperty("branch_name")
	private String branchName;

	@JsonProperty("ac_type")
	private String acType;

	@JsonProperty("ac_number")
	private String acNumber;

	@JsonProperty("ifsc_code")
	private String ifscCode;

	@JsonProperty("is_verified")
	private Boolean isVerified;

	@JsonProperty("abb")
	private Double abb;

	@JsonProperty("total_monthly_credits")
	private Double totalMonthlyCredits;

	@JsonProperty("total_monthly_debits")
	private Double totalMonthlyDebits;

	@JsonProperty("total_monthly_average")
	private Double totalMonthlyAverage;

	@JsonProperty("total_no_of_monthly_debits")
	private Integer totalNoOfMonthlyDebits;

	@JsonProperty("total_no_of_monthly_credits")
	private Integer totalNoOfMonthlyCredits;
	
	@JsonProperty("total_inward_chq")
	private Integer totalInwardChq;

	@JsonProperty("total_outward_chq")
	private Integer totalOutwardChq;

	@JsonProperty("total_inward_chq_bounces")
	private Integer totalInwardChqBounces;

	@JsonProperty("total_outward_chq_bounces")
	private Integer totalOutwardChqBounces;

	@JsonProperty("total_inward_chq_bounces_percentage")
	private Double totalInwardChqBouncesPercentage;

	@JsonProperty("total_outward_chq_bounces_percentage")
	private Double totalOutwardChqBouncesPercentage;
	
	@JsonProperty("banking_stability")
	private String bankingStability;
	
	@JsonProperty("banking_turnover")
	private BigDecimal bankingTurnOver;
	
	@JsonProperty("no_of_emi_cheque_bounces_for_last_6_months")
	private String noOfEmiChequeBouncesForLast6Months;
	
	@JsonProperty("percentage_of_inward_return_for_last_6_months")
	private String percentageOfInwardReturnForLast6Months;
	
	@JsonProperty("monthly_summary")
	private String monthlySummary;
	
	@JsonProperty("transactions")
	private List<MonthlyTransactionLineItem> transactions;

	private final String months[] = { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov",
			"Dec" };

	@JsonProperty("repayment_account")
	private String repaymentAccount;

	@JsonProperty("beneficiary_account")
	private String beneficiaryTccount;

	@JsonProperty("statement_available")
	private String statementAvailable;

	@JsonProperty("emi_bounces_in_last_3_months")
	private Integer emiBouncesInLast3Months;

	@JsonProperty("emi_bounces_in_last_6_months")
	private Integer emiBouncesInLast6Months;
	
	@JsonProperty("emi_bounces_in_last_12_months")
	private Integer emiBouncesInLast12Months;

	@JsonProperty("pct_of_inw_return_3_months")
	private Double percentageOfInwardReturnIn3Months;

	@JsonProperty("pct_of_inw_return_6_months")
	private Double percentageOfInwardReturnIn6Months;

	@JsonProperty("pct_of_inw_return_12_months")
	private Double percentageOfInwardReturnIn12Months;
	
	@JsonProperty("high_value_credits")
	private BigDecimal highValueCredits;
	
	@JsonProperty("xlsx_report")
	private String xlReport;

	public BankStatement() {
	}
	
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getApplicationReferenceNo() {
		return applicationReferenceNo;
	}

	public void setApplicationReferenceNo(String applicationReferenceNo) {
		this.applicationReferenceNo = applicationReferenceNo;
	}

	public String getCustomerReferenceNo() {
		return customerReferenceNo;
	}

	public void setCustomerReferenceNo(String customerReferenceNo) {
		this.customerReferenceNo = customerReferenceNo;
	}

	public String getAccountHolderName() {
		return accountHolderName;
	}

	public void setAccountHolderName(String accountHolderName) {
		this.accountHolderName = accountHolderName;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getBranchName() {
		return branchName;
	}

	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}

	public String getAcType() {
		return acType;
	}

	public void setAcType(String acType) {
		this.acType = acType;
	}

	public List<MonthlyTransactionLineItem> getTransactions() {
		if (transactions != null) {
			Collections.sort(transactions, new Comparator<MonthlyTransactionLineItem>() {
				DateFormat format = new SimpleDateFormat("MMM-yyyy");

				@Override
				public int compare(MonthlyTransactionLineItem o1, MonthlyTransactionLineItem o2) {
					String o1DateString = o1.getMonth() + "-" + o1.getYear();
					String o2DateString = o2.getMonth() + "-" + o2.getYear();
					try {
						return format.parse(o2DateString).compareTo(format.parse(o1DateString));
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					return 0;
				}
			});
		}
		return transactions;
	}

	public void setTransactions(List<MonthlyTransactionLineItem> transactions) {
		this.transactions = transactions;
	}

	public String getAcNumber() {
		return acNumber;
	}

	public void setAcNumber(String acNumber) {
		this.acNumber = acNumber;
	}

	public String getUniqueId() {
		return uniqueId;
	}

	public void setUniqueId(String uniqueId) {
		this.uniqueId = uniqueId;
	}

	public Character getSource() {
		return source;
	}

	public void setSource(Character source) {
		this.source = source;
	}

	public Character getConsiderForEligibility() {
		return considerForEligibility;
	}

	public void setConsiderForEligibility(Character considerForEligibility) {
		this.considerForEligibility = considerForEligibility;
	}

	public String getBankingStability() {
		return bankingStability;
	}

	public void setBankingStability(String bankingStability) {
		this.bankingStability = bankingStability;
	}

	public String getNoOfEmiChequeBouncesForLast6Months() {
		return noOfEmiChequeBouncesForLast6Months;
	}

	public void setNoOfEmiChequeBouncesForLast6Months(String noOfEmiChequeBouncesForLast6Months) {
		this.noOfEmiChequeBouncesForLast6Months = noOfEmiChequeBouncesForLast6Months;
	}

	public String getPercentageOfInwardReturnForLast6Months() {
		return percentageOfInwardReturnForLast6Months;
	}

	public void setPercentageOfInwardReturnForLast6Months(String percentageOfInwardReturnForLast6Months) {
		this.percentageOfInwardReturnForLast6Months = percentageOfInwardReturnForLast6Months;
	}

	public String getMonthlySummary() {
		return monthlySummary;
	}

	public void setMonthlySummary(String monthlySummary) {
		this.monthlySummary = monthlySummary;
	}

	public String getCustomerRole() {
		return customerRole;
	}

	public void setCustomerRole(String customerRole) {
		this.customerRole = customerRole;
	}

	public String getIfscCode() {
		return ifscCode;
	}

	public void setIfscCode(String ifscCode) {
		this.ifscCode = ifscCode;
	}

	public String[] getMonths() {
		return months;
	}

	public String getRepaymentAccount() {
		return repaymentAccount;
	}

	public void setRepaymentAccount(String repaymentAccount) {
		this.repaymentAccount = repaymentAccount;
	}

	public String getBeneficiaryTccount() {
		return beneficiaryTccount;
	}

	public void setBeneficiaryTccount(String beneficiaryTccount) {
		this.beneficiaryTccount = beneficiaryTccount;
	}

	public String getStatementAvailable() {
		return statementAvailable;
	}

	public void setStatementAvailable(String statementAvailable) {
		this.statementAvailable = statementAvailable;
	}

	public Integer getEmiBouncesInLast3Months() {
		return emiBouncesInLast3Months;
	}

	public void setEmiBouncesInLast3Months(Integer emiBouncesInLast3Months) {
		this.emiBouncesInLast3Months = emiBouncesInLast3Months;
	}

	public Integer getEmiBouncesInLast6Months() {
		return emiBouncesInLast6Months;
	}

	public void setEmiBouncesInLast6Months(Integer emiBouncesInLast6Months) {
		this.emiBouncesInLast6Months = emiBouncesInLast6Months;
	}

	public Double getPercentageOfInwardReturnIn3Months() {
		return percentageOfInwardReturnIn3Months;
	}

	public void setPercentageOfInwardReturnIn3Months(Double percentageOfInwardReturnIn3Months) {
		this.percentageOfInwardReturnIn3Months = percentageOfInwardReturnIn3Months;
	}

	public Double getPercentageOfInwardReturnIn6Months() {
		return percentageOfInwardReturnIn6Months;
	}

	public void setPercentageOfInwardReturnIn6Months(Double percentageOfInwardReturnIn6Months) {
		this.percentageOfInwardReturnIn6Months = percentageOfInwardReturnIn6Months;
	}

	public Boolean getVerified() {
		return isVerified;
	}

	public void setVerified(Boolean verified) {
		isVerified = verified;
	}

	public Double getAbb() {
		return abb;
	}

	public void setAbb(Double abb) {
		this.abb = abb;
	}

	public Double getTotalMonthlyCredits() {
		return totalMonthlyCredits;
	}

	public void setTotalMonthlyCredits(Double totalMonthlyCredits) {
		this.totalMonthlyCredits = totalMonthlyCredits;
	}

	public Double getTotalMonthlyDebits() {
		return totalMonthlyDebits;
	}

	public void setTotalMonthlyDebits(Double totalMonthlyDebits) {
		this.totalMonthlyDebits = totalMonthlyDebits;
	}

	public Double getTotalMonthlyAverage() {
		return totalMonthlyAverage;
	}

	public void setTotalMonthlyAverage(Double totalMonthlyAverage) {
		this.totalMonthlyAverage = totalMonthlyAverage;
	}

	public Integer getTotalNoOfMonthlyDebits() {
		return totalNoOfMonthlyDebits;
	}

	public void setTotalNoOfMonthlyDebits(Integer totalNoOfMonthlyDebits) {
		this.totalNoOfMonthlyDebits = totalNoOfMonthlyDebits;
	}

	public Integer getTotalNoOfMonthlyCredits() {
		return totalNoOfMonthlyCredits;
	}

	public void setTotalNoOfMonthlyCredits(Integer totalNoOfMonthlyCredits) {
		this.totalNoOfMonthlyCredits = totalNoOfMonthlyCredits;
	}

	public Integer getTotalInwardChqBounces() {
		return totalInwardChqBounces;
	}

	public void setTotalInwardChqBounces(Integer totalInwardChqBounces) {
		this.totalInwardChqBounces = totalInwardChqBounces;
	}

	public Integer getTotalOutwardChqBounces() {
		return totalOutwardChqBounces;
	}

	public void setTotalOutwardChqBounces(Integer totalOutwardChqBounces) {
		this.totalOutwardChqBounces = totalOutwardChqBounces;
	}

	public Double getTotalInwardChqBouncesPercentage() {
		return totalInwardChqBouncesPercentage;
	}

	public void setTotalInwardChqBouncesPercentage(Double totalInwardChqBouncesPercentage) {
		this.totalInwardChqBouncesPercentage = totalInwardChqBouncesPercentage;
	}

	public Double getTotalOutwardChqBouncesPercentage() {
		return totalOutwardChqBouncesPercentage;
	}

	public void setTotalOutwardChqBouncesPercentage(Double totalOutwardChqBouncesPercentage) {
		this.totalOutwardChqBouncesPercentage = totalOutwardChqBouncesPercentage;
	}
	
	public String getXlReport() {
		return xlReport;
	}


	public void setXlReport(String xlReport) {
		this.xlReport = xlReport;
	}


	public Integer getTotalInwardChq() {
		return totalInwardChq;
	}


	public void setTotalInwardChq(Integer totalInwardChq) {
		this.totalInwardChq = totalInwardChq;
	}


	public Integer getTotalOutwardChq() {
		return totalOutwardChq;
	}


	public void setTotalOutwardChq(Integer totalOutwardChq) {
		this.totalOutwardChq = totalOutwardChq;
	}


	public BigDecimal getBankingTurnOver() {
		return bankingTurnOver;
	}


	public void setBankingTurnOver(BigDecimal bankingTurnOver) {
		this.bankingTurnOver = bankingTurnOver;
	}


	public Integer getEmiBouncesInLast12Months() {
		return emiBouncesInLast12Months;
	}


	public void setEmiBouncesInLast12Months(Integer emiBouncesInLast12Months) {
		this.emiBouncesInLast12Months = emiBouncesInLast12Months;
	}


	public Double getPercentageOfInwardReturnIn12Months() {
		return percentageOfInwardReturnIn12Months;
	}


	public void setPercentageOfInwardReturnIn12Months(Double percentageOfInwardReturnIn12Months) {
		this.percentageOfInwardReturnIn12Months = percentageOfInwardReturnIn12Months;
	}


	public BigDecimal getHighValueCredits() {
		return highValueCredits;
	}


	public void setHighValueCredits(BigDecimal highValueCredits) {
		this.highValueCredits = highValueCredits;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((abb == null) ? 0 : abb.hashCode());
		result = prime * result + ((acNumber == null) ? 0 : acNumber.hashCode());
		result = prime * result + ((acType == null) ? 0 : acType.hashCode());
		result = prime * result + ((accountHolderName == null) ? 0 : accountHolderName.hashCode());
		result = prime * result + ((applicationReferenceNo == null) ? 0 : applicationReferenceNo.hashCode());
		result = prime * result + ((bankName == null) ? 0 : bankName.hashCode());
		result = prime * result + ((bankingStability == null) ? 0 : bankingStability.hashCode());
		result = prime * result + ((bankingTurnOver == null) ? 0 : bankingTurnOver.hashCode());
		result = prime * result + ((beneficiaryTccount == null) ? 0 : beneficiaryTccount.hashCode());
		result = prime * result + ((branchName == null) ? 0 : branchName.hashCode());
		result = prime * result + ((considerForEligibility == null) ? 0 : considerForEligibility.hashCode());
		result = prime * result + ((customerReferenceNo == null) ? 0 : customerReferenceNo.hashCode());
		result = prime * result + ((customerRole == null) ? 0 : customerRole.hashCode());
		result = prime * result + ((emiBouncesInLast12Months == null) ? 0 : emiBouncesInLast12Months.hashCode());
		result = prime * result + ((emiBouncesInLast3Months == null) ? 0 : emiBouncesInLast3Months.hashCode());
		result = prime * result + ((emiBouncesInLast6Months == null) ? 0 : emiBouncesInLast6Months.hashCode());
		result = prime * result + ((highValueCredits == null) ? 0 : highValueCredits.hashCode());
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result + ((ifscCode == null) ? 0 : ifscCode.hashCode());
		result = prime * result + ((isVerified == null) ? 0 : isVerified.hashCode());
		result = prime * result + ((monthlySummary == null) ? 0 : monthlySummary.hashCode());
		result = prime * result + Arrays.hashCode(months);
		result = prime * result
				+ ((noOfEmiChequeBouncesForLast6Months == null) ? 0 : noOfEmiChequeBouncesForLast6Months.hashCode());
		result = prime * result + ((percentageOfInwardReturnForLast6Months == null) ? 0
				: percentageOfInwardReturnForLast6Months.hashCode());
		result = prime * result
				+ ((percentageOfInwardReturnIn12Months == null) ? 0 : percentageOfInwardReturnIn12Months.hashCode());
		result = prime * result
				+ ((percentageOfInwardReturnIn3Months == null) ? 0 : percentageOfInwardReturnIn3Months.hashCode());
		result = prime * result
				+ ((percentageOfInwardReturnIn6Months == null) ? 0 : percentageOfInwardReturnIn6Months.hashCode());
		result = prime * result + ((repaymentAccount == null) ? 0 : repaymentAccount.hashCode());
		result = prime * result + ((source == null) ? 0 : source.hashCode());
		result = prime * result + ((statementAvailable == null) ? 0 : statementAvailable.hashCode());
		result = prime * result + ((totalInwardChq == null) ? 0 : totalInwardChq.hashCode());
		result = prime * result + ((totalInwardChqBounces == null) ? 0 : totalInwardChqBounces.hashCode());
		result = prime * result
				+ ((totalInwardChqBouncesPercentage == null) ? 0 : totalInwardChqBouncesPercentage.hashCode());
		result = prime * result + ((totalMonthlyAverage == null) ? 0 : totalMonthlyAverage.hashCode());
		result = prime * result + ((totalMonthlyCredits == null) ? 0 : totalMonthlyCredits.hashCode());
		result = prime * result + ((totalMonthlyDebits == null) ? 0 : totalMonthlyDebits.hashCode());
		result = prime * result + ((totalNoOfMonthlyCredits == null) ? 0 : totalNoOfMonthlyCredits.hashCode());
		result = prime * result + ((totalNoOfMonthlyDebits == null) ? 0 : totalNoOfMonthlyDebits.hashCode());
		result = prime * result + ((totalOutwardChq == null) ? 0 : totalOutwardChq.hashCode());
		result = prime * result + ((totalOutwardChqBounces == null) ? 0 : totalOutwardChqBounces.hashCode());
		result = prime * result
				+ ((totalOutwardChqBouncesPercentage == null) ? 0 : totalOutwardChqBouncesPercentage.hashCode());
		result = prime * result + ((transactions == null) ? 0 : transactions.hashCode());
		result = prime * result + ((uniqueId == null) ? 0 : uniqueId.hashCode());
		result = prime * result + ((xlReport == null) ? 0 : xlReport.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BankStatement other = (BankStatement) obj;
		if (abb == null) {
			if (other.abb != null)
				return false;
		} else if (!abb.equals(other.abb))
			return false;
		if (acNumber == null) {
			if (other.acNumber != null)
				return false;
		} else if (!acNumber.equals(other.acNumber))
			return false;
		if (acType == null) {
			if (other.acType != null)
				return false;
		} else if (!acType.equals(other.acType))
			return false;
		if (accountHolderName == null) {
			if (other.accountHolderName != null)
				return false;
		} else if (!accountHolderName.equals(other.accountHolderName))
			return false;
		if (applicationReferenceNo == null) {
			if (other.applicationReferenceNo != null)
				return false;
		} else if (!applicationReferenceNo.equals(other.applicationReferenceNo))
			return false;
		if (bankName == null) {
			if (other.bankName != null)
				return false;
		} else if (!bankName.equals(other.bankName))
			return false;
		if (bankingStability == null) {
			if (other.bankingStability != null)
				return false;
		} else if (!bankingStability.equals(other.bankingStability))
			return false;
		if (bankingTurnOver == null) {
			if (other.bankingTurnOver != null)
				return false;
		} else if (!bankingTurnOver.equals(other.bankingTurnOver))
			return false;
		if (beneficiaryTccount == null) {
			if (other.beneficiaryTccount != null)
				return false;
		} else if (!beneficiaryTccount.equals(other.beneficiaryTccount))
			return false;
		if (branchName == null) {
			if (other.branchName != null)
				return false;
		} else if (!branchName.equals(other.branchName))
			return false;
		if (considerForEligibility == null) {
			if (other.considerForEligibility != null)
				return false;
		} else if (!considerForEligibility.equals(other.considerForEligibility))
			return false;
		if (customerReferenceNo == null) {
			if (other.customerReferenceNo != null)
				return false;
		} else if (!customerReferenceNo.equals(other.customerReferenceNo))
			return false;
		if (customerRole == null) {
			if (other.customerRole != null)
				return false;
		} else if (!customerRole.equals(other.customerRole))
			return false;
		if (emiBouncesInLast12Months == null) {
			if (other.emiBouncesInLast12Months != null)
				return false;
		} else if (!emiBouncesInLast12Months.equals(other.emiBouncesInLast12Months))
			return false;
		if (emiBouncesInLast3Months == null) {
			if (other.emiBouncesInLast3Months != null)
				return false;
		} else if (!emiBouncesInLast3Months.equals(other.emiBouncesInLast3Months))
			return false;
		if (emiBouncesInLast6Months == null) {
			if (other.emiBouncesInLast6Months != null)
				return false;
		} else if (!emiBouncesInLast6Months.equals(other.emiBouncesInLast6Months))
			return false;
		if (highValueCredits == null) {
			if (other.highValueCredits != null)
				return false;
		} else if (!highValueCredits.equals(other.highValueCredits))
			return false;
		if (id != other.id)
			return false;
		if (ifscCode == null) {
			if (other.ifscCode != null)
				return false;
		} else if (!ifscCode.equals(other.ifscCode))
			return false;
		if (isVerified == null) {
			if (other.isVerified != null)
				return false;
		} else if (!isVerified.equals(other.isVerified))
			return false;
		if (monthlySummary == null) {
			if (other.monthlySummary != null)
				return false;
		} else if (!monthlySummary.equals(other.monthlySummary))
			return false;
		if (!Arrays.equals(months, other.months))
			return false;
		if (noOfEmiChequeBouncesForLast6Months == null) {
			if (other.noOfEmiChequeBouncesForLast6Months != null)
				return false;
		} else if (!noOfEmiChequeBouncesForLast6Months.equals(other.noOfEmiChequeBouncesForLast6Months))
			return false;
		if (percentageOfInwardReturnForLast6Months == null) {
			if (other.percentageOfInwardReturnForLast6Months != null)
				return false;
		} else if (!percentageOfInwardReturnForLast6Months.equals(other.percentageOfInwardReturnForLast6Months))
			return false;
		if (percentageOfInwardReturnIn12Months == null) {
			if (other.percentageOfInwardReturnIn12Months != null)
				return false;
		} else if (!percentageOfInwardReturnIn12Months.equals(other.percentageOfInwardReturnIn12Months))
			return false;
		if (percentageOfInwardReturnIn3Months == null) {
			if (other.percentageOfInwardReturnIn3Months != null)
				return false;
		} else if (!percentageOfInwardReturnIn3Months.equals(other.percentageOfInwardReturnIn3Months))
			return false;
		if (percentageOfInwardReturnIn6Months == null) {
			if (other.percentageOfInwardReturnIn6Months != null)
				return false;
		} else if (!percentageOfInwardReturnIn6Months.equals(other.percentageOfInwardReturnIn6Months))
			return false;
		if (repaymentAccount == null) {
			if (other.repaymentAccount != null)
				return false;
		} else if (!repaymentAccount.equals(other.repaymentAccount))
			return false;
		if (source == null) {
			if (other.source != null)
				return false;
		} else if (!source.equals(other.source))
			return false;
		if (statementAvailable == null) {
			if (other.statementAvailable != null)
				return false;
		} else if (!statementAvailable.equals(other.statementAvailable))
			return false;
		if (totalInwardChq == null) {
			if (other.totalInwardChq != null)
				return false;
		} else if (!totalInwardChq.equals(other.totalInwardChq))
			return false;
		if (totalInwardChqBounces == null) {
			if (other.totalInwardChqBounces != null)
				return false;
		} else if (!totalInwardChqBounces.equals(other.totalInwardChqBounces))
			return false;
		if (totalInwardChqBouncesPercentage == null) {
			if (other.totalInwardChqBouncesPercentage != null)
				return false;
		} else if (!totalInwardChqBouncesPercentage.equals(other.totalInwardChqBouncesPercentage))
			return false;
		if (totalMonthlyAverage == null) {
			if (other.totalMonthlyAverage != null)
				return false;
		} else if (!totalMonthlyAverage.equals(other.totalMonthlyAverage))
			return false;
		if (totalMonthlyCredits == null) {
			if (other.totalMonthlyCredits != null)
				return false;
		} else if (!totalMonthlyCredits.equals(other.totalMonthlyCredits))
			return false;
		if (totalMonthlyDebits == null) {
			if (other.totalMonthlyDebits != null)
				return false;
		} else if (!totalMonthlyDebits.equals(other.totalMonthlyDebits))
			return false;
		if (totalNoOfMonthlyCredits == null) {
			if (other.totalNoOfMonthlyCredits != null)
				return false;
		} else if (!totalNoOfMonthlyCredits.equals(other.totalNoOfMonthlyCredits))
			return false;
		if (totalNoOfMonthlyDebits == null) {
			if (other.totalNoOfMonthlyDebits != null)
				return false;
		} else if (!totalNoOfMonthlyDebits.equals(other.totalNoOfMonthlyDebits))
			return false;
		if (totalOutwardChq == null) {
			if (other.totalOutwardChq != null)
				return false;
		} else if (!totalOutwardChq.equals(other.totalOutwardChq))
			return false;
		if (totalOutwardChqBounces == null) {
			if (other.totalOutwardChqBounces != null)
				return false;
		} else if (!totalOutwardChqBounces.equals(other.totalOutwardChqBounces))
			return false;
		if (totalOutwardChqBouncesPercentage == null) {
			if (other.totalOutwardChqBouncesPercentage != null)
				return false;
		} else if (!totalOutwardChqBouncesPercentage.equals(other.totalOutwardChqBouncesPercentage))
			return false;
		if (transactions == null) {
			if (other.transactions != null)
				return false;
		} else if (!transactions.equals(other.transactions))
			return false;
		if (uniqueId == null) {
			if (other.uniqueId != null)
				return false;
		} else if (!uniqueId.equals(other.uniqueId))
			return false;
		if (xlReport == null) {
			if (other.xlReport != null)
				return false;
		} else if (!xlReport.equals(other.xlReport))
			return false;
		return true;
	}


	@Override
	public String toString() {
		return "BankStatement [id=" + id + ", uniqueId=" + uniqueId + ", applicationReferenceNo="
				+ applicationReferenceNo + ", customerReferenceNo=" + customerReferenceNo + ", customerRole="
				+ customerRole + ", source=" + source + ", considerForEligibility=" + considerForEligibility
				+ ", accountHolderName=" + accountHolderName + ", bankName=" + bankName + ", branchName=" + branchName
				+ ", acType=" + acType + ", acNumber=" + acNumber + ", ifscCode=" + ifscCode + ", isVerified="
				+ isVerified + ", abb=" + abb + ", totalMonthlyCredits=" + totalMonthlyCredits + ", totalMonthlyDebits="
				+ totalMonthlyDebits + ", totalMonthlyAverage=" + totalMonthlyAverage + ", totalNoOfMonthlyDebits="
				+ totalNoOfMonthlyDebits + ", totalNoOfMonthlyCredits=" + totalNoOfMonthlyCredits + ", totalInwardChq="
				+ totalInwardChq + ", totalOutwardChq=" + totalOutwardChq + ", totalInwardChqBounces="
				+ totalInwardChqBounces + ", totalOutwardChqBounces=" + totalOutwardChqBounces
				+ ", totalInwardChqBouncesPercentage=" + totalInwardChqBouncesPercentage
				+ ", totalOutwardChqBouncesPercentage=" + totalOutwardChqBouncesPercentage + ", bankingStability="
				+ bankingStability + ", bankingTurnOver=" + bankingTurnOver + ", noOfEmiChequeBouncesForLast6Months="
				+ noOfEmiChequeBouncesForLast6Months + ", percentageOfInwardReturnForLast6Months="
				+ percentageOfInwardReturnForLast6Months + ", monthlySummary=" + monthlySummary + ", transactions="
				+ transactions + ", months=" + Arrays.toString(months) + ", repaymentAccount=" + repaymentAccount
				+ ", beneficiaryTccount=" + beneficiaryTccount + ", statementAvailable=" + statementAvailable
				+ ", emiBouncesInLast3Months=" + emiBouncesInLast3Months + ", emiBouncesInLast6Months="
				+ emiBouncesInLast6Months + ", emiBouncesInLast12Months=" + emiBouncesInLast12Months
				+ ", percentageOfInwardReturnIn3Months=" + percentageOfInwardReturnIn3Months
				+ ", percentageOfInwardReturnIn6Months=" + percentageOfInwardReturnIn6Months
				+ ", percentageOfInwardReturnIn12Months=" + percentageOfInwardReturnIn12Months + ", highValueCredits="
				+ highValueCredits + ", xlReport=" + xlReport + "]";
	}


}

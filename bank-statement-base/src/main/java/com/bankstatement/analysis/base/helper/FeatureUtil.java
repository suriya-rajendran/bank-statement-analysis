package com.bankstatement.analysis.base.helper;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.bankstatement.analysis.base.datamodel.ApplicationDetail;
import com.bankstatement.analysis.base.datamodel.BankTransactionDetails;
import com.bankstatement.analysis.base.datamodel.BankTransactionDetails.CATEGORY_TYPE;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class FeatureUtil implements Serializable {
	/**
	* 
	*/
	private static final long serialVersionUID = 4338018388710229818L;

	@JsonIgnore
	private final List<BankTransactionDetails> bankTransaction;

	@JsonIgnore
	private final List<BankTransactionDetails> bankTransactionBy12Month;

	@JsonIgnore
	private final List<BankTransactionDetails> bankTransactionBy3Month;

	@JsonIgnore
	private final List<BankTransactionDetails> bankTransactionBy3MonthFromApplicationDate;

	@JsonIgnore
	private final FeatureBaseHelper featureBy12Months;

	@JsonIgnore
	private final FeatureBaseHelper featureBy3Months;

	@JsonIgnore
	private final FeatureBaseHelper featureBy3MonthsFromApplicationDate;

	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

	public FeatureUtil(ApplicationDetail applicationDetail) throws ParseException {
		super();
		this.bankTransaction = applicationDetail.getTransactionDetails();
		this.bankTransactionBy12Month = bankTransactionByMonth(bankTransaction, 12, new Date());

		this.bankTransactionBy3Month = bankTransactionByMonth(bankTransaction, 3, new Date());

		this.bankTransactionBy3MonthFromApplicationDate = bankTransactionByMonth(bankTransaction, 3,
				sdf.parse(applicationDetail.getApplicationDate()));
		this.featureBy12Months = new FeatureBaseHelper(bankTransactionBy12Month);
		this.featureBy3Months = new FeatureBaseHelper(bankTransactionBy3Month);
		this.featureBy3MonthsFromApplicationDate = new FeatureBaseHelper(bankTransactionBy3MonthFromApplicationDate);
		setNetInterest12Month();
		setTotalInterest12Month();
		setNoOfBounceIwEcsChargeAppln3Months();
		setTotalAmountCash12Month();
		// TODO check setAverageMonthlyCashout
		setAverageMonthlyCashout();
		setNoPurchaseByCard12Month();
		setTotalAmountCashOut3Month();
		setTotalNegativeCharge3Month();
		setStdDev12MonthlyCashIn();
		// TODO CHECK OUTPUT VALUE
		setCoefVar12MonthlyCashIn();
		// ---------10------------
		setTotalDiscretionAmt12m();
		setTotalInsuranceamt12m();
		setNumCashOut3m();
		setTotalAmtCashIn3m();
		setAvgMonthlyCashin();
		setAvgCashWithdraw12m();
		setAvgCashDepositAppln3m();
		setNumCashOutAppln3m();
		setTotalCardPurchaseAmt12m();
		setTravelSpend12m();
		setTravelSpendIncomePc12m();
		setTotalcashbackrecv12m();
		setNocashIn3m();
//		setNocashIn12m();
		setRatioNumOfCashIn3m12m();
		setcoefVar12MonthCashOut();
		setNocashWithdrawAppln12m();
		setSalaryIncome3m();
		setSalaryIncome12m();
		setTotalGoodSavings3m();
		setGoodSavingIncomePc3m();
		setNoBankCharges12m();
		setTotalAmtCashOut12m();
		setNoCashIn12m();
		setNoCashOut12m();
		setRatioNumOfCashOut3m12m();
		setStdDev12MonthlyCashOut();
	}

	@JsonIgnore
	private List<BankTransactionDetails> bankTransactionByMonth(List<BankTransactionDetails> bankTransaction,
			Integer months, Date date) {

		Date currentDate = date;

		List<BankTransactionDetails> monthsTransactions = new ArrayList<>();
		for (BankTransactionDetails transaction : bankTransaction) {
			try {
				Date transactionDate = new SimpleDateFormat("yyyy-MM-dd").parse(transaction.getDate());
				if (isWithinLastMonths(transactionDate, currentDate, months)) {
					monthsTransactions.add(transaction);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return monthsTransactions;
	}

	@JsonIgnore
	private boolean isWithinLastMonths(Date transactionDate, Date currentDate, Integer months) {
		// Get calendar objects for transaction and current dates
		LocalDate localDate1 = transactionDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		LocalDate localDate2 = currentDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

		// Calculate the period between the two dates
		Period period = Period.between(localDate1, localDate2);

		// Extract the number of months from the period
		int monthsDifference = period.getYears() * 12 + period.getMonths();
		if (monthsDifference == months) {
			return true;
		}
		return false;
	}

	@JsonProperty("net_interest_12m")
	public double netInterest12Month;

	public void setNetInterest12Month() throws ParseException {

		this.netInterest12Month = featureBy12Months.getNetInterestMonth();

	}

	@JsonProperty("total_interest_earned_12m")
	public double totalInterest12Month;

	public void setTotalInterest12Month() throws ParseException {

		this.totalInterest12Month = featureBy12Months.getTotalInterestMonth();

	}

	@JsonProperty("num_Of_Bounced_IW_ECS_Charges_appln_3m")
	public long noOfBounceIwEcsChargeAppln3Months;

	public void setNoOfBounceIwEcsChargeAppln3Months() throws ParseException {

		this.noOfBounceIwEcsChargeAppln3Months = featureBy3MonthsFromApplicationDate.getNoOfBounceIwEcsChargeMonths();

	}

	@JsonProperty("total_amt_cash_in_12m")
	public double totalAmountCash12Month;

	public void setTotalAmountCash12Month() throws ParseException {

		this.totalAmountCash12Month = featureBy12Months.getTotalAmountCashInMonth();

	}

	@JsonProperty("avg_12_monthly_cashout")
	public double average12MonthlyCashout;

	public void setAverageMonthlyCashout() throws ParseException {
		this.average12MonthlyCashout = featureBy12Months.getAverageMonthCashout();

	}

	@JsonProperty("num_purchase_by_card_12m")
	public long noPurchaseByCard12Month;

	public void setNoPurchaseByCard12Month() throws ParseException {

		this.noPurchaseByCard12Month = featureBy12Months.getNoPurchaseByCardMonth();

	}

	@JsonProperty("total_amt_cash_out_3m")
	public double totalAmountCashOut3Month;

	public void setTotalAmountCashOut3Month() throws ParseException {

		this.totalAmountCashOut3Month = featureBy3MonthsFromApplicationDate.getTotalAmountCashOutMonth();
	}

	@JsonProperty("total_negative_charge_amt_3m")
	public double totalNegativeCharge3Month;

	public void setTotalNegativeCharge3Month() throws ParseException {
		this.totalNegativeCharge3Month = featureBy3MonthsFromApplicationDate.getTotalNegativeChargeMonth();
	}

	@JsonProperty("std_dev_12_monthly_cashin")
	public double stdDev12MonthlyCashIn;

	public void setStdDev12MonthlyCashIn() throws ParseException {

		this.stdDev12MonthlyCashIn = featureBy12Months.getStdDevMonthCashIn();
	}

	@JsonProperty("coef_var_12_monthly_cashin")
	public double coefVar12MonthlyCashIn;

	public void setCoefVar12MonthlyCashIn() throws ParseException {

		this.coefVar12MonthlyCashIn = featureBy12Months.getCoefVarMonthCashIn();
	}

	@JsonProperty("total_discretion_amt_12m")
	public double totalDiscretionAmt12m;

	public void setTotalDiscretionAmt12m() throws ParseException {
		this.totalDiscretionAmt12m = featureBy12Months.getTotalDiscretionAmountMonth();
	}

	@JsonProperty("total_insurance_amt_12m")
	public double totalInsuranceamt12m;

	public void setTotalInsuranceamt12m() throws ParseException {
		this.totalInsuranceamt12m = featureBy12Months.getTotalInsuranceAmountMonth();
	}

	@JsonProperty("num_cash_out_3m")
	public long numCashOut3m;

	public void setNumCashOut3m() throws ParseException {
		this.numCashOut3m = featureBy3MonthsFromApplicationDate.getNoCashOutMonth();
	}

	@JsonProperty("total_amt_cash_in_3m")
	public double totalAmtCashIn3m;

	public void setTotalAmtCashIn3m() throws ParseException {
		this.totalAmtCashIn3m = featureBy3MonthsFromApplicationDate.getTotalAmountCashInMonth();
	}

	@JsonProperty("avg_monthly_cashin")
	public double avgMonthlyCashin;

	public void setAvgMonthlyCashin() throws ParseException {
		this.avgMonthlyCashin = featureBy3Months.getAverageMonthCashIn();
	}

	@JsonProperty("avg_cash_withdraw_12m")
	public double avgCashWithdraw12m;

	public void setAvgCashWithdraw12m() throws ParseException {
		this.avgCashWithdraw12m = featureBy12Months.getAverageCashWithdrawMonth();
	}

	@JsonProperty("avg_cash_deposit_appln_3m")
	public double avgCashDepositAppln3m;

	public void setAvgCashDepositAppln3m() throws ParseException {
		this.avgCashDepositAppln3m = featureBy3MonthsFromApplicationDate.getAverageCashDepositMonth();
	}

	@JsonProperty("num_cash_out_appln_3m")
	public long numCashOutAppln3m;

	public void setNumCashOutAppln3m() throws ParseException {
		this.numCashOutAppln3m = featureBy3MonthsFromApplicationDate.getNoCashOutMonth();
	}

	@JsonProperty("total_card_purchase_amt_12m")
	public double totalCardPurchaseAmt12m;

	public void setTotalCardPurchaseAmt12m() throws ParseException {
		this.totalCardPurchaseAmt12m = featureBy12Months.getTotalCardPurchaseAmount();
	}

	@JsonProperty("travel_spend_12m")
	public double travelSpend12m;

	public void setTravelSpend12m() throws ParseException {
		this.travelSpend12m = featureBy12Months.getTravelSpendMonth();
	}

	@JsonProperty("travel_spend_income_pc_12m")
	public double travelSpendIncomePc12m;

	public void setTravelSpendIncomePc12m() throws ParseException {
		this.travelSpendIncomePc12m = featureBy12Months.getTravelSpendIncomeMonth();
	}

	@JsonProperty("total_cash_back_recv_12m")
	public double totalcashbackrecv12m;

	public void setTotalcashbackrecv12m() throws ParseException {
		this.totalcashbackrecv12m = featureBy12Months.getTotalCashbackReceivedMonth();
	}

	@JsonProperty("no_cash_in_appln_3m")
	public long nocashInAppln3m;

	public void setNocashIn3m() throws ParseException {
		this.nocashInAppln3m = featureBy3MonthsFromApplicationDate.getNocashInMonth();
	}

//	@JsonProperty("no_cash_in_12m")
//	public long nocashIn12m;
//
//	public void setNocashIn12m() throws ParseException {
//		this.nocashIn12m = featureBy12Months.getNocashInMonth();
//	}

	@JsonProperty("ratio_num_of_cash_in_3m_12m")
	public double ratioNumOfCashIn3m12m;

	public void setRatioNumOfCashIn3m12m() throws ParseException {
		if (featureBy12Months.getNocashInMonth() == 0) {
			this.ratioNumOfCashIn3m12m = -1;
		} else {
			this.ratioNumOfCashIn3m12m = featureBy3Months.getNocashInMonth() / featureBy12Months.getNocashInMonth();
		}

	}

	@JsonProperty("coef_var_12_month_cash_out")
	public double coefVar12MonthCashOut;

	public void setcoefVar12MonthCashOut() throws ParseException {

		this.coefVar12MonthCashOut = featureBy12Months.getCoefVarMonthCashOut();

	}

	@JsonProperty("no_cash_withdraw_appln_3m")
	public long nocashWithdrawAppln12m;

	public void setNocashWithdrawAppln12m() throws ParseException {
		this.nocashWithdrawAppln12m = featureBy3MonthsFromApplicationDate.getNoCashWithdrawMonth();
	}

	@JsonProperty("salary_income_3m")
	public double salaryIncome3m;

	public void setSalaryIncome3m() throws ParseException {

		this.salaryIncome3m = featureBy3MonthsFromApplicationDate.getSalaryIncomeMonth();

	}

	@JsonProperty("salary_income_12m")
	public double salaryIncome12m;

	public void setSalaryIncome12m() throws ParseException {

		this.salaryIncome3m = featureBy12Months.getSalaryIncomeMonth();

	}

	@JsonProperty("total_good_savings_3m")
	public double totalGoodSavings3m;

	public void setTotalGoodSavings3m() throws ParseException {

		this.totalGoodSavings3m = featureBy3MonthsFromApplicationDate.getTotalGoodSavingsMonth();

	}

	@JsonProperty("good_savings_income_pc_3m")
	public double goodSavingIncomePc3m;

	public void setGoodSavingIncomePc3m() throws ParseException {

		this.goodSavingIncomePc3m = featureBy3MonthsFromApplicationDate.getGoodSavingsIncomeMonth();

	}

	@JsonProperty("no_bank_charges_12m")
	public long noBankCharges12m;

	public void setNoBankCharges12m() throws ParseException {
		this.noBankCharges12m = featureBy12Months.getNoBankChargesMonth();
	}

	@JsonProperty("total_amt_cash_out_12m")
	public double totalAmtCashOut12m;

	public void setTotalAmtCashOut12m() throws ParseException {

		this.totalAmtCashOut12m = featureBy12Months.getTotalAmountCashOutMonth();

	}

	@JsonProperty("no_cash_in_12m")
	public long noCashIn12m;

	public void setNoCashIn12m() throws ParseException {

		this.noCashIn12m = featureBy12Months.getNocashInMonth();

	}

	@JsonProperty("no_cash_out_12m")
	public long noCashOut12m;

	public void setNoCashOut12m() throws ParseException {

		this.noCashOut12m = featureBy12Months.getNoCashOutMonth();

	}

	@JsonProperty("ratio_num_of_cash_out_3m_12m")
	public double ratioNumOfCashOut3m12m;

	public void setRatioNumOfCashOut3m12m() throws ParseException {
		if (featureBy12Months.getNoCashOutMonth() == 0) {
			this.ratioNumOfCashOut3m12m = -1;
		} else {
			this.ratioNumOfCashOut3m12m = featureBy3Months.getNoCashOutMonth() / featureBy12Months.getNoCashOutMonth();
		}

	}

	@JsonProperty("std_dev_12_monthly_cashout")
	public double stdDev12MonthlyCashOut;

	public void setStdDev12MonthlyCashOut() throws ParseException {

		this.stdDev12MonthlyCashOut = featureBy12Months.getStdDevMonthCashOut();
	}

}

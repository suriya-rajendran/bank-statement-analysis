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
import java.util.List;

import com.bankstatement.analysis.base.datamodel.ApplicationDetail;
import com.bankstatement.analysis.base.datamodel.BankTransactionDetails;
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
}

package com.bankstatement.analysis.base.helper;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.util.CollectionUtils;

import com.bankstatement.analysis.base.datamodel.ApplicationDetail;
import com.bankstatement.analysis.base.datamodel.BankTransactionDetails;
import com.bankstatement.analysis.base.datamodel.BankTransactionDetails.CATEGORY_TYPE;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class FeatureHelper implements Serializable {
	/**
	* 
	*/
	private static final long serialVersionUID = 4338018388710229818L;

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
		return bankTransaction;
	}

	@JsonIgnore
	private boolean isWithinLastMonths(Date transactionDate, Date currentDate, Integer months) {
		// Get calendar objects for transaction and current dates
		Calendar transactionCalendar = Calendar.getInstance();
		transactionCalendar.setTime(transactionDate);
		Calendar currentCalendar = Calendar.getInstance();
		currentCalendar.setTime(currentDate);

		// Compare years and months
		int transactionYear = transactionCalendar.get(Calendar.YEAR);
		int transactionMonth = transactionCalendar.get(Calendar.MONTH);
		int currentYear = currentCalendar.get(Calendar.YEAR);
		int currentMonth = currentCalendar.get(Calendar.MONTH);

		// Check if the transaction date is within the last 12 months
		if (currentYear - transactionYear == 1) {
			return currentMonth >= transactionMonth;
		} else if (currentYear - transactionYear == 0) {
			return currentMonth - transactionMonth <= months && currentMonth - transactionMonth >= 0;
		}
		return false;
	}

	public FeatureHelper(ApplicationDetail applicationDetail) throws ParseException {

		sdf = new SimpleDateFormat("yyyy-MM-dd");

		this.bankTransaction = applicationDetail.getTransactionDetails();
		this.bankTransactionBy12Month = bankTransactionByMonth(bankTransaction, 12, new Date());
		this.bankTransactionBy3Month = bankTransactionByMonth(bankTransaction, 3, new Date());
		this.bankTransactionBy3MonthFromApplicationDate = bankTransactionByMonth(bankTransaction, 3,
				sdf.parse(applicationDetail.getApplicationDate()));
	}

	@JsonIgnore
	private final List<BankTransactionDetails> bankTransaction;

	@JsonIgnore
	private final List<BankTransactionDetails> bankTransactionBy12Month;

	@JsonIgnore
	private final List<BankTransactionDetails> bankTransactionBy3Month;

	@JsonIgnore
	private final List<BankTransactionDetails> bankTransactionBy3MonthFromApplicationDate;

	@JsonIgnore
	private final SimpleDateFormat sdf;

	// -----------------------------------------------------------------------------------------------------------------------------------------------------------
	@JsonIgnore
	public HashMap<String, Double> getTotalAmountMonthly(List<BankTransactionDetails> transactions, CATEGORY_TYPE type,
			List<String> category) throws ParseException {

		HashMap<String, Double> monthlySumMap = new HashMap<>();
		for (BankTransactionDetails transaction : transactions) {
			String monthYear = new SimpleDateFormat("yyyy-MM").format(sdf.parse(transaction.getDate()));
			if (type == null && CollectionUtils.isEmpty(category)) {
				monthlySumMap.put(monthYear, monthlySumMap.getOrDefault(monthYear, 0.0) + transaction.getAmount());
			} else if (type != null && !CollectionUtils.isEmpty(category)) {

				if (transaction.getCategoryType() == type
						&& category.stream().anyMatch(transaction.getCategory()::contains)) {
					monthlySumMap.put(monthYear, monthlySumMap.getOrDefault(monthYear, 0.0) + transaction.getAmount());
				}
			} else if (type == null && !CollectionUtils.isEmpty(category)) {
				if (category.stream().anyMatch(transaction.getCategory()::contains)) {
					monthlySumMap.put(monthYear, monthlySumMap.getOrDefault(monthYear, 0.0) + transaction.getAmount());
				}
			} else if (type != null && CollectionUtils.isEmpty(category)) {
				if (transaction.getCategoryType() == type) {
					monthlySumMap.put(monthYear, monthlySumMap.getOrDefault(monthYear, 0.0) + transaction.getAmount());
				}
			}
		}

		return monthlySumMap;

	}

	@JsonIgnore
	public double getTotalAmountMonthlyBasedOnType(List<BankTransactionDetails> transaction, CATEGORY_TYPE type,
			List<String> category) throws ParseException {

		return getTotalAmountMonthly(transaction, type, category).values().stream()
				.collect(Collectors.summingDouble(Double::doubleValue));

	}

	@JsonIgnore
	public double getAverageAmountMonthlyBasedOnType(List<BankTransactionDetails> transaction, CATEGORY_TYPE type,
			List<String> category) throws ParseException {
		double averageTotal = 0;
		HashMap<String, Double> monthlySumMap = getTotalAmountMonthly(transaction, type, category);
		HashMap<String, Integer> monthlyTransactionCount = getMonthlyTransactionCount(transaction, type, category);
		for (Map.Entry<String, Double> entry : monthlySumMap.entrySet()) {
			String month = entry.getKey();
			double totalAmount = entry.getValue();
			int transactionCount = monthlyTransactionCount.get(month);
			double average = totalAmount / transactionCount;
			averageTotal += average;
		}
		return averageTotal;

	}

	@JsonIgnore
	public HashMap<String, Integer> getMonthlyTransactionCount(List<BankTransactionDetails> transactions,
			CATEGORY_TYPE type, List<String> category) throws ParseException {

		HashMap<String, Integer> monthlyTransactionCount = new HashMap<>();
		for (BankTransactionDetails transaction : transactions) {
			String monthYear = new SimpleDateFormat("yyyy-MM").format(sdf.parse(transaction.getDate()));
			if (type == null && CollectionUtils.isEmpty(category)) {
				monthlyTransactionCount.put(monthYear, monthlyTransactionCount.getOrDefault(monthYear, 0) + 1);
			} else if (type != null && !CollectionUtils.isEmpty(category)) {

				if (transaction.getCategoryType() == type
						&& category.stream().anyMatch(transaction.getCategory()::contains)) {
					monthlyTransactionCount.put(monthYear, monthlyTransactionCount.getOrDefault(monthYear, 0) + 1);
				}
			} else if (type == null && !CollectionUtils.isEmpty(category)) {
				if (category.stream().anyMatch(transaction.getCategory()::contains)) {
					monthlyTransactionCount.put(monthYear, monthlyTransactionCount.getOrDefault(monthYear, 0) + 1);
				}
			} else if (type != null && CollectionUtils.isEmpty(category)) {
				if (transaction.getCategoryType() == type) {
					monthlyTransactionCount.put(monthYear, monthlyTransactionCount.getOrDefault(monthYear, 0) + 1);
				}
			}

		}
		return monthlyTransactionCount;
	}
	// -----------------------------------------------------------------------------------------------------------------------------------------------------------

	@JsonProperty("net_interest_12m")
	public double netInterest12Month;

	public double getNetInterest12Month() {
		return this.netInterest12Month = bankTransactionBy12Month.stream()
				.filter(d -> d.getCategory().toUpperCase().contains("Interest".toUpperCase())
						|| d.getCategory().toUpperCase().contains("Interest Charges".toUpperCase()))
				.mapToDouble(BankTransactionDetails::getAmount).sum();

	}

	@JsonProperty("total_interest_earned_12m")
	public double totalInterest12Month;

	public double getTotalInterest12Month() {
		return this.totalInterest12Month = bankTransactionBy12Month.stream()
				.filter(d -> d.getCategory().toUpperCase().contains("Interest".toUpperCase()))
				.mapToDouble(BankTransactionDetails::getAmount).sum();

	}

	@JsonProperty("num_O_Bounced_IW_ECS_Charges_3m")
	public double noOfBounceIwEcsCharge3Months;

	public double getNoOfBounceIwEcsCharge3Months() {
		return this.noOfBounceIwEcsCharge3Months = bankTransactionBy3MonthFromApplicationDate.stream()
				.filter(d -> d.getCategory().toUpperCase().contains("Bounced I/W ECS".toUpperCase()))
				.mapToDouble(BankTransactionDetails::getAmount).sum();

	}

	@JsonProperty("total_amt_cash_in_12m")
	public double totalAmountCash12Month;

	public double getTotalAmountCash12Month() {
		return this.totalAmountCash12Month = bankTransactionBy12Month.stream()
				.filter(d -> CATEGORY_TYPE.INFLOW == d.getCategoryType()).mapToDouble(BankTransactionDetails::getAmount)
				.sum();

	}

	@JsonProperty("avg_monthly_cashout")
	public double averageMonthlyCashout;

	public double getAverageMonthlyCashout() throws ParseException {
		return this.averageMonthlyCashout = getAverageAmountMonthlyBasedOnType(bankTransaction, CATEGORY_TYPE.OUTFLOW,
				null);

	}

}

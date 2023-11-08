package com.bankstatement.analysis.base.helper;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
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
		this.bankTransactionBy12Month = bankTransactionByMonth(bankTransaction, 1, new Date());
//		this.bankTransactionBy3Month = bankTransactionByMonth(bankTransaction, 3, new Date());
		this.bankTransactionBy3MonthFromApplicationDate = bankTransactionByMonth(bankTransaction, 3,
				sdf.parse(applicationDetail.getApplicationDate()));
		setNetInterest12Month();
		setTotalInterest12Month();
		setNoOfBounceIwEcsCharge3Months();
		setTotalAmountCash12Month();
		setAverageMonthlyCashout();
		setNoPurchaseByCard12Month();
		setTotalAmountCashOut3Month();
		setTotalNegativeCharge3Month();
		setStdDevMonthlyCashin();

	}

	@JsonIgnore
	private final List<BankTransactionDetails> bankTransaction;

	@JsonIgnore
	private final List<BankTransactionDetails> bankTransactionBy12Month;

//	@JsonIgnore
//	private final List<BankTransactionDetails> bankTransactionBy3Month;

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
	public double calculateStandardDeviation(List<Double> values) {

		// get the sum of array
		double sum = 0.0;
		for (double i : values) {
			sum += i;
		}

		// get the mean of array
		int length = values.size();
		double mean = sum / length;

		// calculate the standard deviation
		double standardDeviation = 0.0;
		for (double num : values) {
			standardDeviation += Math.pow(num - mean, 2);
		}

		return Math.sqrt(standardDeviation / length);
	}

	@JsonIgnore
	public double mean(double arr[], int n) {
		double sum = 0;

		for (int i = 0; i < n; i++)
			sum = sum + arr[i];
		return sum / n;
	}

	// Function to find standard
	// deviation of given array.

	@JsonIgnore
	public double standardDeviation(double arr[], int n) {
		double sum = 0;

		for (int i = 0; i < n; i++)
			sum = sum + (arr[i] - mean(arr, n)) * (arr[i] - mean(arr, n));

		return Math.sqrt(sum / (n - 1));
	}

	// Function to find coefficient of variation.
	@JsonIgnore
	public double coefficientOfVariation(double arr[], int n) {
		return (standardDeviation(arr, n) / mean(arr, n));
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

	public void setNetInterest12Month() {
		this.netInterest12Month = bankTransactionBy12Month.stream()
				.filter(d -> d.getCategory().toUpperCase().contains("Interest".toUpperCase())
						|| d.getCategory().toUpperCase().contains("Interest Charges".toUpperCase()))
				.mapToDouble(BankTransactionDetails::getAmount).sum();

	}

	@JsonProperty("total_interest_earned_12m")
	public double totalInterest12Month;

	public void setTotalInterest12Month() {
		this.totalInterest12Month = bankTransactionBy12Month.stream()
				.filter(d -> d.getCategory().toUpperCase().contains("Interest".toUpperCase()))
				.mapToDouble(BankTransactionDetails::getAmount).sum();

	}

	@JsonProperty("num_O_Bounced_IW_ECS_Charges_3m")
	public long noOfBounceIwEcsCharge3Months;

	public void setNoOfBounceIwEcsCharge3Months() {
		this.noOfBounceIwEcsCharge3Months = bankTransactionBy3MonthFromApplicationDate.stream()
				.filter(d -> d.getCategory().toUpperCase().contains("Bounced I/W ECS".toUpperCase())).count();

	}

	@JsonProperty("total_amt_cash_in_12m")
	public double totalAmountCash12Month;

	public void setTotalAmountCash12Month() {
		this.totalAmountCash12Month = bankTransactionBy12Month.stream()
				.filter(d -> CATEGORY_TYPE.INFLOW == d.getCategoryType()).mapToDouble(BankTransactionDetails::getAmount)
				.sum();

	}

	@JsonProperty("avg_monthly_cashout")
	public double averageMonthlyCashout;

	public void setAverageMonthlyCashout() throws ParseException {
		this.averageMonthlyCashout = getAverageAmountMonthlyBasedOnType(bankTransactionBy12Month, CATEGORY_TYPE.OUTFLOW,
				null);

	}

	@JsonProperty("num_purchase_by_card_12m")
	public long noPurchaseByCard12Month;

	public void setNoPurchaseByCard12Month() throws ParseException {
		this.noPurchaseByCard12Month = bankTransactionBy12Month.stream()
				.filter(d -> CATEGORY_TYPE.OUTFLOW == d.getCategoryType()
						&& d.getCategory().equalsIgnoreCase("Purchase by Card"))
				.count();

	}

	@JsonProperty("total_amt_cash_out_3m")
	public double totalAmountCashOut3Month;

	public void setTotalAmountCashOut3Month() throws ParseException {
		this.totalAmountCashOut3Month = bankTransactionBy3MonthFromApplicationDate.stream()
				.filter(d -> CATEGORY_TYPE.OUTFLOW == d.getCategoryType())
				.mapToDouble(BankTransactionDetails::getAmount).sum();
	}

//	Category1' = 'Outflow'
//	and
//	 'Category2' in ('Below Min Balance','Bounced I/W ECS Charges','Bounced IW Cheque charges', 'Penal Charges')

	@JsonProperty("total_negative_charge_amt_3m")
	public double totalNegativeCharge3Month;

	public void setTotalNegativeCharge3Month() throws ParseException {
		this.totalNegativeCharge3Month = bankTransactionBy3MonthFromApplicationDate.stream()
				.filter(d -> CATEGORY_TYPE.OUTFLOW == d.getCategoryType()
						&& Arrays.asList("Below Min Balance", "Bounced I/W ECS Charges", "Bounced IW Cheque charges",
								"Penal Charges").contains(d.getCategory()))
				.mapToDouble(BankTransactionDetails::getAmount).sum();
	}

	@JsonProperty("std_dev_monthly_cashin")
	public double stdDevMonthlyCashin;

	public void setStdDevMonthlyCashin() throws ParseException {

		HashMap<String, Double> totalAmountMonthly = getTotalAmountMonthly(bankTransactionBy12Month,
				CATEGORY_TYPE.INFLOW, null);
		List<Double> values = new ArrayList<>(totalAmountMonthly.values());

		this.stdDevMonthlyCashin = calculateStandardDeviation(values);
	}

}

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

	public FeatureHelper(ApplicationDetail applicationDetail) throws ParseException {

		sdf = new SimpleDateFormat("yyyy-MM-dd");

		this.bankTransaction = applicationDetail.getTransactionDetails();
		this.bankTransactionBy12Month = bankTransactionByMonth(bankTransaction, 1, new Date());
		this.bankTransactionBy3Month = bankTransactionByMonth(bankTransaction, 3, new Date());
		this.bankTransactionBy3MonthFromApplicationDate = bankTransactionByMonth(bankTransaction, 3,
				sdf.parse(applicationDetail.getApplicationDate()));
		setNetInterest12Month();
		setTotalInterest12Month();
		setNoOfBounceIwEcsCharge3Months();
		setTotalAmountCash12Month();
		// TODO check setAverageMonthlyCashout
		setAverageMonthlyCashout();
		setNoPurchaseByCard12Month();
		setTotalAmountCashOut3Month();
		setTotalNegativeCharge3Month();
		setStdDevMonthlyCashin();
		// TODO CHECK OUTPUT VALUE
		setCoefVarMonthlyCashin();
		// ---------10------------
		setTotalDiscretionAmt12m();
		setTotalInsuranceamt12m();
		setNumCashOut3m();
		setTotalAmtCashIn3m();
		setAvgMonthlyCashin();
		setAvgCashWithdraw12m();
		setAvgCashDeposit3m();
		setNumCashOutAppDt3m();
		setTotalCardPurchaseAmt12m();
		setTravelSpend12m();
		// ---------20------------

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
	public double getTotalAmountMonthlySum(List<BankTransactionDetails> transaction, CATEGORY_TYPE type,
			List<String> category) throws ParseException {

		return getTotalAmountMonthly(transaction, type, category).values().stream()
				.collect(Collectors.summingDouble(Double::doubleValue));

	}

	@JsonIgnore
	public long getTotalAmountMonthlyCount(List<BankTransactionDetails> transaction, CATEGORY_TYPE type,
			List<String> category) throws ParseException {

		return getTotalAmountMonthly(transaction, type, category).values().stream().count();
//				.collect(Collectors.summingDouble(Double::doubleValue));

	}

	@JsonIgnore
	public double getAverageAmountMonthlySum(List<BankTransactionDetails> transaction, CATEGORY_TYPE type,
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
	public double getAverageAmountMonthlySumByMonth(List<BankTransactionDetails> transaction, CATEGORY_TYPE type,
			List<String> category) throws ParseException {
		double averageTotal = 0;
		HashMap<String, Double> monthlySumMap = getTotalAmountMonthly(transaction, type, category);
		for (Map.Entry<String, Double> entry : monthlySumMap.entrySet()) {
			String month = entry.getKey();
			double totalAmount = entry.getValue();
			averageTotal += totalAmount;
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
	public double calculateCoefficientOfVariation(double standardDeviation, List<Double> values) {

		// get the sum of array
		double sum = 0.0;
		for (double i : values) {
			sum += i;
		}

		// get the mean of array
		int length = values.size();
		double mean = sum / length;

		// calculate the standard deviation

		return (standardDeviation / mean) * 100;
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

	public void setNetInterest12Month() throws ParseException {
//		this.netInterest12Month = bankTransactionBy12Month.stream()
//				.filter(d -> d.getCategory().toUpperCase().contains("Interest".toUpperCase())
//						|| d.getCategory().toUpperCase().contains("Interest Charges".toUpperCase()))
//				.mapToDouble(BankTransactionDetails::getAmount).sum();

		this.netInterest12Month = getTotalAmountMonthlySum(bankTransactionBy12Month, null,
				Arrays.asList("Interest", "Interest Charges"));

	}

	@JsonProperty("total_interest_earned_12m")
	public double totalInterest12Month;

	public void setTotalInterest12Month() throws ParseException {
//		this.totalInterest12Month = bankTransactionBy12Month.stream()
//				.filter(d -> d.getCategory().toUpperCase().contains("Interest".toUpperCase()))
//				.mapToDouble(BankTransactionDetails::getAmount).sum();

		this.totalInterest12Month = getTotalAmountMonthlySum(bankTransactionBy12Month, null, Arrays.asList("Interest"));

	}

	@JsonProperty("num_O_Bounced_IW_ECS_Charges_3m")
	public long noOfBounceIwEcsCharge3Months;

	public void setNoOfBounceIwEcsCharge3Months() throws ParseException {
//		this.noOfBounceIwEcsCharge3Months = bankTransactionBy3MonthFromApplicationDate.stream()
//				.filter(d -> d.getCategory().toUpperCase().contains("Bounced I/W ECS".toUpperCase())).count();

		this.noOfBounceIwEcsCharge3Months = getTotalAmountMonthlyCount(bankTransactionBy3MonthFromApplicationDate, null,
				Arrays.asList("Bounced I/W ECS"));

	}

	@JsonProperty("total_amt_cash_in_12m")
	public double totalAmountCash12Month;

	public void setTotalAmountCash12Month() throws ParseException {
//		this.totalAmountCash12Month = bankTransactionBy12Month.stream()
//				.filter(d -> CATEGORY_TYPE.INFLOW == d.getCategoryType()).mapToDouble(BankTransactionDetails::getAmount)
//				.sum();

		this.totalAmountCash12Month = getTotalAmountMonthlySum(bankTransactionBy12Month, CATEGORY_TYPE.INFLOW, null);

	}

	@JsonProperty("avg_monthly_cashout")
	public double averageMonthlyCashout;

	public void setAverageMonthlyCashout() throws ParseException {
		this.averageMonthlyCashout = getAverageAmountMonthlySum(bankTransactionBy12Month, CATEGORY_TYPE.OUTFLOW, null);

	}

	@JsonProperty("num_purchase_by_card_12m")
	public long noPurchaseByCard12Month;

	public void setNoPurchaseByCard12Month() throws ParseException {
//		this.noPurchaseByCard12Month = bankTransactionBy12Month.stream()
//				.filter(d -> CATEGORY_TYPE.OUTFLOW == d.getCategoryType()
//						&& d.getCategory().equalsIgnoreCase("Purchase by Card"))
//				.count();

		this.noPurchaseByCard12Month = getTotalAmountMonthlyCount(bankTransactionBy12Month, CATEGORY_TYPE.OUTFLOW,
				Arrays.asList("Purchase by Card"));

	}

	@JsonProperty("total_amt_cash_out_3m")
	public double totalAmountCashOut3Month;

	public void setTotalAmountCashOut3Month() throws ParseException {
//		this.totalAmountCashOut3Month = bankTransactionBy3MonthFromApplicationDate.stream()
//				.filter(d -> CATEGORY_TYPE.OUTFLOW == d.getCategoryType())
//				.mapToDouble(BankTransactionDetails::getAmount).sum();

		this.totalAmountCashOut3Month = getTotalAmountMonthlySum(bankTransactionBy3MonthFromApplicationDate,
				CATEGORY_TYPE.OUTFLOW, null);
	}

//	Category1' = 'Outflow'
//	and
//	 'Category2' in ('Below Min Balance','Bounced I/W ECS Charges','Bounced IW Cheque charges', 'Penal Charges')

	@JsonProperty("total_negative_charge_amt_3m")
	public double totalNegativeCharge3Month;

	public void setTotalNegativeCharge3Month() throws ParseException {
//		this.totalNegativeCharge3Month = bankTransactionBy3MonthFromApplicationDate.stream()
//				.filter(d -> CATEGORY_TYPE.OUTFLOW == d.getCategoryType()
//						&& Arrays.asList("Below Min Balance", "Bounced I/W ECS Charges", "Bounced IW Cheque charges",
//								"Penal Charges").contains(d.getCategory()))
//				.mapToDouble(BankTransactionDetails::getAmount).sum();
//		
		this.totalNegativeCharge3Month = getTotalAmountMonthlySum(bankTransactionBy3MonthFromApplicationDate,
				CATEGORY_TYPE.OUTFLOW, Arrays.asList("Below Min Balance", "Bounced I/W ECS Charges",
						"Bounced IW Cheque charges", "Penal Charges"));
	}

	@JsonProperty("std_dev_monthly_cashin")
	public double stdDevMonthlyCashin;

	public void setStdDevMonthlyCashin() throws ParseException {

		HashMap<String, Double> totalAmountMonthly = getTotalAmountMonthly(bankTransactionBy12Month,
				CATEGORY_TYPE.INFLOW, null);
		List<Double> values = new ArrayList<>(totalAmountMonthly.values());

		this.stdDevMonthlyCashin = calculateStandardDeviation(values);
	}

	@JsonProperty("coef_var_monthly_cashin")
	public double coefVarMonthlyCashin;

	public void setCoefVarMonthlyCashin() throws ParseException {

		HashMap<String, Double> totalAmountMonthly = getTotalAmountMonthly(bankTransactionBy12Month,
				CATEGORY_TYPE.INFLOW, null);
		List<Double> values = new ArrayList<>(totalAmountMonthly.values());

		this.coefVarMonthlyCashin = calculateCoefficientOfVariation(calculateStandardDeviation(values), values);
	}

	@JsonProperty("total_discretion_amt_12m")
	public double totalDiscretionAmt12m;

	public void setTotalDiscretionAmt12m() throws ParseException {
		this.totalDiscretionAmt12m = getTotalAmountMonthlySum(bankTransactionBy12Month, CATEGORY_TYPE.OUTFLOW,
				Arrays.asList("Entertainment", "Food", "Clothing", "Online Shopping", "Software", "Travel",
						"Foreign currency"));
	}

	@JsonProperty("total_insurance_amt_12m")
	public double totalInsuranceamt12m;

	public void setTotalInsuranceamt12m() throws ParseException {
		this.totalInsuranceamt12m = getTotalAmountMonthlySum(bankTransactionBy12Month, CATEGORY_TYPE.OUTFLOW,
				Arrays.asList("Insurance"));
	}

	@JsonProperty("num_cash_out_3m")
	public long numCashOut3m;

	public void setNumCashOut3m() throws ParseException {
		this.numCashOut3m = getTotalAmountMonthlyCount(bankTransactionBy3Month, CATEGORY_TYPE.OUTFLOW, null);
	}

	@JsonProperty("total_amt_cash_in_3m")
	public double totalAmtCashIn3m;

	public void setTotalAmtCashIn3m() throws ParseException {
		this.totalAmtCashIn3m = getTotalAmountMonthlyCount(bankTransactionBy3MonthFromApplicationDate,
				CATEGORY_TYPE.INFLOW, null);
	}

	@JsonProperty("avg_monthly_cashin")
	public double avgMonthlyCashin;

	public void setAvgMonthlyCashin() throws ParseException {
		this.avgMonthlyCashin = getAverageAmountMonthlySum(bankTransactionBy3MonthFromApplicationDate,
				CATEGORY_TYPE.INFLOW, null);
	}

	@JsonProperty("avg_cash_withdraw_12m")
	public double avgCashWithdraw12m;

	public void setAvgCashWithdraw12m() throws ParseException {
		this.avgCashWithdraw12m = getAverageAmountMonthlySumByMonth(bankTransactionBy12Month, CATEGORY_TYPE.OUTFLOW,
				Arrays.asList("Cash Withdrawal"));
	}

	@JsonProperty("avg_cash_deposit_3m")
	public double avgCashDeposit3m;

	public void setAvgCashDeposit3m() throws ParseException {
		this.avgCashDeposit3m = getAverageAmountMonthlySumByMonth(bankTransactionBy12Month, CATEGORY_TYPE.INFLOW,
				Arrays.asList("Cash Deposit"));
	}

	@JsonProperty("num_cash_out_app_dt_3m")
	public long numCashOutAppDt3m;

	public void setNumCashOutAppDt3m() throws ParseException {
		this.numCashOutAppDt3m = getTotalAmountMonthlyCount(bankTransactionBy3MonthFromApplicationDate,
				CATEGORY_TYPE.OUTFLOW, null);
	}

	@JsonProperty("total_card_purchase_amt_12m")
	public double totalCardPurchaseAmt12m;

	public void setTotalCardPurchaseAmt12m() throws ParseException {
		this.totalCardPurchaseAmt12m = getTotalAmountMonthlySum(bankTransactionBy12Month, CATEGORY_TYPE.OUTFLOW,
				Arrays.asList("Purchase by Card"));
	}

	@JsonProperty("travel_spend_12m")
	public double travelSpend12m;

	public void setTravelSpend12m() throws ParseException {
		this.travelSpend12m = getTotalAmountMonthlySum(bankTransactionBy12Month, CATEGORY_TYPE.OUTFLOW,
				Arrays.asList("Travel"));
	}

	// TODO travel_spend_income_pc_12m
	@JsonProperty("travel_spend_income_pc_12m")
	public double travelSpendIncomePc12m;

	public void setTravelSpendIncomePc12m() throws ParseException {
		this.travelSpendIncomePc12m = getTotalAmountMonthlySum(bankTransactionBy12Month, CATEGORY_TYPE.OUTFLOW,
				Arrays.asList("Travel"));
	}

	@JsonProperty("total_cash_back_recv_12m")
	public double totalcashbackrecv12m;

	public void setTotalcashbackrecv12m() throws ParseException {
		this.totalcashbackrecv12m = getTotalAmountMonthlyCount(bankTransactionBy12Month, CATEGORY_TYPE.INFLOW,
				Arrays.asList("Cash Back"));
	}

	@JsonProperty("num_cash_appln_in_3m")
	public long numcashIn3m;

	public void setNumcashIn3m() throws ParseException {
		this.numcashIn3m = getTotalAmountMonthlyCount(bankTransactionBy3MonthFromApplicationDate, CATEGORY_TYPE.INFLOW,
				null);
	}

	@JsonProperty("num_cash_in_12m")
	public long numcashIn12m;

	public void setNumcashIn12m() throws ParseException {
		this.numcashIn12m = getTotalAmountMonthlyCount(bankTransactionBy12Month, CATEGORY_TYPE.INFLOW, null);
	}

}

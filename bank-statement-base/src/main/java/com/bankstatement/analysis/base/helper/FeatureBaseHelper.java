package com.bankstatement.analysis.base.helper;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.util.CollectionUtils;

import com.bankstatement.analysis.base.datamodel.BankTransactionDetails;
import com.bankstatement.analysis.base.datamodel.BankTransactionDetails.CATEGORY_TYPE;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
public class FeatureBaseHelper implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3141903475441863161L;

	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

	@JsonIgnore
	private final List<BankTransactionDetails> bankTransaction;

	public FeatureBaseHelper(List<BankTransactionDetails> bankTransaction) throws ParseException {
		this.bankTransaction = bankTransaction;
		setNetInterestMonth();
		setTotalInterestMonth();
		setNoOfBounceIwEcsChargeMonths();
		setTotalAmountCashMonth();
		setAverageMonthCashout();
		setNoPurchaseByCardMonth();
		setTotalAmountCashOutMonth();
		setTotalNegativeChargeMonth();
		setStdDevMonthCashIn();
		setCoefVarMonthCashIn();
		setTotalDiscretionAmountMonth();
		setTotalInsuranceAmountMonth();
		setNoCashOutMonth();
		setAverageMonthlyCashIn();
		setAverageCashWithdrawMonth();
		setAverageCashDepositMonth();
		setTotalCardPurchaseAmount();
		setTravelSpendMonth();
		setTotalCashbackReceivedMonth();
		setTravelSpendIncomeMonth();
		setNocashInMonth();
		setStdDevMonthCashOut();
		setNoCashWithdrawMonth();
		setSalaryIncomeMonth();
		setTotalGoodSavingsMonth();
		setGoodSavingsIncomeMonth();
		setNoBankChargesMonth();
		setCoefVarMonthCashOut();

	}

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

	// ----------------------------------------------------------------------------------

	public double netInterestMonth;

	public void setNetInterestMonth() throws ParseException {

		this.netInterestMonth = getTotalAmountMonthlySum(bankTransaction, null,
				Arrays.asList("Interest", "Interest Charges"));

	}

	public double totalInterestMonth;

	public void setTotalInterestMonth() throws ParseException {

		this.totalInterestMonth = getTotalAmountMonthlySum(bankTransaction, null, Arrays.asList("Interest"));

	}

	public long noOfBounceIwEcsChargeMonths;

	public void setNoOfBounceIwEcsChargeMonths() throws ParseException {
		this.noOfBounceIwEcsChargeMonths = getTotalAmountMonthlyCount(bankTransaction, null,
				Arrays.asList("Bounced I/W ECS"));

	}

	public double totalAmountCashInMonth;

	public void setTotalAmountCashMonth() throws ParseException {

		this.totalAmountCashInMonth = getTotalAmountMonthlySum(bankTransaction, CATEGORY_TYPE.INFLOW, null);

	}

	public double averageMonthCashout;

	public void setAverageMonthCashout() throws ParseException {
		this.averageMonthCashout = getAverageAmountMonthlySum(bankTransaction, CATEGORY_TYPE.OUTFLOW, null);

	}

	public long noPurchaseByCardMonth;

	public void setNoPurchaseByCardMonth() throws ParseException {

		this.noPurchaseByCardMonth = getTotalAmountMonthlyCount(bankTransaction, CATEGORY_TYPE.OUTFLOW,
				Arrays.asList("Purchase by Card"));

	}

	public double totalAmountCashOutMonth;

	public void setTotalAmountCashOutMonth() throws ParseException {

		this.totalAmountCashOutMonth = getTotalAmountMonthlySum(bankTransaction, CATEGORY_TYPE.OUTFLOW, null);
	}

	public double totalNegativeChargeMonth;

	public void setTotalNegativeChargeMonth() throws ParseException {

		this.totalNegativeChargeMonth = getTotalAmountMonthlySum(bankTransaction, CATEGORY_TYPE.OUTFLOW, Arrays
				.asList("Below Min Balance", "Bounced I/W ECS Charges", "Bounced IW Cheque charges", "Penal Charges"));
	}

	public double stdDevMonthCashIn;

	public void setStdDevMonthCashIn() throws ParseException {

		HashMap<String, Double> totalAmountMonthly = getTotalAmountMonthly(bankTransaction, CATEGORY_TYPE.INFLOW, null);
		List<Double> values = new ArrayList<>(totalAmountMonthly.values());

		this.stdDevMonthCashIn = calculateStandardDeviation(values);
	}

	public double coefVarMonthCashIn;

	public void setCoefVarMonthCashIn() throws ParseException {

		HashMap<String, Double> totalAmountMonthly = getTotalAmountMonthly(bankTransaction, CATEGORY_TYPE.INFLOW, null);
		List<Double> values = new ArrayList<>(totalAmountMonthly.values());

		this.coefVarMonthCashIn = calculateCoefficientOfVariation(stdDevMonthCashIn, values);
	}

	public double totalDiscretionAmountMonth;

	public void setTotalDiscretionAmountMonth() throws ParseException {
		this.totalDiscretionAmountMonth = getTotalAmountMonthlySum(bankTransaction, CATEGORY_TYPE.OUTFLOW,
				Arrays.asList("Entertainment", "Food", "Clothing", "Online Shopping", "Software", "Travel",
						"Foreign currency"));
	}

	public double totalInsuranceAmountMonth;

	public void setTotalInsuranceAmountMonth() throws ParseException {
		this.totalInsuranceAmountMonth = getTotalAmountMonthlySum(bankTransaction, CATEGORY_TYPE.OUTFLOW,
				Arrays.asList("Insurance"));
	}

	public long noCashOutMonth;

	public void setNoCashOutMonth() throws ParseException {
		this.noCashOutMonth = getTotalAmountMonthlyCount(bankTransaction, CATEGORY_TYPE.OUTFLOW, null);
	}

	public double averageMonthCashIn;

	public void setAverageMonthlyCashIn() throws ParseException {
		this.averageMonthCashIn = getAverageAmountMonthlySum(bankTransaction, CATEGORY_TYPE.INFLOW, null);

	}

	public double averageCashWithdrawMonth;

	public void setAverageCashWithdrawMonth() throws ParseException {
		this.averageCashWithdrawMonth = getAverageAmountMonthlySumByMonth(bankTransaction, CATEGORY_TYPE.OUTFLOW,
				Arrays.asList("Cash Withdrawal"));
	}

	public double averageCashDepositMonth;

	public void setAverageCashDepositMonth() throws ParseException {
		this.averageCashDepositMonth = getAverageAmountMonthlySumByMonth(bankTransaction, CATEGORY_TYPE.INFLOW,
				Arrays.asList("Cash Deposit"));
	}

	public double totalCardPurchaseAmount;

	public void setTotalCardPurchaseAmount() throws ParseException {
		this.totalCardPurchaseAmount = getTotalAmountMonthlySum(bankTransaction, CATEGORY_TYPE.OUTFLOW,
				Arrays.asList("Purchase by Card"));
	}

	public double travelSpendMonth;

	public void setTravelSpendMonth() throws ParseException {
		this.travelSpendMonth = getTotalAmountMonthlySum(bankTransaction, CATEGORY_TYPE.OUTFLOW,
				Arrays.asList("Travel"));
	}

	public double totalCashbackReceivedMonth;

	public void setTotalCashbackReceivedMonth() throws ParseException {
		this.totalCashbackReceivedMonth = getTotalAmountMonthlyCount(bankTransaction, CATEGORY_TYPE.INFLOW,
				Arrays.asList("Cash Back"));
	}

	public double travelSpendIncomeMonth;

	public void setTravelSpendIncomeMonth() throws ParseException {

		if (totalAmountCashInMonth == 0) {
			this.travelSpendIncomeMonth = -1;
		} else {
			this.travelSpendIncomeMonth = travelSpendMonth / totalAmountCashInMonth;
		}
	}

	public long nocashInMonth;

	public void setNocashInMonth() throws ParseException {
		this.nocashInMonth = getTotalAmountMonthlyCount(bankTransaction, CATEGORY_TYPE.OUTFLOW, null);
	}

	public double stdDevMonthCashOut;

	public void setStdDevMonthCashOut() throws ParseException {

		HashMap<String, Double> totalAmountMonthly = getTotalAmountMonthly(bankTransaction, CATEGORY_TYPE.OUTFLOW,
				null);
		List<Double> values = new ArrayList<>(totalAmountMonthly.values());

		this.stdDevMonthCashOut = calculateStandardDeviation(values);
	}

	public long noCashWithdrawMonth;

	public void setNoCashWithdrawMonth() throws ParseException {
		this.noCashWithdrawMonth = getTotalAmountMonthlyCount(bankTransaction, CATEGORY_TYPE.OUTFLOW,
				Arrays.asList("Cash Withdrawal"));
	}

	public double salaryIncomeMonth;

	public void setSalaryIncomeMonth() throws ParseException {
		this.salaryIncomeMonth = getTotalAmountMonthlySum(bankTransaction, CATEGORY_TYPE.INFLOW,
				Arrays.asList("Salary"));
	}

	public double totalGoodSavingsMonth;

	public void setTotalGoodSavingsMonth() throws ParseException {
		this.totalGoodSavingsMonth = getTotalAmountMonthlySum(bankTransaction, CATEGORY_TYPE.OUTFLOW,
				Arrays.asList("Fixed Deposit", "Mutual Fund purchase", "Small Saving"));
	}

	public double goodSavingsIncomeMonth;

	public void setGoodSavingsIncomeMonth() throws ParseException {
		if (totalAmountCashInMonth == 0) {
			this.goodSavingsIncomeMonth = -1;
		} else {
			this.goodSavingsIncomeMonth = totalGoodSavingsMonth / totalAmountCashInMonth;
		}

	}

	public long noBankChargesMonth;

	public void setNoBankChargesMonth() throws ParseException {
		this.noBankChargesMonth = getTotalAmountMonthlyCount(bankTransaction, CATEGORY_TYPE.OUTFLOW,
				Arrays.asList("Bank Charges"));
	}

	public double coefVarMonthCashOut;

	public void setCoefVarMonthCashOut() throws ParseException {

		HashMap<String, Double> totalAmountMonthly = getTotalAmountMonthly(bankTransaction, CATEGORY_TYPE.OUTFLOW,
				null);
		List<Double> values = new ArrayList<>(totalAmountMonthly.values());

		this.coefVarMonthCashOut = calculateCoefficientOfVariation(stdDevMonthCashIn, values);
	}

	// 25,36

}

package com.bankstatement.analysis.base.service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.bankstatement.analysis.base.datamodel.AccountDetail;
import com.bankstatement.analysis.base.datamodel.AccountDetail.ACCOUNT_STATUS;
import com.bankstatement.analysis.base.datamodel.BankStatementAggregate;
import com.bankstatement.analysis.base.datamodel.BankStatementAggregate.REPORT_TYPE;
import com.bankstatement.analysis.base.datamodel.BankStatementInitiate;
import com.bankstatement.analysis.base.datamodel.BankTransactionDetails;
import com.bankstatement.analysis.base.datamodel.BankTransactionDetails.CATEGORY_TYPE;
import com.bankstatement.analysis.base.datamodel.Customer;
import com.bankstatement.analysis.base.datamodel.Customer.CUSTOMER_TYPE;
import com.bankstatement.analysis.base.datamodel.Customer.REPORT_STATUS;
import com.bankstatement.analysis.base.helper.FeatureUtil;
import com.bankstatement.analysis.base.repo.BankStatementAggregateRepo;
import com.bankstatement.analysis.base.repo.CustomerRepo;
import com.bankstatement.analysis.request.pojo.BankStatementPojo;
import com.bankstatement.analysis.request.pojo.CustomException;
import com.bankstatement.analysis.request.pojo.CustomerPojo;
import com.bankstatement.analysis.request.pojo.InitiateRequestPojo;
import com.bankstatement.analysis.transaction.pojo.BankAccountDetails;
import com.bankstatement.analysis.transaction.pojo.Xn;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class FeatureService {

	private static final Logger logger = LoggerFactory.getLogger(FeatureService.class);

	@Autowired
	BankStatementAggregateRepo bankStatementAggregateRepo;

	@Autowired
	BankStatementImpl bankStatementImpl;

	@Autowired
	CustomerRepo customerRepo;

	ObjectMapper objectMapper = new ObjectMapper();

	public HashMap<String, String> saveApplicationDetails(BankStatementPojo bankStatementPojo) throws Exception {

		HashMap<String, String> response = new HashMap<>();

		try {
			BankStatementAggregate bankStatementAggregate = new BankStatementAggregate();

			bankStatementAggregate.setApplicationReferenceNo(bankStatementPojo.getApplicationReferenceNo());

			bankStatementAggregate.setTenure(bankStatementPojo.getTenure());

			bankStatementAggregate.setLoanamount(bankStatementPojo.getLoanamount());

			bankStatementAggregate.setApplicationDate(bankStatementPojo.getApplicationDate());

			if (!CollectionUtils.isEmpty(bankStatementPojo.getCustomer())) {
				for (CustomerPojo vo : bankStatementPojo.getCustomer()) {
					Customer customer = new Customer();

					customer.setCustomerReferenceNo(vo.getCustomerReferenceNo());

					customer.setCustomerType(CUSTOMER_TYPE.valueOf(vo.getCustomerType().toString()));

					bankStatementAggregate.addCustomer(customer);

				}
			}

			bankStatementAggregateRepo.save(bankStatementAggregate);
			response.put("status", "successful");
			response.put("request_no", bankStatementAggregate.getWebRefID());
			return response;
		} catch (Exception e) {
			logger.info("exception occured {}", e);
			throw new Exception();
		}
	}

	public BankStatementAggregate getApplicationDetails(String webRefId) {
		return bankStatementAggregateRepo.findByWebRefID(webRefId);

	}

	@Async
	public void updateApplicationDetails(InitiateRequestPojo initiate) {

		BankStatementAggregate aggregate = bankStatementAggregateRepo
				.findByWebRefID(initiate.getApplicationRequestNo());

		if (aggregate != null && aggregate.getReportType() == null) {
			aggregate.setReportType(REPORT_TYPE.valueOf(initiate.getReportType()));
			bankStatementAggregateRepo.save(aggregate);
		}

	}

	public void updateCustomer(String custWebRefNo, String reportStatus) {

		Customer customer = customerRepo.findByWebRefID(custWebRefNo);

		if (customer != null && org.apache.commons.lang.StringUtils.isEmpty(reportStatus)) {
			customer.setReportStatus(REPORT_STATUS.valueOf(reportStatus));
			customerRepo.save(customer);
		}

	}

	@Async
	public void constructFeature(String responseBody, BankStatementInitiate initiate) throws Exception {

		BankStatementInitiate bankStatementInitiate = bankStatementImpl
				.getBankStatementInitiateByProcessId(initiate.getProcessId());
		if (bankStatementInitiate != null) {

			Customer customer = customerRepo.findByWebRefID(bankStatementInitiate.getCustWebNo());

			if (customer != null) {

				JsonNode jsonNode = objectMapper.readTree(responseBody);

				JsonNode accountDetails = jsonNode.get("accountXns");

				List<BankAccountDetails> bankDetails = objectMapper.readValue(accountDetails.toString(),
						new TypeReference<List<BankAccountDetails>>() {
						});

				for (BankAccountDetails det : bankDetails) {

					AccountDetail accountDetail = new AccountDetail();

					accountDetail.setAcNumber(det.getAccountNo());

					accountDetail.setBankName(det.getAccountType());

					for (Xn d : det.getXns()) {
						BankTransactionDetails details = new BankTransactionDetails();
						details.setDate(d.getDate());

						details.setChqNo(d.getChqNo());

						details.setNarration(d.getNarration());

						details.setAmount(d.getAmount());

						details.setOriginalCategory(d.getCategory());

						details.setBalance(d.getBalance());

						details.setRequestId(initiate.getRequestId());

						if (d.getAmount() < 0) {
							details.setCategoryType(CATEGORY_TYPE.OUTFLOW);
						} else {
							details.setCategoryType(CATEGORY_TYPE.INFLOW);
						}

						if (d.getCategory().toUpperCase().contains("Transfer To".toUpperCase())) {
							details.setCategory("Transfer out");
						} else if (d.getCategory().toUpperCase().contains("Transfer From".toUpperCase())) {
							details.setCategory("Transfer in");
						} else {
							details.setCategory(d.getCategory());
						}

						accountDetail.addTransactionDetails(details);

					}
					logger.info("{}", accountDetail.getTransaction());

					customer.addAccountDetails(accountDetail);
				}
				customer.setReportStatus(REPORT_STATUS.CALLBACK);
				customerRepo.save(customer);
			}
		}
	}

	@Transactional
	public ResponseEntity<?> fetchfeatureResponse(BankStatementPojo bankStatementPojo)
			throws JsonProcessingException, ParseException {
		String response = null;

		List<BankTransactionDetails> bankTransactionDetails = new ArrayList<>();
		BankStatementAggregate aggregate = bankStatementAggregateRepo.findByWebRefID(bankStatementPojo.getRequestNo());

		if (aggregate != null && aggregate.getReportType() != null) {
			for (CustomerPojo vo : bankStatementPojo.getCustomer()) {
				Customer customer = aggregate.getCustomer().stream()
						.filter(d -> d.getWebRefID().equalsIgnoreCase(vo.getCustomerWebRefNo())).findFirst()
						.orElse(null);

				if (customer != null) {
					customer.getAccountDetail().stream().forEach(d -> {
						vo.getAccountPojo().stream().forEach(da -> {
							if (da.getAccWebRefNo().equalsIgnoreCase(d.getWebRefID())) {
								d.setAccountStatus(ACCOUNT_STATUS.valueOf(da.getAccountStatus().toString()));

							}
						});

					});

					if (REPORT_TYPE.MEMBER_WISE == aggregate.getReportType()) {

						bankTransactionDetails = customer.getAccountDetail().stream()
								.filter(d -> ACCOUNT_STATUS.INCLUDED == d.getAccountStatus())
								.map(AccountDetail::getTransaction).flatMap(Collection::stream)
								.collect(Collectors.toList());
						customer.setCustomerResponse(objectMapper.writeValueAsString(
								new FeatureUtil(bankTransactionDetails, aggregate.getApplicationDate())));

						customer.setReportStatus(REPORT_STATUS.REPORT_GENERATED);
						response = customer.getCustomerResponse();
					}

					customerRepo.save(customer);
				}

			}

			bankTransactionDetails = new ArrayList<>();

			if (REPORT_TYPE.APPLICATION == aggregate.getReportType()) {
				aggregate = bankStatementAggregateRepo.findByWebRefID(bankStatementPojo.getRequestNo());

				if (!aggregate.getCustomer().stream().anyMatch(d -> d.getReportStatus() == REPORT_STATUS.INITIATED
						|| d.getReportStatus() == REPORT_STATUS.NOT_INITIATED)) {

					bankTransactionDetails = aggregate.getCustomer().stream().map(Customer::getAccountDetail)
							.flatMap(Collection::stream).filter(d -> ACCOUNT_STATUS.INCLUDED == d.getAccountStatus())
							.map(AccountDetail::getTransaction).flatMap(Collection::stream)
							.collect(Collectors.toList());

					aggregate.setApplicationResponse(objectMapper.writeValueAsString(
							new FeatureUtil(bankTransactionDetails, aggregate.getApplicationDate())));

					bankStatementAggregateRepo.save(aggregate);
					response = aggregate.getApplicationResponse();
				}
			}
		} else {
			throw new CustomException("400", "Report Type Cannot be empty");
		}

		return ResponseEntity.ok(response);

	}

}

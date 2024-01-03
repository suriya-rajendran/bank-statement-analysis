package com.bankstatement.analysis.base.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.ParseException;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.bankstatement.analysis.base.datamodel.AccountDetail;
import com.bankstatement.analysis.base.datamodel.AccountDetail.ACCOUNT_STATUS;
import com.bankstatement.analysis.base.datamodel.BankStatementAggregate;
import com.bankstatement.analysis.base.datamodel.BankStatementAggregate.REPORT_TYPE;
import com.bankstatement.analysis.base.datamodel.BankStatementInitiate;
import com.bankstatement.analysis.base.datamodel.BankTransactionDetails;
import com.bankstatement.analysis.base.datamodel.BankTransactionDetails.CATEGORY_TYPE;
import com.bankstatement.analysis.base.datamodel.Customer;
import com.bankstatement.analysis.base.datamodel.Customer.CUSTOMER_TYPE;
import com.bankstatement.analysis.base.datamodel.CustomerTransactionDetails;
import com.bankstatement.analysis.base.datamodel.CustomerTransactionDetails.REPORT_STATUS;
import com.bankstatement.analysis.base.datamodel.Document;
import com.bankstatement.analysis.base.helper.FeatureUtil;
import com.bankstatement.analysis.base.repo.BankStatementAggregateRepo;
import com.bankstatement.analysis.base.repo.CustomerRepo;
import com.bankstatement.analysis.base.repo.CustomerTransactionDetailsRepo;
import com.bankstatement.analysis.perfios.request.pojo.AccountAnalysis;
import com.bankstatement.analysis.request.pojo.BankStatementPojo;
import com.bankstatement.analysis.request.pojo.CustomException;
import com.bankstatement.analysis.request.pojo.CustomerPojo;
import com.bankstatement.analysis.request.pojo.CustomerTransactionPojo;
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

	@Autowired
	ObjectMapper objectMapper = new ObjectMapper();

	@Autowired
	EntityManager manager;

	@Autowired
	CustomerTransactionDetailsRepo CustomerTransactionDetailsRepo;

//	@Async
//    @Transactional
//    public CompletableFuture<Void> yourAsyncMethod() {
//        // Your asynchronous logic here
//        // Call a method that performs a 'merge' operation
//        mergeOperation();
//
//        return CompletableFuture.completedFuture(null);
//    }
//
//    @Transactional
//    public void mergeOperation() {
//        // Your 'merge' operation logic here
//    }

	public BankStatementAggregate saveApplicationDetails(BankStatementPojo bankStatementPojo) throws Exception {

		try {
			BankStatementAggregate bankStatementAggregate = bankStatementAggregateRepo
					.findByApplicationReferenceNo(bankStatementPojo.getApplicationReferenceNo());
			if (bankStatementAggregate != null) {
				bankStatementAggregate = bankStatementAggregateRepo.findByWebRefID(bankStatementPojo.getWebRefNo());

				if (bankStatementAggregate == null) {
					throw new CustomException("400", bankStatementPojo.getWebRefNo()
							+ " Invalid WebRef No & Application Reference No already exists");
				}
			} else {
				bankStatementAggregate = new BankStatementAggregate();
			}

			bankStatementAggregate.setApplicationReferenceNo(bankStatementPojo.getApplicationReferenceNo());

			bankStatementAggregate.setTenure(bankStatementPojo.getTenure());

			bankStatementAggregate.setLoanamount(bankStatementPojo.getLoanamount());

			bankStatementAggregate.setReportType(REPORT_TYPE.valueOf(bankStatementPojo.getReportType()));

			bankStatementAggregate.setApplicationDate(bankStatementPojo.getApplicationDate());

			bankStatementAggregate.setProcessType(bankStatementPojo.getProcessType());

			if (!CollectionUtils.isEmpty(bankStatementPojo.getCustomer())) {
				for (CustomerPojo vo : bankStatementPojo.getCustomer()) {
					// TODO update logic need to do
					Customer customer = new Customer();

					customer.setCustomerReferenceNo(vo.getCustomerReferenceNo());

					customer.setCustomerType(CUSTOMER_TYPE.valueOf(vo.getCustomerType().toString()));

					bankStatementAggregate.addCustomer(customer);

				}
			}
			bankStatementAggregateRepo.save(bankStatementAggregate);
			return bankStatementAggregate;
		} catch (Exception e) {
			logger.info("exception occured {}", e);
			if (e instanceof CustomException) {
				CustomException ex = (CustomException) e;
				throw new CustomException(ex.getErrorCode(), ex.getErrorMessage());
			}
			throw new Exception();
		}
	}

	public BankStatementAggregate getApplicationDetails(String webRefId) {
		return bankStatementAggregateRepo.findByWebRefID(webRefId);

	}

	public void updateCustomer(String custWebRefNo, String tranWebRefNo, String reportStatus) {

		Customer customer = customerRepo.findByWebRefID(custWebRefNo);

		if (customer != null && !org.apache.commons.lang.StringUtils.isEmpty(reportStatus)) {
			for (CustomerTransactionDetails vo : customer.getTransactionDetail()) {
				vo.setReportStatus(REPORT_STATUS.valueOf(reportStatus));

			}
			customerRepo.save(customer);
		}

	}

	@Async
	public void constructFeature(String responseBody, BankStatementInitiate initiate) throws Exception {

		BankStatementInitiate bankStatementInitiate = bankStatementImpl
				.getBankStatementInitiateByProcessId(initiate.getProcessId());
		if (bankStatementInitiate != null) {

			Customer cust = customerRepo.findByWebRefID(bankStatementInitiate.getCustWebNo());

			if (cust != null) {
				CustomerTransactionDetails vo = cust.getTransactionDetail().stream()
						.filter(d -> d.getWebRefID().equalsIgnoreCase(bankStatementInitiate.getDocWebNo())).findFirst()
						.orElse(null);

				if (vo != null) {
					JsonNode jsonNode = objectMapper.readTree(responseBody);

					JsonNode accountDetails = jsonNode.get("accountXns");

					List<BankAccountDetails> bankDetails = objectMapper.readValue(accountDetails.toString(),
							new TypeReference<List<BankAccountDetails>>() {
							});

					for (BankAccountDetails det : bankDetails) {

						JsonNode bankdetails = jsonNode.get("accountAnalysis");

						List<AccountAnalysis> accountAnalysis = objectMapper.readValue(bankdetails.toString(),
								new TypeReference<List<AccountAnalysis>>() {
								});

						AccountDetail accountDetail = new AccountDetail();

						accountDetail.setAcNumber(det.getAccountNo());

						AccountAnalysis info = accountAnalysis.stream()
								.filter(d -> d.getAccountNo().equalsIgnoreCase(det.getAccountNo())).findAny()
								.orElse(null);

						accountDetail.setBankName(info.getSummaryInfo().getInstName());

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

						vo.addAccountDetails(accountDetail);
					}
					vo.setReportStatus(REPORT_STATUS.CALLBACK);
					CustomerTransactionDetailsRepo.save(vo);
				}
			}
		}
	}

	@Transactional
	public ResponseEntity<?> fetchfeatureResponse(BankStatementPojo bankStatementPojo)
			throws JsonProcessingException, ParseException {
		String response = null;

		List<BankTransactionDetails> bankTransactionDetails = new ArrayList<>();
		BankStatementAggregate aggregate = bankStatementAggregateRepo.findByWebRefID(bankStatementPojo.getWebRefNo());

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
				aggregate = bankStatementAggregateRepo.findByWebRefID(bankStatementPojo.getWebRefNo());

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

	public HashMap<String, String> updateCustomerDetail(BankStatementPojo bankStatementPojo) throws Exception {
		HashMap<String, String> response = new HashMap<>();

		try {
			BankStatementAggregate bankStatementAggregate = bankStatementAggregateRepo
					.findByWebRefID(bankStatementPojo.getWebRefNo());

			if (bankStatementAggregate != null) {

				if (!CollectionUtils.isEmpty(bankStatementAggregate.getCustomer())) {
					for (Customer vo : bankStatementAggregate.getCustomer()) {
						CustomerPojo customerPojo = bankStatementPojo.getCustomer().stream()
								.filter(d -> d.getCustomerWebRefNo().equalsIgnoreCase(vo.getWebRefID())).findFirst()
								.orElse(null);
						if (customerPojo != null) {
							for (CustomerTransactionPojo tran : customerPojo.getCustomerDetail()) {
								CustomerTransactionDetails details = new CustomerTransactionDetails();

								details.setRequestType(tran.getType());

								details.setScannedDoc(tran.isScannedDoc());

								details.setInstitutionType(tran.getInstitutionType());

								Document doc = new Document();
								doc.setImagePath(tran.getImagePath());
								details.setDocuments(doc);
								vo.addCustomerTransactionDetails(details);
							}
							customerRepo.save(vo);
						}

					}
				}

			} else {
				throw new CustomException("400", " Invalid WebRef No ");
			}
			response.put("status", "successful");

			return response;
		} catch (Exception e) {
			logger.info("exception occured {}", e);
			if (e instanceof CustomException) {
				CustomException ex = (CustomException) e;
				throw new CustomException(ex.getErrorCode(), ex.getErrorMessage());
			}
			throw new Exception();
		}

	}

}

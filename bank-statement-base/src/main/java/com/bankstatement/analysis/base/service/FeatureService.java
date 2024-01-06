package com.bankstatement.analysis.base.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.ParseException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.bankstatement.analysis.base.datamodel.AccountDetail;
import com.bankstatement.analysis.base.datamodel.AccountDetail.ACCOUNT_STATUS;
import com.bankstatement.analysis.base.datamodel.BankStatementAggregate;
import com.bankstatement.analysis.base.datamodel.BankStatementAggregate.AGGREGATE_STATUS;
import com.bankstatement.analysis.base.datamodel.BankTransactionDetails;
import com.bankstatement.analysis.base.datamodel.Customer;
import com.bankstatement.analysis.base.datamodel.Customer.CUSTOMER_STATUS;
import com.bankstatement.analysis.base.datamodel.Customer.CUSTOMER_TYPE;
import com.bankstatement.analysis.base.datamodel.CustomerTransactionDetails;
import com.bankstatement.analysis.base.datamodel.CustomerTransactionDetails.REPORT_STATUS;
import com.bankstatement.analysis.base.datamodel.CustomerTransactionDetails.TRANSACTION_STATUS;
import com.bankstatement.analysis.base.datamodel.Document;
import com.bankstatement.analysis.base.helper.FeatureUtil;
import com.bankstatement.analysis.base.repo.BankStatementAggregateRepo;
import com.bankstatement.analysis.base.repo.CustomerRepo;
import com.bankstatement.analysis.base.repo.CustomerTransactionDetailsRepo;
import com.bankstatement.analysis.request.pojo.BankStatementPojo;
import com.bankstatement.analysis.request.pojo.CustomException;
import com.bankstatement.analysis.request.pojo.CustomerPojo;
import com.bankstatement.analysis.request.pojo.CustomerTransactionPojo;
import com.bankstatement.analysis.request.pojo.InitiateRequestPojo;
import com.fasterxml.jackson.core.JsonProcessingException;
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
	CustomerTransactionDetailsRepo customerTransactionDetailsRepo;

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

			bankStatementAggregate.setApplicationDate(bankStatementPojo.getApplicationDate());

			bankStatementAggregate.setProcessType(bankStatementPojo.getProcessType());

			if (!CollectionUtils.isEmpty(bankStatementPojo.getCustomerList())) {
				for (CustomerPojo vo : bankStatementPojo.getCustomerList()) {
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

	public Customer getCustomerDetails(String webRefId) {
		return customerRepo.findByWebRefID(webRefId);

	}

	public void updateCustomer(String custWebRefNo, String tranWebRefNo, String status, String repStatus) {

		Customer customer = customerRepo.findByWebRefID(custWebRefNo);

		if (customer != null) {

			CustomerTransactionDetails vo = customer.getTransactionDetail().stream()
					.filter(d -> d.getWebRefID().equalsIgnoreCase(tranWebRefNo)).findFirst().orElse(null);

			if (vo != null) {

				if (!org.apache.commons.lang.StringUtils.isEmpty(status)) {
					vo.setTransactionStatus(TRANSACTION_STATUS.valueOf(status));
				}

				if (!org.apache.commons.lang.StringUtils.isEmpty(repStatus)) {
					vo.setReportStatus(REPORT_STATUS.valueOf(repStatus));
				}

				customerTransactionDetailsRepo.save(vo);
			}

		}

	}

	@Transactional
	public void fetchfeatureResponse(InitiateRequestPojo initiateRequestPojo)
			throws JsonProcessingException, ParseException, java.text.ParseException {

		List<BankTransactionDetails> bankTransactionDetails = new ArrayList<>();
		BankStatementAggregate aggregate = bankStatementAggregateRepo
				.findByWebRefID(initiateRequestPojo.getApplicationWebRefNo());

		if (aggregate != null) {

			Customer customer = aggregate.getCustomer().stream()
					.filter(d -> d.getWebRefID().equalsIgnoreCase(initiateRequestPojo.getCustomerWebRefNo()))
					.findFirst().orElse(null);
			if (customer != null) {
				for (CustomerTransactionDetails vo : customer.getTransactionDetail()) {
					if (TRANSACTION_STATUS.FAILED != vo.getTransactionStatus()
							&& !CollectionUtils.isEmpty(vo.getAccountDetail())) {
						List<BankTransactionDetails> details = new ArrayList<>();
						details = vo.getAccountDetail().stream()
								.filter(d -> ACCOUNT_STATUS.INCLUDED == d.getAccountStatus())
//								.collect(Collectors.toMap(
//										accountDetail -> accountDetail.getAcNumber() + "-"
//												+ accountDetail.getBankName(),
//										accountDetail -> accountDetail, (existing, replacement) -> existing))
								.collect(Collectors.toMap(
				                        accountDetail -> accountDetail.getAcNumber() + "-" + accountDetail.getBankName(),
				                        Function.identity(),
				                        (existing, replacement) -> existing))
								.values().stream().map(AccountDetail::getTransaction).flatMap(Collection::stream)
								.collect(Collectors.toList());
						if (!CollectionUtils.isEmpty(details)) {
							bankTransactionDetails.addAll(details);
							vo.setTransactionStatus(TRANSACTION_STATUS.COMPLETED);
							customerTransactionDetailsRepo.save(vo);
						}
					}
				}
				if (!CollectionUtils.isEmpty(bankTransactionDetails)) {
					customer.setCustomerResponse(objectMapper.writeValueAsString(
							new FeatureUtil(bankTransactionDetails, aggregate.getApplicationDate())));

					customer.setCustomerStatus(customer.getTransactionDetail().stream().allMatch(
							d -> TRANSACTION_STATUS.COMPLETED == d.getTransactionStatus()) ? CUSTOMER_STATUS.COMPLETED
									: CUSTOMER_STATUS.INPROGRESS);

					customerRepo.save(customer);
					aggregate = bankStatementAggregateRepo.findByWebRefID(initiateRequestPojo.getApplicationWebRefNo());

				}
			}

			boolean valid = aggregate.getCustomer().stream().map(Customer::getTransactionDetail)
					.flatMap(Collection::stream)
					.allMatch(d -> TRANSACTION_STATUS.COMPLETED == d.getTransactionStatus());

			if (valid) {
				bankTransactionDetails = new ArrayList<>();
				bankTransactionDetails = aggregate.getCustomer().stream().map(Customer::getTransactionDetail)
						.flatMap(Collection::stream).map(CustomerTransactionDetails::getAccountDetail)
						.flatMap(Collection::stream)
						.collect(Collectors.toMap(
		                        accountDetail -> accountDetail.getAcNumber() + "-" + accountDetail.getBankName(),
		                        Function.identity(),
		                        (existing, replacement) -> existing))
						.values().stream().map(AccountDetail::getTransaction).flatMap(Collection::stream)
						.collect(Collectors.toList());

				aggregate.setApplicationResponse(objectMapper
						.writeValueAsString(new FeatureUtil(bankTransactionDetails, aggregate.getApplicationDate())));
				aggregate.setAggregateStatus(AGGREGATE_STATUS.COMPLETED);
				bankStatementAggregateRepo.save(aggregate);
			}
		}
	}
//
//	@Transactional
//	public ResponseEntity<?> fetchfeatureResponse(BankStatementPojo bankStatementPojo)
//			throws JsonProcessingException, ParseException {
//		String response = null;
//
//		List<BankTransactionDetails> bankTransactionDetails = new ArrayList<>();
//		BankStatementAggregate aggregate = bankStatementAggregateRepo.findByWebRefID(bankStatementPojo.getWebRefNo());
//
//		if (aggregate != null) {
//			for (CustomerPojo vo : bankStatementPojo.getCustomer()) {
//				Customer customer = aggregate.getCustomer().stream()
//						.filter(d -> d.getWebRefID().equalsIgnoreCase(vo.getCustomerWebRefNo())).findFirst()
//						.orElse(null);
//
//				if (customer != null) {
//					customer.getAccountDetail().stream().forEach(d -> {
//						vo.getAccountPojo().stream().forEach(da -> {
//							if (da.getAccWebRefNo().equalsIgnoreCase(d.getWebRefID())) {
//								d.setAccountStatus(ACCOUNT_STATUS.valueOf(da.getAccountStatus().toString()));
//
//							}
//						});
//
//					});
//
//					if (REPORT_TYPE.MEMBER_WISE == aggregate.getReportType()) {
//
//						bankTransactionDetails = customer.getAccountDetail().stream()
//								.filter(d -> ACCOUNT_STATUS.INCLUDED == d.getAccountStatus())
//								.map(AccountDetail::getTransaction).flatMap(Collection::stream)
//								.collect(Collectors.toList());
//						customer.setCustomerResponse(objectMapper.writeValueAsString(
//								new FeatureUtil(bankTransactionDetails, aggregate.getApplicationDate())));
//
//						customer.setReportStatus(REPORT_STATUS.REPORT_GENERATED);
//						response = customer.getCustomerResponse();
//					}
//
//					customerRepo.save(customer);
//				}
//
//			}
//
//			bankTransactionDetails = new ArrayList<>();
//
//			if (REPORT_TYPE.APPLICATION == aggregate.getReportType()) {
//				aggregate = bankStatementAggregateRepo.findByWebRefID(bankStatementPojo.getWebRefNo());
//
//				if (!aggregate.getCustomer().stream().anyMatch(d -> d.getReportStatus() == REPORT_STATUS.INITIATED
//						|| d.getReportStatus() == REPORT_STATUS.NOT_INITIATED)) {
//
//					bankTransactionDetails = aggregate.getCustomer().stream().map(Customer::getAccountDetail)
//							.flatMap(Collection::stream).filter(d -> ACCOUNT_STATUS.INCLUDED == d.getAccountStatus())
//							.map(AccountDetail::getTransaction).flatMap(Collection::stream)
//							.collect(Collectors.toList());
//
//					aggregate.setApplicationResponse(objectMapper.writeValueAsString(
//							new FeatureUtil(bankTransactionDetails, aggregate.getApplicationDate())));
//
//					bankStatementAggregateRepo.save(aggregate);
//					response = aggregate.getApplicationResponse();
//				}
//			}
//		} else {
//			throw new CustomException("400", "Report Type Cannot be empty");
//		}
//
//		return ResponseEntity.ok(response);
//
//	}

	public CustomerTransactionDetails updateCustomerDetailWithTransaction(BankStatementPojo bankStatementPojo)
			throws Exception {

		try {
			BankStatementAggregate bankStatementAggregate = bankStatementAggregateRepo
					.findByWebRefID(bankStatementPojo.getWebRefNo());

			if (bankStatementAggregate != null) {

				if (!CollectionUtils.isEmpty(bankStatementAggregate.getCustomer())) {

					if (bankStatementPojo.getCustomer() != null) {
						Customer vo = bankStatementAggregate.getCustomer().stream().filter(
								d -> d.getWebRefID().equals(bankStatementPojo.getCustomer().getCustomerWebRefNo()))
								.findFirst().orElse(null);
						if (vo != null) {
							for (CustomerTransactionPojo tran : bankStatementPojo.getCustomer().getCustomerDetail()) {
								CustomerTransactionDetails details = new CustomerTransactionDetails();

								details.setRequestType(tran.getType());

								details.setScannedDoc(tran.isScannedDoc());

								details.setInstitutionType(tran.getInstitutionType());

								Document doc = new Document();
								doc.setImagePath(tran.getImagePath());
								details.setDocuments(doc);

								if (!StringUtils.isEmpty(tran.getWebRefNo())) {
									CustomerTransactionDetails previousTransactionDetails = vo.getTransactionDetail()
											.stream().filter(d -> d.getWebRefID().equalsIgnoreCase(tran.getWebRefNo()))
											.findFirst().orElse(null);
									if (previousTransactionDetails != null) {
										vo.getTransactionDetail().remove(previousTransactionDetails);
									}
								}
								vo.setCustomerStatus(CUSTOMER_STATUS.INPROGRESS);
								vo.addCustomerTransactionDetails(details);
								customerRepo.save(vo);
								return details;
							}
						}

					}

				}

			} else {
				throw new CustomException("400", " Invalid WebRef No ");
			}

			return null;
		} catch (

		Exception e) {
			logger.info("exception occured {}", e);
			if (e instanceof CustomException) {
				CustomException ex = (CustomException) e;
				throw new CustomException(ex.getErrorCode(), ex.getErrorMessage());
			}
			throw new Exception();
		}

	}

	public Customer updateCustomerDetailWithMultiTransaction(BankStatementPojo bankStatementPojo) throws Exception {

		try {
			BankStatementAggregate bankStatementAggregate = bankStatementAggregateRepo
					.findByWebRefID(bankStatementPojo.getWebRefNo());

			if (bankStatementAggregate != null) {

				if (!CollectionUtils.isEmpty(bankStatementAggregate.getCustomer())) {

					if (bankStatementPojo.getCustomer() != null) {
						Customer vo = bankStatementAggregate.getCustomer().stream().filter(
								d -> d.getWebRefID().equals(bankStatementPojo.getCustomer().getCustomerWebRefNo()))
								.findFirst().orElse(null);
						if (vo != null) {
							for (CustomerTransactionPojo tran : bankStatementPojo.getCustomer().getCustomerDetail()) {
								CustomerTransactionDetails details = new CustomerTransactionDetails();

								details.setRequestType(tran.getType());

								details.setScannedDoc(tran.isScannedDoc());

								details.setInstitutionType(tran.getInstitutionType());

								Document doc = new Document();
								doc.setImagePath(tran.getImagePath());
								details.setDocuments(doc);

								if (!StringUtils.isEmpty(tran.getWebRefNo())) {
									CustomerTransactionDetails previousTransactionDetails = vo.getTransactionDetail()
											.stream().filter(d -> d.getWebRefID().equalsIgnoreCase(tran.getWebRefNo()))
											.findFirst().orElse(null);
									if (previousTransactionDetails != null) {
										vo.getTransactionDetail().remove(previousTransactionDetails);
									}
								}

								vo.addCustomerTransactionDetails(details);

							}
							vo.setCustomerStatus(CUSTOMER_STATUS.INPROGRESS);
							customerRepo.save(vo);
							return vo;
						}

					}

				}

			} else {
				throw new CustomException("400", " Invalid WebRef No ");
			}

			return null;
		} catch (

		Exception e) {
			logger.info("exception occured {}", e);
			if (e instanceof CustomException) {
				CustomException ex = (CustomException) e;
				throw new CustomException(ex.getErrorCode(), ex.getErrorMessage());
			}
			throw new Exception();
		}

	}

}

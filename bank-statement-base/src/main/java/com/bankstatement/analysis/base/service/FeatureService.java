package com.bankstatement.analysis.base.service;

import java.text.ParseException;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.bankstatement.analysis.base.datamodel.ApplicationDetail;
import com.bankstatement.analysis.base.datamodel.ApplicationDetail.APPLICATION_STATUS;
import com.bankstatement.analysis.base.datamodel.BankStatementInitiate;
import com.bankstatement.analysis.base.datamodel.BankTransactionDetails;
import com.bankstatement.analysis.base.datamodel.BankTransactionDetails.CATEGORY_TYPE;
import com.bankstatement.analysis.base.helper.FeatureHelper;
import com.bankstatement.analysis.base.helper.FeatureUtil;
import com.bankstatement.analysis.base.repo.ApplicationDetailRepository;
import com.bankstatement.analysis.request.pojo.InitiateRequestPojo;
import com.bankstatement.analysis.transaction.pojo.BankAccountDetails;
import com.bankstatement.analysis.transaction.pojo.Xn;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class FeatureService {

	private static final Logger logger = LoggerFactory.getLogger(FeatureService.class);

	@Autowired
	ApplicationDetailRepository applicationDetailRepository;

	@Autowired
	BankStatementImpl bankStatementImpl;

	ObjectMapper objectMapper = new ObjectMapper();

	@Async
	public void saveApplicationDetails(InitiateRequestPojo initiate) {
		ApplicationDetail applicationDetail = applicationDetailRepository
				.findByApplicationReferenceNo(initiate.getApplicationReferenceNo());

		if (applicationDetail == null) {
			applicationDetail = new ApplicationDetail();
			applicationDetail.setApplicationReferenceNo(initiate.getApplicationReferenceNo());

		}
		applicationDetail.setApplicationDate(initiate.getApplicationDate());
		applicationDetail.setStatus(APPLICATION_STATUS.INITIATED);
		applicationDetail.setResponse(null);
		applicationDetailRepository.save(applicationDetail);

	}

	@Async
	public void constructFeature(String responseBody, BankStatementInitiate initiate)
			throws JsonMappingException, JsonProcessingException, ParseException {
		ApplicationDetail applicationDetail = applicationDetailRepository
				.findByApplicationReferenceNo(initiate.getApplicationReferenceNo());

		if (applicationDetail == null) {
			applicationDetail = new ApplicationDetail();
			applicationDetail.setApplicationReferenceNo(initiate.getApplicationReferenceNo());
			applicationDetail.setApplicationDate(initiate.getApplicationDate());
		}

		JsonNode jsonNode = objectMapper.readTree(responseBody);

		JsonNode accountDetails = jsonNode.get("accountXns");

		List<BankAccountDetails> bankDetails = objectMapper.readValue(accountDetails.toString(),
				new TypeReference<List<BankAccountDetails>>() {
				});

		for (BankAccountDetails det : bankDetails) {
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

				applicationDetail.addFlowDetails(details);

			}
			logger.info("{}", applicationDetail.getTransactionDetails());
		}

		applicationDetailRepository.save(applicationDetail);
		helper(applicationDetail);
	}

	public void reTrigger(String responseBody, BankStatementInitiate initiate)
			throws JsonMappingException, JsonProcessingException, ParseException {
		ApplicationDetail applicationDetail = applicationDetailRepository
				.findByApplicationReferenceNo(initiate.getApplicationReferenceNo());
		helper(applicationDetail);

	}

	public void helper(ApplicationDetail applicationDetail)
			throws ParseException, JsonMappingException, JsonProcessingException {
		if (!CollectionUtils.isEmpty(applicationDetail.getTransactionDetails())) {

			FeatureUtil helper = new FeatureUtil(applicationDetail);

			applicationDetail.setResponse(objectMapper.writeValueAsString(helper));
			if (StringUtils.isNotEmpty(applicationDetail.getResponse())) {
				applicationDetail.setStatus(APPLICATION_STATUS.COMPLETED);
				applicationDetailRepository.save(applicationDetail);
			}
		}
	}

	public void extr(String appln) throws ParseException, JsonProcessingException {
		ApplicationDetail applicationDetail = applicationDetailRepository.findByApplicationReferenceNo(appln);
		if (!CollectionUtils.isEmpty(applicationDetail.getTransactionDetails())) {
			FeatureHelper helper = new FeatureHelper(applicationDetail);

			applicationDetail.setResponse(objectMapper.writeValueAsString(helper));
			if (StringUtils.isNotEmpty(applicationDetail.getResponse())) {
				applicationDetail.setStatus(APPLICATION_STATUS.COMPLETED);
				applicationDetailRepository.save(applicationDetail);
			}
		}
	}

	public ResponseEntity<?> deleteInitiatedRequest(String processId) throws Exception {
		HashMap<String, String> details = new HashMap<>();

		BankStatementInitiate bankStatementInit = bankStatementImpl.getBankStatementInitiateByProcessId(processId);
		if (bankStatementInit != null) {
			String requestId = bankStatementInit.getRequestId();
			String applicationNo = bankStatementInit.getApplicationReferenceNo();
			bankStatementImpl.bsInitiateRepository.deleteById(bankStatementInit.getId());
			bankStatementImpl.bsTransactionRepository.deleteByProcessId(processId);
			bankStatementImpl.bsTransactionRepository.deleteByProcessId(processId);
			ApplicationDetail applicationDetail = applicationDetailRepository
					.findByApplicationReferenceNo(applicationNo);
			if (applicationDetail != null) {
				List<BankTransactionDetails> bankTransaction = applicationDetailRepository
						.findTransactionDetailsByRequestId(requestId);
				if (!CollectionUtils.isEmpty(bankTransaction)) {
					applicationDetail.getTransactionDetails().removeAll(bankTransaction);
					if (CollectionUtils.isEmpty(applicationDetail.getTransactionDetails())) {
						applicationDetail.setStatus(APPLICATION_STATUS.INITIATED);
						applicationDetail.setResponse(null);
					} else {
						// TODO retrigger
					}
					applicationDetailRepository.save(applicationDetail);
				}
			}

		}
		details.put("message", "Deleted successfully");
		return ResponseEntity.ok(details);
	}

	public ResponseEntity<?> fetchfeatureResponse(String applicationReferenceNo) {
		ApplicationDetail applicationDetail = applicationDetailRepository
				.findByApplicationReferenceNo(applicationReferenceNo);
		if (applicationDetail != null && APPLICATION_STATUS.COMPLETED == applicationDetail.getStatus()) {
			return ResponseEntity.ok(applicationDetail.getResponse());
		}

		return null;

	}

}

package com.bankstatement.analysis.base.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bankstatement.analysis.base.datamodel.ApplicationDetail;
import com.bankstatement.analysis.base.datamodel.BankStatementInitiate;
import com.bankstatement.analysis.base.datamodel.BankTransactionDetails;
import com.bankstatement.analysis.base.datamodel.BankTransactionDetails.CATEGORY_TYPE;
import com.bankstatement.analysis.base.repo.ApplicationDetailRepository;
import com.bankstatement.analysis.transaction.pojo.TransactionDetails;

@Service
public class FeatureService {

	private static final Logger logger = LoggerFactory.getLogger(FeatureService.class);

	@Autowired
	ApplicationDetailRepository applicationDetailRepository;

	public void constructFeature(String responseBody, BankStatementInitiate initiate) {
		ApplicationDetail applicationDetail = applicationDetailRepository
				.findByApplicationReferenceNo(initiate.getApplicationReferenceNo());

		if (applicationDetail == null) {
			applicationDetail = new ApplicationDetail();
			applicationDetail.setApplicationReferenceNo(initiate.getApplicationReferenceNo());
			applicationDetail.setApplicationDate(initiate.getApplicationDate());
		}

		// TODO
		List<TransactionDetails> transactionDetails = new ArrayList<>();

		for (TransactionDetails d : transactionDetails) {
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

			if (CATEGORY_TYPE.OUTFLOW == details.getCategoryType()) {
				if ("Transfer To".contains(d.getCategory().toUpperCase())) {
					details.setCategory("Transfer out");
				} else if ("Transfer To".contains(d.getCategory().toUpperCase())) {
					details.setCategory("Transfer in");
				}
			}

			if (StringUtils.isEmpty(details.getCategory())) {
				details.setCategory(d.getCategory());
			}

			applicationDetail.addFlowDetails(details);

		}
		applicationDetailRepository.save(applicationDetail);
	}

}

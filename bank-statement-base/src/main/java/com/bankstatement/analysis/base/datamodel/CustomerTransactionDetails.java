package com.bankstatement.analysis.base.datamodel;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;

@Entity
@Data
@Table(name = "customer_transaction_detail")
@EqualsAndHashCode(callSuper = true)
public class CustomerTransactionDetails extends BaseEntity {

	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@OrderBy("id")
	@JoinColumn(name = "customer_details_id")
	@Setter(AccessLevel.NONE)
	@JsonIgnore
	private Set<AccountDetail> accountDetail = new HashSet<>();

	@OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	private Document documents;

	@JsonProperty("request_type")
	@Column(name = "request_type")
	private String requestType;

	@JsonProperty("institution_type")
	@Column(name = "institution_type")
	private String institutionType;

	@JsonProperty("scanned_doc")
	@Column(name = "scanned_doc")
	private boolean scannedDoc=false;

	@JsonProperty("report_status")
	@Column(name = "report_status")
	@Enumerated(EnumType.STRING)
	private REPORT_STATUS reportStatus = REPORT_STATUS.NOT_INITIATED;

	public enum REPORT_STATUS {
		NOT_INITIATED, INITIATED, CALLBACK, REPORT
	}

	@JsonProperty("transaction_status")
	@Column(name = "transaction_status")
	@Enumerated(EnumType.STRING)
	private TRANSACTION_STATUS transactionStatus = TRANSACTION_STATUS.INPROGRESS;

	public enum TRANSACTION_STATUS {
		INPROGRESS, COMPLETED, FAILED
	}

	private String message;

	@JsonIgnore
	public void addAccountDetails(AccountDetail env) {
		if (env != null) {
			try {
				this.accountDetail.add(env);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@JsonIgnore
	public void addAccountDetailsList(List<AccountDetail> env) {
		if (!CollectionUtils.isEmpty(env)) {
			try {
				this.accountDetail.addAll(env);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}

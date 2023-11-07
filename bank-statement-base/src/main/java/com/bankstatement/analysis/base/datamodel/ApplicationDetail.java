package com.bankstatement.analysis.base.datamodel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

@Data
@Entity
@Table(name = "application_detail")
public class ApplicationDetail implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8541376050792806177L;

	@Column(name = "application_ref_no")
	private String applicationReferenceNo;

	@Column(name = "application_date")
	private String applicationDate;

	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@OrderBy("id")
	@JoinColumn(name = "application_id")
	@Setter(AccessLevel.NONE)
	private List<BankTransactionDetails> transactionDetails = new ArrayList<>();

	@JsonIgnore
	public void addFlowDetails(BankTransactionDetails env) {
		if (env != null) {
			try {
				this.transactionDetails.add(env);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@JsonIgnore
	public void addTransferDetails(List<BankTransactionDetails> env) {
		if (!CollectionUtils.isEmpty(env)) {
			try {
				this.transactionDetails.addAll(env);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}

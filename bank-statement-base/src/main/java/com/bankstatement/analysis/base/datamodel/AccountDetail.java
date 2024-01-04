package com.bankstatement.analysis.base.datamodel;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import org.springframework.util.CollectionUtils;

import com.bankstatement.analysis.base.datamodel.Customer.CUSTOMER_TYPE;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;

@Entity
@Data
@Table(name = "account_details")
@EqualsAndHashCode(callSuper = false)
public class AccountDetail extends BaseEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1233843827932887171L;

	@Column(name = "account_holder_name")
	@JsonProperty("account_holder_name")
	private String accountHolderName;

	@Column(name = "bank_name")
	@JsonProperty("bank_name")
	private String bankName;

	@Column(name = "branch_name")
	@JsonProperty("branch_name")
	private String branchName;

	@Column(name = "ac_number")
	@JsonProperty("ac_number")
	private String acNumber;

	@Column(name = "ifsc_code")
	@JsonProperty("ifsc_code")
	private String ifscCode;

	@Column(name = "account_status")
	@Enumerated(EnumType.STRING)
	private ACCOUNT_STATUS accountStatus = ACCOUNT_STATUS.INCLUDED;

	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@OrderBy("id")
	@JoinColumn(name = "account_id")
	@Setter(AccessLevel.NONE)
	private List<BankTransactionDetails> transaction = new ArrayList<>();

	public enum ACCOUNT_STATUS {
		INCLUDED, NOT_INCLUDED
	}

	@JsonIgnore
	public void addTransactionDetails(BankTransactionDetails env) {
		if (env != null) {
			try {
				this.transaction.add(env);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@JsonIgnore
	public void addTransactionDetailsList(List<BankTransactionDetails> env) {
		if (!CollectionUtils.isEmpty(env)) {
			try {
				this.transaction.addAll(env);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}

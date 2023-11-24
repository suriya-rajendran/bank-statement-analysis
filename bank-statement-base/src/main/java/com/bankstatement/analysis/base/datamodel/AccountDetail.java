package com.bankstatement.analysis.base.datamodel;

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

import com.bankstatement.analysis.base.datamodel.Customer.CUSTOMER_TYPE;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;

@Entity
@Data
@Table(name = "account_details")
@EqualsAndHashCode(callSuper = true)
public class AccountDetail extends BaseEntity {

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
	private ACCOUNT_STATUS accountStatus;

	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@OrderBy("id")
	@JoinColumn(name = "account_id")
	@Setter(AccessLevel.NONE)
	private List<BankTransactionDetails> account = new ArrayList<>();

	public enum ACCOUNT_STATUS {
		INCLUDED, NOT_INCLUDED
	}
}

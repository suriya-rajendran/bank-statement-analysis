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
import javax.persistence.Lob;
import javax.persistence.OneToMany;
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
@Table(name = "customer")
@EqualsAndHashCode(callSuper = true)
public class Customer extends BaseEntity {

	@Column(name = "customer_reference_no")
	@JsonProperty("customer_reference_no")
	private String customerReferenceNo;

	@Column(name = "customer_type")
	@Enumerated(EnumType.STRING)
	private CUSTOMER_TYPE customerType;

	@Lob
	@Column(name = "customer_response")
	private String customerResponse;

	public enum CUSTOMER_TYPE {
		APPLICANT, CO_APPLICANT
	}

	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@OrderBy("id")
	@JoinColumn(name = "customer_id")
	@Setter(AccessLevel.NONE)
	@JsonIgnore
	private Set<CustomerTransactionDetails> transactionDetail = new HashSet<>();

	@JsonIgnore
	public void addCustomerTransactionDetails(CustomerTransactionDetails env) {
		if (env != null) {
			try {
				this.transactionDetail.add(env);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@JsonIgnore
	public void addCustomerTransactionDetails(Set<CustomerTransactionDetails> env) {
		if (!CollectionUtils.isEmpty(env)) {
			try {
				this.transactionDetail.addAll(env);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}

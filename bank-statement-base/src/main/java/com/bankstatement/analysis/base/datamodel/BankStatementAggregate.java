package com.bankstatement.analysis.base.datamodel;

import java.util.ArrayList;
import java.util.List;

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
@Table(name = "bankstatement_aggregate")
@EqualsAndHashCode(callSuper = true)
public class BankStatementAggregate extends BaseEntity {

	@Column(name = "application_reference_no")
	@JsonProperty("application_reference_no")
	private String applicationReferenceNo;

	@Column(name = "tenure")
	@JsonProperty("tenure")
	private Integer tenure;

	@Column(name = "process_type")
	@JsonProperty("process_type")
	private String processType;

	@Column(name = "loan_amount")
	@JsonProperty("loan_amount")
	private Double loanamount;

	@Column(name = "report_type")
	@Enumerated(EnumType.STRING)
	private REPORT_TYPE reportType;

	@Lob
	@Column(name = "application_response")
	private String applicationResponse;

	@Column(name = "application_date")
	private String applicationDate;

	@Column(name = "aggregate_status")
	@Enumerated(EnumType.STRING)
	private AGGREGATE_STATUS aggregateStatus = AGGREGATE_STATUS.PENDING;

	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@OrderBy("id")
	@JoinColumn(name = "aggregate_id")
	@Setter(AccessLevel.NONE)
	private List<Customer> customer = new ArrayList<>();

	public enum REPORT_TYPE {
		APPLICATION, MEMBER_WISE
	}

	public enum AGGREGATE_STATUS {
		PENDING, COMPLETED
	}

	@JsonIgnore
	public void addCustomer(Customer env) {
		if (env != null) {
			try {
				this.customer.add(env);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@JsonIgnore
	public void addCustomerList(List<Customer> env) {
		if (!CollectionUtils.isEmpty(env)) {
			try {
				this.customer.addAll(env);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}

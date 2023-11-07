package com.bankstatement.analysis.base.datamodel;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
 

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@EqualsAndHashCode(callSuper = false)
@Table(name = "bank_category1_flow_detail")
public class BankCategoryFlowTransaction extends CategoryBaseModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4875443196924289791L;
	@Column(name = "category_type")
	@Enumerated(EnumType.STRING)
	public CATEGORY_TYPE1 categoryType;


	public enum CATEGORY_TYPE1 {
		OUTFLOW, INFLOW;
	}
}

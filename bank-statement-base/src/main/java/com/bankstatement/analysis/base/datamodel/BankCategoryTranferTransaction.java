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
@Table(name = "bank_category2_transfer_detail")
public class BankCategoryTranferTransaction extends CategoryBaseModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5876144099764360682L;

	@Column(name = "category_type")
	@Enumerated(EnumType.STRING)
	public CATEGORY_TYPE2 categoryType;

	public enum CATEGORY_TYPE2 {
		OUTFLOW, INFLOW;
	}
}

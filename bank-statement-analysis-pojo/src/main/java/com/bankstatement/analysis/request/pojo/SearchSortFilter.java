package com.bankstatement.analysis.request.pojo;

import lombok.Data;

@Data
public class SearchSortFilter {

	private String searchKey;
	private String searchValue;
	private String sortBy;
	private String sortOrder;
	private String filterValue;

}

package com.bankstatement.analysis.rest;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bankstatement.analysis.base.service.DashboardService;
import com.bankstatement.analysis.request.pojo.PagedData;

@RestController
@RequestMapping("/rest/bank")
public class DashboardController {

	public final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

	private static final String PARAM_SEARCH_PARAM = "search_param";

	private static final String PARAM_SEARCH_TYPE = "search_type";

	private static final String PARAM_STATUS = "status";

	@Autowired
	DashboardService dashboardService;

	@GetMapping(value = "/fetch/initated-tasks")
	public PagedData getMyTaskSearch(HttpServletRequest request, @RequestParam(defaultValue = "1") Integer pageNo,
			@RequestParam(defaultValue = "10") Integer pageSize,
			@RequestParam(defaultValue = "id,desc") String[] sortBy,
			@RequestParam(value = PARAM_SEARCH_TYPE, defaultValue = "", required = false) String searchType,
			@RequestParam(value = PARAM_SEARCH_PARAM, defaultValue = "", required = false) String searchQuery) {
		return dashboardService.fetchInitiatedTasks(pageNo - 1, pageSize, sortBy, searchType, searchQuery,  
				getProductCode(request));
	}

	private String getProductCode(HttpServletRequest request) {
		return (String) request.getAttribute("product_code");
	}
}
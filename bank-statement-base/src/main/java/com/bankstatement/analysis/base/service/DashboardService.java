package com.bankstatement.analysis.base.service;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.bankstatement.analysis.base.datamodel.BankStatementBaseModel.STATUS;
import com.bankstatement.analysis.base.datamodel.BankStatementInitiate;
import com.bankstatement.analysis.base.datamodel.BankStatementInitiate.PENNYDROPSTATUS;
import com.bankstatement.analysis.request.pojo.InitiatedTask;
import com.bankstatement.analysis.request.pojo.PagedData;
import com.bankstatement.analysis.request.pojo.SearchSortFilter;

@Service
public class DashboardService {

	public final static Logger logger = LoggerFactory.getLogger(DashboardService.class);

	@Autowired
	EntityManager manager;

	public static final String INBETWEEN = "InBetween";
	public static final String GREATERTHANOREQUALTO = "GreaterThanOrEqualTo";
	public static final String LESSERTHANOREQUALTO = "LesserThanOrEqualTo";
	public static final String LESSERTHAN = "LesserThan";
	public static final String GREATERTHAN = "GreaterThan";
	public static final String EQUALTO = "EqualTo";

	public static final String PROCESS_ID = "ProcessId";
	public static final String PRODUCT_CODE = "ProductCode";
	public static final String REQUEST_ID = "RequestId";
	public static final String REQUEST_DATE = "Request_date";
	public static final String NAME = "Name";
	public static final String ID = "ID";
	public static final String PROCESS_TYPE = "processType";

	public PagedData fetchInitiatedTasks(Integer pageNo, Integer pageSize, String[] sortBy, String searchType,
			String searchQuery, String productCode) {
		List<InitiatedTask> result = new ArrayList<>();
		Integer totalSize = 0;
		try {
			CriteriaBuilder cb = manager.getCriteriaBuilder();
			CriteriaQuery<InitiatedTask> query = cb.createQuery(InitiatedTask.class);

			Root<?> root = query.from(BankStatementInitiate.class);

			SearchSortFilter sr = new SearchSortFilter();
			sr.setSearchKey(searchType);
			sr.setSearchValue(searchQuery);
			sr.setSortBy(sortBy[0]);
			sr.setSortOrder(sortBy[1].toUpperCase());

			result = manager.createQuery(getMyApplicationQuery(root, query, cb, sr, productCode))
					.setFirstResult(pageNo * pageSize).setMaxResults(pageSize).getResultList();

			if (!CollectionUtils.isEmpty(result)) {
				CriteriaQuery<Object[]> queryCount = cb.createQuery(Object[].class);
				root = queryCount.from(BankStatementInitiate.class);
				Query qry = manager.createQuery(getMyApplicationTotalSizeQuery(root, queryCount, cb, sr, productCode));
				totalSize = Integer.parseInt(qry.getSingleResult().toString());
			} else {
				result = new ArrayList<>();
			}
		} catch (Exception e) {
			logger.info("error occured while fetching initated tasks for" + productCode, e);
		}

		return new PagedData(result, totalSize, result.size());
	}

	public CriteriaQuery<InitiatedTask> getMyApplicationQuery(Root<?> root, CriteriaQuery<InitiatedTask> query,
			CriteriaBuilder cb, SearchSortFilter sr, String productCode) {
		List<Predicate> predicates = getMyApplicationPredicate(root, cb, sr, productCode);
		// Sorting
		switch (sr.getSortBy().toUpperCase()) {
		case REQUEST_DATE:
			List<Order> loginDate = new ArrayList<>();
			if ("ASC".equalsIgnoreCase(sr.getSortOrder())) {
				Order asc = cb.asc(root.get("requestDate"));
				loginDate.add(asc);
			} else {
				Order desc = cb.desc(root.get("requestDate"));
				loginDate.add(desc);
			}
			query.orderBy(loginDate);
			break;
		case ID:
			List<Order> ids = new ArrayList<>();
			if ("ASC".equalsIgnoreCase(sr.getSortOrder())) {
				Order asc = cb.asc(root.get("id"));
				ids.add(asc);
			} else {
				Order desc = cb.desc(root.get("id"));
				ids.add(desc);
			}
			query.orderBy(ids);
			break;

		}
		Expression<String> pennyDropStatus = cb.literal(""); // Initialize with an empty string

		for (PENNYDROPSTATUS enumValue : PENNYDROPSTATUS.values()) {
			pennyDropStatus = cb.<String>selectCase()
					.when(cb.equal(root.get("pennyDropStatus"), enumValue), enumValue.toString())
					.otherwise(pennyDropStatus);
		}

		Expression<String> status = cb.literal(""); // Initialize with an empty string

		for (STATUS enumValue : STATUS.values()) {
			status = cb.<String>selectCase().when(cb.equal(root.get("status"), enumValue), enumValue.toString())
					.otherwise(status);
		}

		query.select(cb.construct(InitiatedTask.class, root.get("processId"), root.get("requestId"),
				root.get("processType"), root.get("requestType"), root.get("name"), root.get("nameMatch"),
				root.get("pennyDropVerification"), pennyDropStatus, status, root.get("requestDate")));
		query.where(predicates.stream().toArray(Predicate[]::new));
		return query;

	}

	private List<Predicate> getMyApplicationPredicate(Root<?> root, CriteriaBuilder cb, SearchSortFilter sr,
			String productCode) {
		List<Predicate> predicates = new ArrayList<>();
		predicates.add(cb.equal(root.get("productCode").as(String.class), productCode));

		// Searching
		switch (sr.getSearchKey()) {
		case PROCESS_ID:
			predicates.add(cb.equal(root.get("processId").as(String.class), sr.getSearchValue()));
			break;
		case REQUEST_DATE:
			Path<Timestamp> receivedDate = root.<Timestamp>get("requestDate");
			predicates.add(getPredicateForDate(receivedDate, cb, "requestDate", sr.getSearchValue()));
			break;
		case REQUEST_ID:
			predicates.add(cb.equal(root.get("requestId").as(String.class), sr.getSearchValue()));
			break;
		case PROCESS_TYPE:
			predicates.add(cb.equal(root.get("processType").as(String.class), sr.getSearchValue()));
			break;
		case NAME:
			predicates.add(cb.equal(root.get("name").as(String.class), sr.getSearchValue()));
			break;

		}
		return predicates;
	}

	public CriteriaQuery<Object[]> getMyApplicationTotalSizeQuery(Root<?> root, CriteriaQuery<Object[]> query,
			CriteriaBuilder cb, SearchSortFilter sr, String productCode) {
		List<Predicate> predicates = getMyApplicationPredicate(root, cb, sr, productCode);

		query.multiselect(cb.count(root));
		query.where(predicates.stream().toArray(Predicate[]::new)).distinct(true);
		return query;

	}

	public static Predicate getPredicateForDate(Path<Timestamp> dateModifiedPath, CriteriaBuilder cb, String fieldName,
			String searchValue) {
		Date dateModSearch = null;
		LocalDate ldMod = null;
		LocalDate daMod = null;
		String fromDate = null;
		String toDate = null;

		if (searchValue.indexOf(INBETWEEN) != -1) {// 13/09/2020InBetween15/09/2020 Sample data for fromto
													// Search
			fromDate = searchValue.split(INBETWEEN)[0];
			toDate = searchValue.split(INBETWEEN)[1];
		} else if (searchValue.indexOf(LESSERTHANOREQUALTO) != -1) {
			fromDate = searchValue.split(LESSERTHANOREQUALTO)[0];
		} else if (searchValue.indexOf(GREATERTHANOREQUALTO) != -1) {
			fromDate = searchValue.split(GREATERTHANOREQUALTO)[0];
		} else if (searchValue.indexOf(LESSERTHAN) != -1) {
			fromDate = searchValue.split(LESSERTHAN)[0];
		} else if (searchValue.indexOf(GREATERTHAN) != -1) {
			fromDate = searchValue.split(GREATERTHAN)[0];
		} else if (searchValue.indexOf(EQUALTO) != -1) {
			fromDate = searchValue.split(EQUALTO)[0];
		} else {
			fromDate = searchValue;
		}
		try {
			dateModSearch = new SimpleDateFormat("dd/MM/yyyy").parse(fromDate);
			ldMod = dateModSearch.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			if (toDate != null) {
				dateModSearch = new SimpleDateFormat("dd/MM/yyyy").parse(toDate);
				daMod = dateModSearch.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
				daMod = daMod.plusDays(1);
			} else {
				daMod = ldMod.plusDays(1);
			}
		} catch (Exception e) {
			logger.error("util date conversion issue {}, {}", fieldName, e.getMessage());
		}

		Timestamp tmFrom = Timestamp.valueOf(ldMod.atStartOfDay());
		Timestamp timeTo = Timestamp.valueOf(daMod.atStartOfDay());

		if (searchValue.indexOf(INBETWEEN) != -1) {// 13/09/2020InBetween15/09/2020 Sample data for fromto
													// Search
			return cb.between(dateModifiedPath, tmFrom, timeTo);
		} else if (searchValue.indexOf(LESSERTHANOREQUALTO) != -1) {

			return cb.lessThanOrEqualTo(dateModifiedPath.as(java.sql.Date.class), dateModSearch);
		} else if (searchValue.indexOf(GREATERTHANOREQUALTO) != -1) {
			return cb.greaterThanOrEqualTo(dateModifiedPath, tmFrom);
		} else if (searchValue.indexOf(LESSERTHAN) != -1) {
			return cb.lessThan(dateModifiedPath, tmFrom);
		} else if (searchValue.indexOf(GREATERTHAN) != -1) {
			return cb.greaterThan(dateModifiedPath, tmFrom);
		} else if (searchValue.indexOf(EQUALTO) != -1) {
			return cb.between(dateModifiedPath, tmFrom, timeTo);
		} else {
			return cb.between(dateModifiedPath, tmFrom, timeTo);
		}
	}

}

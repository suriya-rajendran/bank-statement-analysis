package com.bankstatement.analysis.security.configuration;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import com.bankstatement.analysis.base.datamodel.Product;
import com.bankstatement.analysis.base.datamodel.ProductDetails.PRODUCT_DETAILS_SERVICE;
import com.bankstatement.analysis.base.repo.ProductRepository;
import com.bankstatement.analysis.base.util.EncryptionDecryptionUtil;
import com.bankstatement.analysis.request.pojo.CustomException;

public class Interceptor implements HandlerInterceptor {

	private final ProductRepository productRepository;

	@Autowired
	public Interceptor(ProductRepository productRepository) {
		this.productRepository = productRepository;
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {

		boolean valid = false;
		try {
			String token = request.getHeader("token");
			List<String> data = EncryptionDecryptionUtil.decryptString(token);
			if (CollectionUtils.isEmpty(data) && data.size() != 3) {
				throw new CustomException("400", "Invalid Token");
			}
			Product product = productRepository.findByProductCode(data.get(0));
			if (product == null) {
				throw new CustomException("400", "Invalid Token");
			}
			valid = product.validToken(PRODUCT_DETAILS_SERVICE.valueOf(data.get(1)), data.get(2));
			request.setAttribute("product_code", data.get(0));

			if (!valid) {
				throw new CustomException("400", "Invalid Token");
			}
		} catch (CustomException e) {
			throw e;
		} catch (Exception e) {
			throw new Exception();
		}
		return valid;
	}

}

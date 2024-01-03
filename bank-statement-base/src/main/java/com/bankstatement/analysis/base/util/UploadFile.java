package com.bankstatement.analysis.base.util;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

@WebServlet("/uploadFile")
//@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 15, // 2MB
//		maxFileSize = 1024 * 1024 * 50, // 10MB
//		maxRequestSize = 1024 * 1024 * 50)
public class UploadFile extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6167432407541876334L;

	Logger logger = LoggerFactory.getLogger(this.getClass().getName());
	private String uploadDir;
	private List<String> extensionTypes;

	public UploadFile(String rootPath, String allowedExtensions) {
		String[] arr = allowedExtensions.split(",");
		extensionTypes = Arrays.asList(arr);
		uploadDir = rootPath;
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Map<String, String> responseBody = new HashMap<>();
		try {

			if (ServletFileUpload.isMultipartContent(request)) {
				try {
					List<FileItem> items = new ServletFileUpload(new DiskFileItemFactory()).parseRequest(request);

					for (FileItem item : items) {
						if (!item.isFormField()) {
							// Process the file

							String fileNameWithExtension = new File(item.getName()).getName();

							String fileName = FilenameUtils.getBaseName(fileNameWithExtension);

							fileName = fileName.replaceAll("[^a-zA-Z0-9\\._-]+", "_");
							fileName += "_" + UUID.randomUUID().toString();
							
							String extension = fileNameWithExtension.substring(fileNameWithExtension.lastIndexOf("."));
							
							if (allowedMIMETypes(extension)) {
								String filePath = uploadDir + File.separator + fileName+extension;

								item.write(new File(filePath));

								responseBody.put("filePath", filePath);
								responseBody.put("fileName", fileName+extension);

								response.getWriter().write(new ObjectMapper().writeValueAsString(responseBody));
							}
						}
					}
				} catch (Exception e) {
					throw new Exception();
				}
			} else {
				throw new Exception();
			}

		} catch (Exception e) {
			responseBody.put("error", "Error Saving File");
			response.getWriter().write(new ObjectMapper().writeValueAsString(responseBody));
		}
	}

	private boolean allowedMIMETypes(String extension) {
		if (extensionTypes.contains(extension)) {
			return true;
		}
		return false;

	}

}
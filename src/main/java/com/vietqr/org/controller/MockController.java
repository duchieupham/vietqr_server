package com.vietqr.org.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vietqr.org.dto.MockApiDTO;
import com.vietqr.org.dto.PageResultDTO;
import com.vietqr.org.util.EnvironmentUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("mock/api")
public class MockController {

    private final ObjectMapper mapper = new ObjectMapper();

    private static final Logger logger = Logger.getLogger(MockController.class);

    @Value("${mock.file.api.path}")
    private String mockFilePath;

    @Autowired
    private Environment env;

    @RequestMapping(value = "/{prefix}/{url}", method = RequestMethod.GET)
    public ResponseEntity<Object> handleGet(@PathVariable String url,
                                            @PathVariable String prefix,
                                            @RequestParam Map<String, String> requestParams) {
        return handleRequest("GET",prefix, url, requestParams, null);
    }

    @RequestMapping(value = "/{prefix}/{url}", method = RequestMethod.POST)
    public ResponseEntity<Object> handlePost(@PathVariable String url,
                                             @PathVariable String prefix,
                                             @RequestParam Map<String, String> requestParams,
                                             @RequestBody(required = false) Object requestBody) {
        return handleRequest("POST",prefix, url, requestParams, requestBody);
    }

    @RequestMapping(value = "/{prefix}/{url}", method = RequestMethod.PUT)
    public ResponseEntity<Object> handlePut(@PathVariable String url,
                                            @PathVariable String prefix,
                                            @RequestParam Map<String, String> requestParams,
                                            @RequestBody(required = false) Object requestBody) {
        return handleRequest("PUT",prefix, url, requestParams, requestBody);
    }

    @RequestMapping(value = "/{prefix}/{url}", method = RequestMethod.DELETE)
    public ResponseEntity<Object> handleDelete(@PathVariable String url,
                                               @PathVariable String prefix,
                                               @RequestParam Map<String, String> requestParams) {
        return handleRequest("DELETE",prefix, url, requestParams, null);
    }

    @RequestMapping(value = "/{url}", method = RequestMethod.GET)
    public ResponseEntity<Object> handleGet(@PathVariable String url,
                                            @RequestParam Map<String, String> requestParams) {
        return handleRequest("GET", url, requestParams, null);
    }

    @RequestMapping(value = "/{url}", method = RequestMethod.POST)
    public ResponseEntity<Object> handlePost(@PathVariable String url,
                                             @RequestParam Map<String, String> requestParams,
                                             @RequestBody(required = false) Object requestBody) {
        return handleRequest("POST", url, requestParams, requestBody);
    }

    @RequestMapping(value = "/{url}", method = RequestMethod.PUT)
    public ResponseEntity<Object> handlePut(@PathVariable String url,
                                            @RequestParam Map<String, String> requestParams,
                                            @RequestBody(required = false) Object requestBody) {
        return handleRequest("PUT", url, requestParams, requestBody);
    }

    @RequestMapping(value = "/{url}", method = RequestMethod.DELETE)
    public ResponseEntity<Object> handleDelete(@PathVariable String url,
                                               @RequestParam Map<String, String> requestParams) {
        return handleRequest("DELETE", url, requestParams, null);
    }

    private ResponseEntity<Object> handleRequest(String method, String url,
                                                 Map<String, String> requestParams,
                                                 Object requestBody) {
        try {
            List<MockApiDTO> mockApis = new ArrayList<>();
            if (EnvironmentUtil.isProduction()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            File file = new File(mockFilePath);
            if (!file.exists()) {
                throw new FileNotFoundException("Mock API file not found at path: " + mockFilePath);
            }
            mockApis = mapper.readValue(file, new TypeReference<List<MockApiDTO>>() {
            });
            for (MockApiDTO mockApi : mockApis) {
                if (mockApi.getUrl().equals(url)
                        && mockApi.getMethod().equalsIgnoreCase(method)) {
                    if (mockApi.getRequestParams() != null
                            && !mockApi.getRequestParams().keySet()
                            .equals(requestParams.keySet())) {
                        continue;
                    }

                    if (mockApi.getRequestBody() != null
                            && !hasSameProperties(mockApi.getRequestBody(), requestBody)) {
                        continue;
                    }

                    // Check if pagination is enabled
                    if (mockApi.getPaging() != null
                            && mockApi.getPaging()
                            && "GET".equalsIgnoreCase(method)) {
                        int page = requestParams.containsKey("page") ? Integer.parseInt(requestParams.get("page")) : 1;
                        int pageSize = 20;
                        int startIndex = (page - 1) * pageSize;
                        int endIndex = startIndex + pageSize;
                        PageResultDTO pageResult = new PageResultDTO();
                        pageResult.setPage(page);
                        pageResult.setSize(pageSize);
                        List<Object> responseList = (List<Object>) mockApi.getResponseBody();
                        int totalElement = responseList.size();
                        pageResult.setTotalPage(totalElement % pageSize == 0 ? totalElement / pageSize : totalElement / pageSize + 1);
                        pageResult.setTotalElement(totalElement);
                        if (startIndex >= responseList.size()) {
                            // If the start index exceeds the list size, return an empty response
                            return new ResponseEntity<>(Collections.emptyList(), HttpStatus.OK);
                        }
                        // Ensure endIndex doesn't exceed the list size
                        endIndex = Math.min(endIndex, responseList.size());

                        List<Object> paginatedResponse = responseList.subList(startIndex, endIndex);
                        pageResult.setItems(paginatedResponse);
                        return new ResponseEntity<>(pageResult, HttpStatus.OK);
                    } else {
                        // Return full response if pagination is not enabled or request method is not GET
                        return new ResponseEntity<>(mockApi.getResponseBody(), HttpStatus.valueOf(mockApi.getResponseStatus()));
                    }
                }
            }

            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    private ResponseEntity<Object> handleRequest(String method, String prefix, String url,
                                                 Map<String, String> requestParams,
                                                 Object requestBody) {
        try {
            List<MockApiDTO> mockApis = new ArrayList<>();
            if (EnvironmentUtil.isProduction()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            File file = new File(mockFilePath);
            if (!file.exists()) {
                throw new FileNotFoundException("Mock API file not found at path: " + mockFilePath);
            }
            mockApis = mapper.readValue(file, new TypeReference<List<MockApiDTO>>() {
            });
            for (MockApiDTO mockApi : mockApis) {
                if ((mockApi.getUrl().equals(url) || mockApi.getUrl().equals("**"))
                        && ObjectUtils.nullSafeEquals(mockApi.getPrefix(), prefix)
                        && mockApi.getMethod().equalsIgnoreCase(method)) {
                    if (mockApi.getRequestParams() != null
                            && !mockApi.getRequestParams().keySet()
                            .equals(requestParams.keySet())) {
                        continue;
                    }

                    if (mockApi.getRequestBody() != null
                            && !hasSameProperties(mockApi.getRequestBody(), requestBody)) {
                        continue;
                    }

                    // Check if pagination is enabled
                    if (mockApi.getPaging() != null
                            && mockApi.getPaging()
                            && "GET".equalsIgnoreCase(method)) {
                        int page = requestParams.containsKey("page") ? Integer.parseInt(requestParams.get("page")) : 1;
                        int pageSize = 20;
                        int startIndex = (page - 1) * pageSize;
                        int endIndex = startIndex + pageSize;
                        PageResultDTO pageResult = new PageResultDTO();
                        pageResult.setPage(page);
                        pageResult.setSize(pageSize);
                        List<Object> responseList = (List<Object>) mockApi.getResponseBody();
                        int totalElement = responseList.size();
                        pageResult.setTotalPage(totalElement % pageSize == 0 ? totalElement / pageSize : totalElement / pageSize + 1);
                        pageResult.setTotalElement(totalElement);
                        if (startIndex >= responseList.size()) {
                            // If the start index exceeds the list size, return an empty response
                            return new ResponseEntity<>(Collections.emptyList(), HttpStatus.OK);
                        }
                        // Ensure endIndex doesn't exceed the list size
                        endIndex = Math.min(endIndex, responseList.size());

                        List<Object> paginatedResponse = responseList.subList(startIndex, endIndex);
                        pageResult.setItems(paginatedResponse);
                        return new ResponseEntity<>(pageResult, HttpStatus.OK);
                    } else {
                        // Return full response if pagination is not enabled or request method is not GET
                        return new ResponseEntity<>(mockApi.getResponseBody(), HttpStatus.valueOf(mockApi.getResponseStatus()));
                    }
                }
            }

            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    private boolean hasSameProperties(Object obj1, Object obj2) {
        if (obj1 == null || obj2 == null) {
            return false;
        }

        // Get property names of obj1 and obj2
        ObjectMapper mapper = new ObjectMapper();
        JsonNode tree1 = mapper.valueToTree(obj1);
        JsonNode tree2 = mapper.valueToTree(obj2);

        // Check if the sets of property names are equal
        return tree1.equals(tree2);
    }
}

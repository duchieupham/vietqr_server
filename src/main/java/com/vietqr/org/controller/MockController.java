package com.vietqr.org.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vietqr.org.dto.MockApiDTO;
import com.vietqr.org.util.EnvironmentUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("mock/api")
public class MockController {

    private final ObjectMapper mapper = new ObjectMapper();

    private static final Logger logger = Logger.getLogger(MockController.class);

    @Value("${mock.file.api.path}")
    private String mockFilePath;

    @Autowired
    private Environment env;

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
    public ResponseEntity<Object> handleDelete(@PathVariable String url, @RequestParam Map<String, String> requestParams) {
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
                if (mockApi.getUrl().equals(url) && mockApi.getMethod().equalsIgnoreCase(method)) {
                    if (mockApi.getRequestParams() != null && !mockApi.getRequestParams().equals(requestParams)) {
                        continue;
                    }

                    if (mockApi.getRequestBody() != null && !mockApi.getRequestBody().equals(requestBody)) {
                        continue;
                    }

                    return new ResponseEntity<>(mockApi.getResponseBody(), HttpStatus.valueOf(mockApi.getResponseStatus()));
                }
            }

            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}

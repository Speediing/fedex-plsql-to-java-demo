package com.fedexdemo.rating.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class RatingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void scenariosEndpointLoadsDemoData() throws Exception {
        mockMvc.perform(get("/scenarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").exists());
    }

    @Test
    void ratingEndpointReturnsNotImplementedUntilMigrated() throws Exception {
        mockMvc.perform(post("/ratings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "trackingRef": "DEMO-001",
                                  "originZip": "38118",
                                  "destZip": "75201",
                                  "destType": "R",
                                  "destRegion": "US",
                                  "weightLbs": 25,
                                  "lengthIn": 12,
                                  "widthIn": 10,
                                  "heightIn": 8,
                                  "serviceCode": "GND",
                                  "accountId": "ACCT-1001",
                                  "saturdayDelivery": false,
                                  "dangerousGoods": false
                                }
                                """))
                .andExpect(status().isNotImplemented())
                .andExpect(jsonPath("$.status").value("NOT_IMPLEMENTED"));
    }

    @Test
    void ratingEndpointRejectsOverweightPriorityOvernightFromWeightLimitPolicy() throws Exception {
        mockMvc.perform(post("/ratings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "trackingRef": "FED-3",
                                  "originZip": "38118",
                                  "destZip": "75201",
                                  "destType": "C",
                                  "destRegion": "US",
                                  "weightLbs": 151,
                                  "lengthIn": 12,
                                  "widthIn": 10,
                                  "heightIn": 8,
                                  "serviceCode": "PO",
                                  "accountId": "ACCT-1001",
                                  "saturdayDelivery": false,
                                  "dangerousGoods": false
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("REJECTED"))
                .andExpect(jsonPath("$.errorCode").value(-2041))
                .andExpect(jsonPath("$.errorMessage").value("Weight exceeds air service limit"))
                .andExpect(jsonPath("$.engine").value("legacy-plsql-adapter"));
    }
}

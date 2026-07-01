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
    void ratingEndpointReturnsRatedResponse() throws Exception {
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
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("RATED"))
                .andExpect(jsonPath("$.totalAmount").value(26.26))
                .andExpect(jsonPath("$.zoneCode").value(4))
                .andExpect(jsonPath("$.errorCode").value(0));
    }

    @Test
    void ratingEndpointReturnsRejectedResponseForDangerousGoodsOnOvernight() throws Exception {
        mockMvc.perform(post("/ratings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "trackingRef": "DEMO-004",
                                  "originZip": "38118",
                                  "destZip": "75201",
                                  "destType": "C",
                                  "destRegion": "US",
                                  "weightLbs": 25,
                                  "lengthIn": 12,
                                  "widthIn": 10,
                                  "heightIn": 8,
                                  "serviceCode": "PO",
                                  "accountId": "ACCT-1001",
                                  "saturdayDelivery": false,
                                  "dangerousGoods": true
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("REJECTED"))
                .andExpect(jsonPath("$.errorCode").value(-2042))
                .andExpect(jsonPath("$.errorMessage")
                        .value("Dangerous goods not allowed on Priority Overnight"));
    }
}

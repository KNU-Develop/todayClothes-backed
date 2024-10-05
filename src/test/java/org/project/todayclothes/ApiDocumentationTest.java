package org.project.todayclothes;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
public class ApiDocumentationTest {
    @Autowired
    private MockMvc mockMvc;
    @Test
    public void shouldDocumentApi() throws Exception {
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/user"))
                .andExpect(status().isOk())
                .andDo(document("get-user"));
    }
    @Test
    public void shouldDocumentApiReview() throws Exception {
        mockMvc.perform(RestDocumentationRequestBuilders.get("/review"))
                .andExpect(status().isOk())
                .andDo(document("review"));
    }
    @Test
    public void shouldDocumentApiReview2() throws Exception {
        mockMvc.perform(RestDocumentationRequestBuilders.get("/review/{reviewId}"))
                .andExpect(status().isOk())
                .andDo(document("review"));
    }
}

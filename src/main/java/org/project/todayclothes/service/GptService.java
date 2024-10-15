package org.project.todayclothes.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.project.todayclothes.dto.gpt.ClotheRecommendPromptDto;
import org.project.todayclothes.dto.gpt.GptRequestBodyDto;
import org.project.todayclothes.dto.gpt.GptResponseClotheRecommendDto;
import org.project.todayclothes.exception.BusinessException;
import org.project.todayclothes.exception.code.GptErrorCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Service
@RequiredArgsConstructor
public class GptService {
    @Value("${gpt.api.key}")
    private String API_KEY;
    @Value("${gpt.api.url}")
    private String API_URL;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public GptResponseClotheRecommendDto getGetResponse(ClotheRecommendPromptDto clotheRecommendPromptDto) {
        try {
            // HTTP 요청 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + API_KEY);
            headers.set("Content-Type", "application/json");
            String prompt = createGptPrompt(clotheRecommendPromptDto);

            GptRequestBodyDto gptRequestBodyDto = new GptRequestBodyDto("gpt-4o-mini", prompt);
            String gptRequestBody = objectMapper.writeValueAsString(gptRequestBodyDto);
            HttpEntity<String> entity = new HttpEntity<>(gptRequestBody, headers);
            ResponseEntity<String> response = restTemplate.exchange(
                    API_URL, HttpMethod.POST, entity, String.class);
            String gptResBody = getGptResponseBody(response);
            Matcher matcher = parsingGptResponseBody(gptResBody);
            if (matcher.find()) {
                return convertStringToGptResponseClotheRecommendDto(matcher.group(1));
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String getGptResponseBody(ResponseEntity<String> response) {
        if (response == null) {
            throw new BusinessException(GptErrorCode.GPT_RESPONSE_IS_NULL);
        }
        try {
            JsonNode responseJson = objectMapper.readTree(response.getBody());
            return responseJson.get("choices").get(0).get("message").get("content").asText();
        } catch (JsonProcessingException e) {
            throw new BusinessException(GptErrorCode.GPT_INVALID_RESPONSE_FORMAT);
        }
    }
    private String createGptPrompt(ClotheRecommendPromptDto clotheRecommendPromptDto) {
        return clotheRecommendPromptDto.toString();
    }
    private Matcher parsingGptResponseBody(String gptResponseBody) {
        return (Pattern.compile("\\{([^}]*)\\}")).matcher(gptResponseBody);
    }
    private GptResponseClotheRecommendDto convertStringToGptResponseClotheRecommendDto(String content) throws JsonProcessingException {
        return objectMapper.readValue("{"+content+"}", GptResponseClotheRecommendDto.class);
    }
}
package org.project.todayclothes.controller;

import lombok.RequiredArgsConstructor;
import org.project.todayclothes.dto.gpt.ClotheRecommendPromptDto;
import org.project.todayclothes.dto.gpt.GptResponseClotheRecommendDto;
import org.project.todayclothes.exception.Api_Response;
import org.project.todayclothes.global.code.UtilSuccessCode;
import org.project.todayclothes.service.GptService;
import org.project.todayclothes.util.ApiResponseUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/gpt")
public class GptController {
    private final GptService gptService;

    @PostMapping
    public ResponseEntity<Api_Response<GptResponseClotheRecommendDto>> getGptResponse(@RequestBody ClotheRecommendPromptDto clotheRecommendPromptDto) {
        GptResponseClotheRecommendDto getResponse = gptService.getGetResponse(clotheRecommendPromptDto);
        if (getResponse == null) {
//            return ApiResponseUtil.createErrorResponse()
        }
        return ApiResponseUtil.createSuccessResponse(UtilSuccessCode.GPT_RESPONSE, getResponse);
    }
}

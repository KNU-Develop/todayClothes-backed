package org.project.todayclothes.controller;

import lombok.RequiredArgsConstructor;
import org.project.todayclothes.dto.gpt.ClotheRecommendPromptDto;
import org.project.todayclothes.dto.gpt.GptResClotheRecommendDto;
import org.project.todayclothes.exception.Api_Response;
import org.project.todayclothes.global.code.UtilSuccessCode;
import org.project.todayclothes.service.GptService;
import org.project.todayclothes.utils.ApiResponseUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/gpt")
public class GptController {
    private final GptService gptService;

    @PostMapping
    public ResponseEntity<Api_Response<GptResClotheRecommendDto>> getGptResponse(@RequestBody ClotheRecommendPromptDto clotheRecommendPromptDto) {
        GptResClotheRecommendDto getResponse = gptService.getGetResponse(clotheRecommendPromptDto);
        if (getResponse == null) {
//            return ApiResponseUtil.createErrorResponse()
        }
        return ApiResponseUtil.createSuccessResponse(UtilSuccessCode.GPT_RESPONSE, getResponse);
    }
}

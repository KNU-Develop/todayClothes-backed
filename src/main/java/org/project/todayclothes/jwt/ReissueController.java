package org.project.todayclothes.jwt;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.project.todayclothes.exception.Api_Response;
import org.project.todayclothes.exception.code.CommonErrorCode;
import org.project.todayclothes.utils.ApiResponseUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ReissueController {

    private final ReissueService reissueService;
    private final JWTUtil jwtUtil;

    @PostMapping("/reissue")
    public ResponseEntity<Api_Response<Void>> reissue(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = reissueService.getRefreshToken(request);
        if (refreshToken == null) {
            return ApiResponseUtil.createErrorResponse(CommonErrorCode.NO_REFRESH_TOKEN);
        }

        if (!reissueService.isRefreshToken(refreshToken)) {
            return ApiResponseUtil.createErrorResponse(CommonErrorCode.NOT_REFRESH_TOKEN_TYPE);
        }

        if (!reissueService.validateRefreshToken(refreshToken)) {
            return ApiResponseUtil.createErrorResponse(CommonErrorCode.REFRESH_TOKEN_EXPIRED);
        }



        String newAccessToken = reissueService.createAccessToken(refreshToken);
        String newRefreshToken = reissueService.createRefreshToken(refreshToken);

        response.setHeader("Authorization", "Bearer " + newAccessToken);
        response.addCookie(jwtUtil.createCookie(newRefreshToken));
        return new ResponseEntity<>(HttpStatus.OK);
    }
}

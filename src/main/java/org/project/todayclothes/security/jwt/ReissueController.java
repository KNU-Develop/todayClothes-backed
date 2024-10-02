package org.project.todayclothes.security.jwt;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ReissueController {
    private static final String REFRESH_TOKEN_NULL_MESSAGE = "Refresh token is null";
    private static final String REFRESH_TOKEN_EXPIRED_MESSAGE = "Refresh token is expired";
    private static final String INVALID_REFRESH_TOKEN_MESSAGE = "Invalid refresh token";

    private final ReissueService reissueService;
    private final JWTUtil jwtUtil;

    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = reissueService.getRefreshToken(request);
        if (refreshToken == null) {
            return new ResponseEntity<>(REFRESH_TOKEN_NULL_MESSAGE, HttpStatus.BAD_REQUEST);
        }
        if (!reissueService.validateRefreshToken(refreshToken)) {
            return new ResponseEntity<>(REFRESH_TOKEN_EXPIRED_MESSAGE, HttpStatus.BAD_REQUEST);
        }

        if (!reissueService.isRefreshToken(refreshToken)) {
            return new ResponseEntity<>(INVALID_REFRESH_TOKEN_MESSAGE, HttpStatus.BAD_REQUEST);
        }

        String newAccessToken = reissueService.createAccessToken(refreshToken);
        String newRefreshToken = reissueService.createRefreshToken(refreshToken);

        response.setHeader("access", newAccessToken);
        response.addCookie(jwtUtil.createHttpOnlySecureCookie(newRefreshToken));
        return new ResponseEntity<>(HttpStatus.OK);
    }
}

package dev.ujjwal.app_2_crud.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/csrf")
@Slf4j
@Tag(name = "CSRF")
public class CsrfController {

    @GetMapping
    @Operation(summary = "CSRF Token")
    public ResponseEntity<CsrfToken> csrf(HttpServletRequest request) {
        CsrfToken token = (CsrfToken) request.getAttribute("_csrf");
        log.trace("{} {} {}", token.getHeaderName(), token.getToken(), token.getParameterName());
        return ResponseEntity.ok(token);
    }

}

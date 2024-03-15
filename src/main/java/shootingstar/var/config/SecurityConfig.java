package shootingstar.var.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import shootingstar.var.handler.CustomAccessDeniedHandler;
import shootingstar.var.handler.CustomAuthenticationEntryPoint;
import shootingstar.var.oAuth.OAuth2UserService;
import shootingstar.var.handler.OAuth2SuccessHandler;
import shootingstar.var.jwt.JwtAuthenticationFilter;
import shootingstar.var.jwt.JwtTokenProvider;

import java.util.Collections;
import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final OAuth2UserService oAuth2UserService;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    private final JwtTokenProvider jwtTokenProvider;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .httpBasic(HttpBasicConfigurer::disable)
                .csrf(CsrfConfigurer::disable)
                .cors(corsCustomizer -> corsCustomizer.configurationSource(corsConfigurationSource()))
                .sessionManagement(configurer -> configurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize ->
                                authorize
                                        .requestMatchers( // 허용하지 않는 엔드포인트
                                                "/remote/fgt_lang",
                                                "/"
                                        ).denyAll()

                                        .requestMatchers( // 인증 후 접근 허용
                                                "/api/auth/delete/user"
                                        ).authenticated()

                                        .requestMatchers( // 인증 없이 접근 허용
                                                "/login",
                                                "/error",
                                                "/api/auth/**"
                                        ).permitAll()

                                        .requestMatchers( // 권환 확인
                                                "/api/user/test"
                                        ).hasRole("BASIC")

                                        .anyRequest().authenticated() // 정의한 엔드 포인트를 제외한 모든 요청은 인증이 필요함
                )
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(authenticationManager -> authenticationManager
                        .authenticationEntryPoint(new CustomAuthenticationEntryPoint())
                        .accessDeniedHandler(new CustomAccessDeniedHandler()))
                .oauth2Login(oauth2Configurer -> oauth2Configurer
                        .loginPage("/login")
                        .successHandler(oAuth2SuccessHandler.successHandler())
                        .userInfoEndpoint(userInfoEndpoint -> userInfoEndpoint
                                .userService(oAuth2UserService)));

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:3000")); // 여기에 허용할 오리진 추가
        configuration.setAllowedMethods(Collections.singletonList("*")); // 허용할 HTTP 메소드 설정

        configuration.setAllowCredentials(true); // 쿠키를 넘기기 위해 사용
//        configuration.setMaxAge(3600L); // 브라우저 캐싱 시간(초)

        configuration.setAllowedHeaders(Collections.singletonList("*")); // 허용할 헤더 설정
        configuration.addExposedHeader("Authorization"); // 클라이언트가 접근할 수 있도록 헤더 노출

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // 모든 경로에 대해 적용
        return source;
    }
}

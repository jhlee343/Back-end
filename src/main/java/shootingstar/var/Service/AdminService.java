package shootingstar.var.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import shootingstar.var.entity.Admin;
import shootingstar.var.jwt.JwtTokenProvider;
import shootingstar.var.jwt.TokenInfo;
import shootingstar.var.repository.admin.AdminRepository;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider tokenProvider;

    @Value("${admin-secret-signup-key}")
    private String adminSignupSecretKey;

    public void signup(String id, String password, String secretKey) {
        if (!secretKey.equals(adminSignupSecretKey)) {
            throw new RuntimeException("회원가입 실패 잘못된 인증 키");
        }
        if (adminRepository.existsByAdminLoginId(id)) {
            throw new RuntimeException("해당 id로 가입 불가능");
        }
        String encodePassword = passwordEncoder.encode(password); // 패스워드 암호화
        Admin admin = new Admin(id, encodePassword);
        adminRepository.save(admin);
    }

    public TokenInfo login(String id, String password) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(id, password);
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        return tokenProvider.generateToken(authentication);
    }
}

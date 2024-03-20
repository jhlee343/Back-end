package shootingstar.var.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import shootingstar.var.exception.CustomException;
import shootingstar.var.exception.ErrorCode;
import shootingstar.var.util.MailRedisUtil;

import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;
    private final MailRedisUtil mailRedisUtil;
    private final CheckDuplicateService duplicateService;

    @Value("${fromMail}")
    private String fromEmail;

    public String setContext(Context context, String name, String value, String template) {
        context.setVariable(name, value);
        return templateEngine.process(template, context);
    }

    public void sendAuthCodeEmail(String email) {
        if (duplicateService.checkEmailDuplicate(email)) {
            throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
        }

        String code = createCode();

        String title = "VIP and Rendezvous 이메일 인증 코드"; // 제목
        MimeMessage message = mailSender.createMimeMessage();
        Context context = new Context();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(email);
            helper.setSubject(title);
            helper.setFrom(fromEmail);
            helper.setText(setContext(context, "code", code, "AuthCodeMailTemplate"), true);

            // 메시지 전송
            mailSender.send(message);
            mailRedisUtil.setDataExpire(email, code, 5 * 60 * 1000);
        } catch (MessagingException e) {
            log.info(e.getMessage());
            throw new RuntimeException("메일 전송 실패");
        }
    }

    public void validateCode(String key, String value) {
        if (mailRedisUtil.hasKey(key) && mailRedisUtil.getData(key).equals(value)) {
            mailRedisUtil.setDataExpire(key, "validate", 15 * 60 * 1000);
        } else {
            throw new CustomException(ErrorCode.AUTH_ERROR_EMAIL);
        }
    }


    public String createCode() {
        Random random = new Random();
        StringBuilder key = new StringBuilder();

        for (int i = 0; i <8; i++) {
            int index = random.nextInt(3); // 0 ~ 2 랜덤 index -> case 문

            switch (index) {
                case 0 -> key.append((char) (random.nextInt(26) + 97)); // 대문자
                case 1 -> key.append((char) (random.nextInt(26) + 65)); // 소문자
                case 2 -> key.append(random.nextInt(9)); // 숫자
            }
        }
        return key.toString();
    }
}

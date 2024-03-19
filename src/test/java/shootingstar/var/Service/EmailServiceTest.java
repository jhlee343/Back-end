package shootingstar.var.Service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import shootingstar.var.util.MailRedisUtil;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class EmailServiceTest {

    @Autowired
    private EmailService emailService;
    @Autowired
    private MailRedisUtil mailRedisUtil;

    @Test
    @DisplayName("인증 코드 이메일 전송 테스트")
    public void sendAuthCodeEmail() throws Exception {
        //given
        emailService.sendAuthCodeEmail("skadu66@gmail.com");

        //when
        String data = mailRedisUtil.getData("skadu66@gmail.com");

        //then
        Assertions.assertThat(data).isNotNull();
    }
}
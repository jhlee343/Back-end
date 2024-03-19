package shootingstar.var.oAuth;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class KakaoServiceTest {

    @Autowired
    KakaoAPI kakaoService;

    @Test
    public void accessToken() throws Exception {
        //given
        String code = "HbKeIbj7yJiB46jHHNizhScgpDfy_i3CH8tV2c_1bqQpdBxf35jNOMD1PwgKPXOaAAABjlZN_ayBPKUF0hG4dQ&state=1C2dNmzUofs9a_lJ3S1vpnWKKV40sp3qOZ6HPf3oHK8%3D";

        String accessTokenFromKakao = kakaoService.getAccessTokenFromKakao(code);
        //when

        //then
        System.out.println(accessTokenFromKakao);
    }
}
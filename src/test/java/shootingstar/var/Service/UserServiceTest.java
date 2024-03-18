package shootingstar.var.Service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import shootingstar.var.entity.Follow;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Test
    @Transactional
    public void follow() throws Exception{
        //
    }
    @Test
    public void saveUser() throws Exception {
        //given

        //when

        //then

    }
}
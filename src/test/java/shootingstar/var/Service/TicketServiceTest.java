package shootingstar.var.Service;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class TicketServiceTest {

    @Autowired
    TicketService ticketService;

    @Test
    void calculateCommission() {
        // given
        LocalDate meetingDate = LocalDate.of(2024, 5, 1);
        LocalDateTime meetingDateTime = LocalDateTime.of(2024, 5, 1, 18, 1);

        LocalDate meetingDate2 = LocalDate.of(2024, 5, 2);
        LocalDateTime meetingDateTime2 = LocalDateTime.of(2024, 5, 2, 18, 1);

        LocalDate meetingDate3 = LocalDate.of(2024, 4, 20);
        LocalDateTime meetingDateTime3 = LocalDateTime.of(2024, 4, 20, 18, 1);

        LocalDate meetingDate4 = LocalDate.of(2024, 4, 2);
        LocalDateTime meetingDateTime4 = LocalDateTime.of(2024, 4, 2, 18, 1);

        // when
        BigDecimal bigDecimal = ticketService.calculateCommission(meetingDate, meetingDateTime);
        BigDecimal bigDecimal2 = ticketService.calculateCommission(meetingDate2, meetingDateTime2);
        BigDecimal bigDecimal3 = ticketService.calculateCommission(meetingDate3, meetingDateTime3);
        BigDecimal bigDecimal4 = ticketService.calculateCommission(meetingDate4, meetingDateTime4);

        // then
        assertThat(bigDecimal).isEqualTo(new BigDecimal(0));
        assertThat(bigDecimal2).isEqualTo(new BigDecimal(0));
        assertThat(bigDecimal3).isEqualTo(new BigDecimal(0.1));
        assertThat(bigDecimal4).isEqualTo(new BigDecimal(0.7));
    }

}
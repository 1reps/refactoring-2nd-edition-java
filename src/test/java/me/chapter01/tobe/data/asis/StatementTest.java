package me.chapter01.tobe.data.asis;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import me.chapter01.tobe.data.asis.data.Invoice;
import me.chapter01.tobe.data.asis.data.Play;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class StatementTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private Statement statement;
    private ClassLoader classLoader;
    private InputStream jsonInvoices;
    private InputStream jsonPlays;
    private List<Invoice> invoices;
    private Map<String, Play> plays;

    @BeforeEach
    void setUp() {
        classLoader = getClass().getClassLoader();
        try (
            InputStream invoiceStream = Optional.ofNullable(classLoader.getResourceAsStream("chapter01/invoices.json"))
                .orElseThrow(() -> new IllegalArgumentException("Failed to load resources from invoices.json"));
            InputStream playStream = Optional.ofNullable(classLoader.getResourceAsStream("chapter01/plays.json"))
                .orElseThrow(() -> new IllegalArgumentException("Failed to load resources from plays.json"));
        ) {
            jsonInvoices = invoiceStream;
            jsonPlays = playStream;

            invoices = objectMapper.readValue(jsonInvoices, new TypeReference<>() {
            });
            plays = objectMapper.readValue(jsonPlays, new TypeReference<>() {
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @DisplayName("statement 정상 출력 확인")
    @Test
    void statement_success() {
        // given
        String result = statement.statement(invoices.getFirst(), plays);

        String expected = """
            청구 내역 (고객명: BigCo)
              Hamlet: $650.00원 (55석)
              As You Like It: $580.00원 (35석)
              Othello: $500.00원 (40석)
            총액: $1,730.00원
            적립 포인트: 47점
            """;

        // when // then
        assertThat(result).isEqualTo(expected);
    }

}
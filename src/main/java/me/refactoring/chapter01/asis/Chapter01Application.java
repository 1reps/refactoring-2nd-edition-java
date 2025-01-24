package me.refactoring.chapter01.asis;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import me.refactoring.chapter01.asis.data.Invoice;
import me.refactoring.chapter01.asis.data.Performance;
import me.refactoring.chapter01.asis.data.Play;

public class Chapter01Application {

    public static void main(String[] args) throws IOException {
        // json 파일을 읽어온다
        ClassLoader classLoader = Chapter01Application.class.getClassLoader();
        InputStream jsonInvoices = Optional.ofNullable(classLoader.getResourceAsStream("chapter01/invoices.json"))
            .orElseThrow(() -> new IllegalArgumentException("Failed to load resources from invoices.json"));

        InputStream jsonPlays = Optional.ofNullable(classLoader.getResourceAsStream("chapter01/plays.json"))
            .orElseThrow(() -> new IllegalArgumentException("Failed to load resources from plays.json"));

        ObjectMapper objectMapper = new ObjectMapper();
        List<Invoice> invoices = objectMapper.readValue(jsonInvoices, new TypeReference<>() {
        });
        Map<String, Play> plays = objectMapper.readValue(jsonPlays, new TypeReference<>() {
        });

        List<String> results = invoices.stream()
            .map(invoice -> statement(invoice, plays))
            .toList();

        System.out.println("results: " + results);
    }

    static String statement(Invoice invoice, Map<String, Play> plays) {
        int totalAmount = 0;
        int volumeCredits = 0;
        StringBuilder result = new StringBuilder();
        result.append(String.format("청구 내역 (고객명: %s)\n", invoice.customer()));

        NumberFormat format = NumberFormat.getCurrencyInstance(Locale.US);

        for (Performance perf : invoice.performances()) {
            Play play = plays.get(perf.playID());
            int thisAmount = 0;

            switch (play.type()) {
                case "tragedy": // 비극
                    thisAmount = 40000;
                    if (perf.audience() > 30) {
                        thisAmount += 1000 * (perf.audience() - 30);
                    }
                    break;
                case "comedy": // 희극
                    thisAmount = 30000;
                    if (perf.audience() > 20) {
                        thisAmount += 10000 + 500 * (perf.audience() - 20);
                    }
                    thisAmount += 300 * perf.audience();
                    break;
                default:
                    throw new IllegalArgumentException("알 수 없는 장르: " + play.type());
            }

            // 포인트를 적립한다
            volumeCredits += Math.max(perf.audience() - 30, 0);

            // 희극 관객 5명마다 추가 포인트를 제공한다
            if ("comedy".equals(play.type())) {
                volumeCredits += Math.floor(perf.audience() / 5);
            }

            // 청구 내역을 출력한다
            result.append(
                String.format("    %s: %s (%d)석%n",
                    play.name(),
                    format.format(thisAmount / 100),
                    perf.audience())
            );
            totalAmount += thisAmount;
        }

        result.append(String.format("총액: %s\n", format.format(totalAmount / 100)));
        result.append(String.format("적립 포인트: %d\n", volumeCredits));
        return result.toString();
    }

}

package me.chapter01.tobe.data.asis;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Map;
import me.chapter01.tobe.data.asis.data.Invoice;
import me.chapter01.tobe.data.asis.data.Performance;
import me.chapter01.tobe.data.asis.data.Play;

public class Statement {

    public Statement() {
    }

    public static String statement(Invoice invoice, Map<String, Play> plays) {
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
                String.format("  %s: %s원 (%d석)\n",
                    play.name(),
                    format.format(thisAmount / 100),
                    perf.audience())
            );
            totalAmount += thisAmount;
        }

        result.append(String.format("총액: %s원\n", format.format(totalAmount / 100)));
        result.append(String.format("적립 포인트: %d점\n", volumeCredits));
        return result.toString();
    }

}

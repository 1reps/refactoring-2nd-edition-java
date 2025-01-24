package me.refactoring.chapter01.tobe;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import me.refactoring.chapter01.tobe.data.Invoice;
import me.refactoring.chapter01.tobe.data.Performance;
import me.refactoring.chapter01.tobe.data.Play;

public class Statement {

    public static String statement(Invoice invoice, Map<String, Play> plays) {
        StatementData data = StatementData.of(invoice.customer(), invoice.performances());

        // p.55 EnrichPerformance() 작업중

        return renderPlainText(data, plays);
    }

    private static String renderPlainText(StatementData data, Map<String, Play> plays) {
        StringBuilder result = new StringBuilder();
        result.append(String.format("청구 내역 (고객명: %s)\n", data.customer()));

        for (Performance perf : data.performances()) {
            // 청구 내역을 출력한다
            result.append(
                String.format("  %s: %s원 (%d석)\n",
                    playFor(plays, perf).name(),
                    usd(amountFor(perf, playFor(plays, perf))),
                    perf.audience())
            );
        }
        result.append(String.format("총액: %s원\n", usd(totalAmount(data, plays))));
        result.append(String.format("적립 포인트: %d점\n", totalVolumeCredits(data, plays)));
        return result.toString();
    }

    private static int totalAmount(StatementData data, Map<String, Play> plays) {
        return data.performances().stream()
            .mapToInt(perf -> amountFor(perf, playFor(plays, perf)))
            .sum();
    }

    private static int totalVolumeCredits(StatementData data, Map<String, Play> plays) {
        int volumeCredits = 0;
        return data.performances().stream()
            .mapToInt(perf -> volumeCreditsFor(plays, perf, volumeCredits))
            .sum();
    }

    private static String usd(int aNumber) {
        return NumberFormat.getCurrencyInstance(Locale.US).format(aNumber / 100);
    }

    private static int volumeCreditsFor(Map<String, Play> plays, Performance perf, int volumeCredits) {
        // 포인트를 적립한다
        volumeCredits += Math.max(perf.audience() - 30, 0);

        // 희극 관객 5명마다 추가 포인트를 제공한다
        if ("comedy".equals(playFor(plays, perf).type())) {
            volumeCredits += Math.floor(perf.audience() / 5);
        }
        return volumeCredits;
    }

    private static Play playFor(Map<String, Play> plays, Performance perf) {
        return plays.get(perf.playID());
    }

    private static int amountFor(Performance aPerformance, Play play) {
        int result = 0;
        switch (play.type()) {
            case "tragedy": // 비극
                result = 40000;
                if (aPerformance.audience() > 30) {
                    result += 1000 * (aPerformance.audience() - 30);
                }
                break;
            case "comedy": // 희극
                result = 30000;
                if (aPerformance.audience() > 20) {
                    result += 10000 + 500 * (aPerformance.audience() - 20);
                }
                result += 300 * aPerformance.audience();
                break;
            default:
                throw new IllegalArgumentException("알 수 없는 장르: " + play.type());
        }
        return result;
    }
}
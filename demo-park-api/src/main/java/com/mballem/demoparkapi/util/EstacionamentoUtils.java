package com.mballem.demoparkapi.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EstacionamentoUtils {

    private static final double PRIMEIROS_15_MINUTES = 5.00;
    private static final double PRIMEIROS_60_MINUTES = 9.25;
    private static final double ADICIONAL_15_MINUTES = 1.75;
    private static final double DESCONTO_PERCENTUAL = 0.30;

    // 2024-03-16T15:23:48
    public static String gerarRecibo() {
        LocalDateTime date = LocalDateTime.now();
        String recibo = date.toString().substring(0,19);
        return recibo
                .replace("-", "")
                .replace(":", "")
                .replace("T", "-");
    }

    public static BigDecimal calcularCusto(LocalDateTime entrada, LocalDateTime saida) {
        long minutes = entrada.until(saida, ChronoUnit.MINUTES);
        double total = 0.0;

        if (minutes <= 15) {

            // complete com a lógica para calcular o custo até 15 minutos de uso
            total = PRIMEIROS_15_MINUTES;

        } else if (minutes <= 60) {

            // complete com a lógica para calcular o custo até os primeiros 60 minutos de uso
            total =  PRIMEIROS_60_MINUTES;

        } else {
            // complete com a lógica para calcular o custo acima de 60 minutos de uso
            long minutosRestantes = minutes - 60;
            Double calculoMinutosRestantes = ((double)minutosRestantes / 15);

            if(calculoMinutosRestantes > calculoMinutosRestantes.intValue()) {
                total += PRIMEIROS_60_MINUTES + (ADICIONAL_15_MINUTES * (calculoMinutosRestantes.intValue() + 1));
            } else {
                total += PRIMEIROS_60_MINUTES + (ADICIONAL_15_MINUTES * calculoMinutosRestantes.intValue());
            }
        }

        return new BigDecimal(total).setScale(2, RoundingMode.HALF_EVEN);
    }

    public static BigDecimal calcularDesconto(BigDecimal custo, long numeroDeVezes) {

        // Complete o código com a sua lógica

        BigDecimal desconto = ((numeroDeVezes > 0) && (numeroDeVezes % 10 == 0))
                ? custo.multiply(new BigDecimal(DESCONTO_PERCENTUAL))
                : new BigDecimal(0);

        return desconto.setScale(2, RoundingMode.HALF_EVEN);
    }
}

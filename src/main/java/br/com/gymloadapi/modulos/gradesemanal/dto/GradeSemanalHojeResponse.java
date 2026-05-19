package br.com.gymloadapi.modulos.gradesemanal.dto;

public record GradeSemanalHojeResponse(
    Integer treinoId,
    String treinoNome,
    boolean concluidoHoje
) {
}

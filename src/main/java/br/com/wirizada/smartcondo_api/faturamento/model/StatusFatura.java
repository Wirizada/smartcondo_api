package br.com.wirizada.smartcondo_api.faturamento.model;

/**
 * Máquina de estados da Fatura conforme especificação §2.2.
 */
public enum StatusFatura {
    RASCUNHO,
    ABERTA,
    VENCIDA,
    PAGA,
    CANCELADA
}


package br.com.wirizada.smartcondo_api.faturamento.infrastructure;

import br.com.wirizada.smartcondo_api.faturamento.application.GerarFaturasMensaisUseCase;
import br.com.wirizada.smartcondo_api.faturamento.infrastructure.dto.GerarFaturasRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/faturas")
public class FaturaController {

    private final GerarFaturasMensaisUseCase gerarFaturasMensaisUseCase;

    public FaturaController(GerarFaturasMensaisUseCase gerarFaturasMensaisUseCase) {
        this.gerarFaturasMensaisUseCase = gerarFaturasMensaisUseCase;
    }

    @PostMapping
    public ResponseEntity<String> geraFaturas(@RequestBody GerarFaturasRequest request) {
        gerarFaturasMensaisUseCase.executar(request.valorBase(), request.dataVencimento());
        return ResponseEntity.status(HttpStatus.CREATED).body("Faturas geradas com sucesso para o mês.");
    }
}

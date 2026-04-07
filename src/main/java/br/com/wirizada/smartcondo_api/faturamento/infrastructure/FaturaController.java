package br.com.wirizada.smartcondo_api.faturamento.infrastructure;

import br.com.wirizada.smartcondo_api.faturamento.application.GerarFaturaMensalUseCase;
import br.com.wirizada.smartcondo_api.faturamento.application.ListarFaturaUseCase;
import br.com.wirizada.smartcondo_api.faturamento.infrastructure.dto.GerarFaturasRequest;
import br.com.wirizada.smartcondo_api.faturamento.infrastructure.dto.FaturaProjection;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/faturas")
public class FaturaController {

    private final GerarFaturaMensalUseCase gerarFaturasMensaisUseCase;
    private final ListarFaturaUseCase listarFaturasUseCase;

    public FaturaController(GerarFaturaMensalUseCase gerarFaturasMensaisUseCase
            , ListarFaturaUseCase listarFaturasUseCase) {
        this.gerarFaturasMensaisUseCase = gerarFaturasMensaisUseCase;
        this.listarFaturasUseCase = listarFaturasUseCase;
    }

    @PostMapping
    public ResponseEntity<String> gerarFaturas(@RequestBody @Valid GerarFaturasRequest request) {
        gerarFaturasMensaisUseCase.executar(request.valorBase(), request.dataVencimento());
        return ResponseEntity.status(HttpStatus.CREATED).body("Faturas geradas com sucesso para o mês.");
    }

    @GetMapping
    public ResponseEntity<List<FaturaProjection>> listarFaturas() {
        List<FaturaProjection> faturas = listarFaturasUseCase.executar();
        return ResponseEntity.status(HttpStatus.OK).body(faturas);
    }
}

package br.com.wirizada.smartcondo_api.faturamento.infrastructure;

import br.com.wirizada.smartcondo_api.faturamento.application.GerarFaturasMensaisUseCase;
import br.com.wirizada.smartcondo_api.faturamento.application.ListarFaturasUseCase;
import br.com.wirizada.smartcondo_api.faturamento.infrastructure.dto.GerarFaturasRequest;
import br.com.wirizada.smartcondo_api.faturamento.infrastructure.dto.ListarFaturaProjection;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/faturas")
public class FaturaController {

    private final GerarFaturasMensaisUseCase gerarFaturasMensaisUseCase;
    private final ListarFaturasUseCase listarFaturasUseCase;

    public FaturaController(GerarFaturasMensaisUseCase gerarFaturasMensaisUseCase
            , ListarFaturasUseCase listarFaturasUseCase) {
        this.gerarFaturasMensaisUseCase = gerarFaturasMensaisUseCase;
        this.listarFaturasUseCase = listarFaturasUseCase;
    }

    @PostMapping
    public ResponseEntity<String> gerarFaturas(@RequestBody @Valid GerarFaturasRequest request) {
        gerarFaturasMensaisUseCase.gerarFaturas(request.valorBase(), request.dataVencimento());
        return ResponseEntity.status(HttpStatus.CREATED).body("Faturas geradas com sucesso para o mês.");
    }

    @GetMapping
    public ResponseEntity<List<ListarFaturaProjection>> listarFaturas() {
        List<ListarFaturaProjection> faturas = listarFaturasUseCase.listarFaturas();
        return ResponseEntity.status(HttpStatus.OK).body(faturas);
    }
}

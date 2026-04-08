package br.com.wirizada.smartcondo_api.faturamento.api;

import br.com.wirizada.smartcondo_api.faturamento.api.dto.GerarFaturasRequest;
import br.com.wirizada.smartcondo_api.faturamento.application.dto.FaturaResumoDTO;
import br.com.wirizada.smartcondo_api.faturamento.application.GerarFaturaService;
import br.com.wirizada.smartcondo_api.faturamento.application.ListarFaturaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/faturas")
public class FaturaController {

    private final GerarFaturaService gerarFaturaService;
    private final ListarFaturaService listarFaturaService;

    public FaturaController(GerarFaturaService gerarFaturaService,
                            ListarFaturaService listarFaturaService) {
        this.gerarFaturaService = gerarFaturaService;
        this.listarFaturaService = listarFaturaService;
    }

    @PostMapping
    public ResponseEntity<String> gerarFaturas(@RequestBody @Valid GerarFaturasRequest request) {
        gerarFaturaService.executar(request.valorBase(), request.dataVencimento(), request.competenciaId());
        return ResponseEntity.status(HttpStatus.CREATED).body("Faturas geradas com sucesso para a competência.");
    }

    @GetMapping
    public ResponseEntity<List<FaturaResumoDTO>> listarFaturas() {
        List<FaturaResumoDTO> faturas = listarFaturaService.executar();
        return ResponseEntity.ok(faturas);
    }
}


package br.com.wirizada.smartcondo_api.condominio.api;

import br.com.wirizada.smartcondo_api.condominio.api.dto.CadastrarCondominioRequest;
import br.com.wirizada.smartcondo_api.condominio.api.dto.CondominioResponse;
import br.com.wirizada.smartcondo_api.condominio.application.CondominioService;
import br.com.wirizada.smartcondo_api.condominio.model.Condominio;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/condominios")
public class CondominioController {

    private final CondominioService condominioService;

    public CondominioController(CondominioService condominioService) {
        this.condominioService = condominioService;
    }

    @PostMapping
    public ResponseEntity<CondominioResponse> cadastrar(@RequestBody @Valid CadastrarCondominioRequest request) {
        Condominio condominio = condominioService.cadastrar(request.nome(), request.cnpj());

        CondominioResponse response = new CondominioResponse(
                condominio.getId(),
                condominio.getNome(),
                condominio.getCnpj()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}


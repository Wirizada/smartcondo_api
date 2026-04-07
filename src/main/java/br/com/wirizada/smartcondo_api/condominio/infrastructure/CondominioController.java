package br.com.wirizada.smartcondo_api.condominio.infrastructure;

import br.com.wirizada.smartcondo_api.condominio.application.CadastrarCondominioUseCase;
import br.com.wirizada.smartcondo_api.condominio.domain.Condominio;
import br.com.wirizada.smartcondo_api.condominio.infrastructure.dto.CadastrarCondominioRequest;
import br.com.wirizada.smartcondo_api.condominio.infrastructure.dto.CondominioResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/condominios")
public class CondominioController {

    private final CadastrarCondominioUseCase cadastrarCondominioUseCase;

    public CondominioController(CadastrarCondominioUseCase cadastrarCondominioUseCase) {
        this.cadastrarCondominioUseCase = cadastrarCondominioUseCase;
    }

    @PostMapping
    public ResponseEntity<CondominioResponse> cadastrar(@RequestBody @Valid CadastrarCondominioRequest request) {
        Condominio condominio = cadastrarCondominioUseCase.executar(request.nome(), request.cnpj());

        CondominioResponse response = new CondominioResponse(
                condominio.getId(),
                condominio.getNome(),
                condominio.getCnpj()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}

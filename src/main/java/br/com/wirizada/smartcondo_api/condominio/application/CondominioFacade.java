package br.com.wirizada.smartcondo_api.condominio.application;

import br.com.wirizada.smartcondo_api.condominio.application.dto.UnidadeParaFaturamentoDTO;
import br.com.wirizada.smartcondo_api.condominio.infra.MoradorRepository;
import br.com.wirizada.smartcondo_api.condominio.infra.UnidadeRepository;
import br.com.wirizada.smartcondo_api.condominio.model.Morador;
import br.com.wirizada.smartcondo_api.condominio.model.Unidade;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CondominioFacade {

    private final UnidadeRepository unidadeRepository;
    private final MoradorRepository moradorRepository;

    public CondominioFacade(UnidadeRepository unidadeRepository, MoradorRepository moradorRepository) {
        this.unidadeRepository = unidadeRepository;
        this.moradorRepository = moradorRepository;
    }

    public List<UnidadeParaFaturamentoDTO> listarUnidadesParaFaturamento() {
        List<Unidade> unidades = unidadeRepository.findByAtivaTrue();

        List<UUID> moradorIds = unidades.stream()
                .map(Unidade::getMoradorId)
                .distinct()
                .toList();

        Map<UUID, String> nomesMoradores = moradorRepository.findAllById(moradorIds)
                .stream()
                .collect(Collectors.toMap(Morador::getId, Morador::getNome));

        return unidades.stream()
                .map(u -> new UnidadeParaFaturamentoDTO(
                        u.getId(),
                        nomesMoradores.getOrDefault(u.getMoradorId(), "Morador não encontrado"),
                        u.getFracaoIdeal()
                ))
                .toList();
    }

    public Map<UUID, String> buscarNumerosUnidades(List<UUID> unidadeIds) {
        return unidadeRepository.findAllById(unidadeIds)
                .stream()
                .collect(Collectors.toMap(Unidade::getId, Unidade::getNumero));
    }
}

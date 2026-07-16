package conne.connect.connect.Equipe.service;

import conne.connect.connect.Security.AutorizacaoService;
import conne.connect.connect.Equipe.model.EquipeMembroModel;
import conne.connect.connect.Equipe.repository.EquipeMembroRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class EquipeMembroService {

    private final EquipeMembroRepository equipeMembroRepository;
    private final AutorizacaoService autorizacaoService;

    public EquipeMembroService(EquipeMembroRepository equipeMembroRepository, AutorizacaoService autorizacaoService) {
        this.equipeMembroRepository = equipeMembroRepository;
        this.autorizacaoService = autorizacaoService;
    }

    @Transactional(readOnly = true)
    public List<EquipeMembroModel> findAll() {
        return equipeMembroRepository.findByIdEmpresa_IdEmpresaAndExcluidoIsNull(autorizacaoService.empresaAtual());
    }

    @Transactional
    public EquipeMembroModel criarEquipeMembro(EquipeMembroModel equipeMembroModel) {
        validarEscopo(equipeMembroModel);
        return equipeMembroRepository.save(equipeMembroModel);
    }

    @Transactional(readOnly = true)
    public Optional<EquipeMembroModel> buscarPorId(Long idEquipeMembro) {
        return equipeMembroRepository.findByIdEquipeMembroAndIdEmpresa_IdEmpresaAndExcluidoIsNull(idEquipeMembro, autorizacaoService.empresaAtual());
    }

    @Transactional
    public EquipeMembroModel atualizarEquipeMembro(Long idEquipeMembro, EquipeMembroModel equipeMembroModel) {
        validarEscopo(equipeMembroModel);
        EquipeMembroModel equipeMembro = buscarEquipeMembroExistente(idEquipeMembro);
        equipeMembro.setIdEquipe(equipeMembroModel.getIdEquipe());
        equipeMembro.setIdUsuarioEmpresa(equipeMembroModel.getIdUsuarioEmpresa());
        return equipeMembroRepository.save(equipeMembro);
    }

    @Transactional
    public void excluirEquipeMembro(Long idEquipeMembro) {
        EquipeMembroModel equipeMembro = buscarEquipeMembroExistente(idEquipeMembro);
        equipeMembro.setExcluido(LocalDate.now());
        equipeMembroRepository.save(equipeMembro);
    }

    private void validarEscopo(EquipeMembroModel registro) {
        autorizacaoService.validarEmpresaAtual(registro.getIdEmpresa() != null ? registro.getIdEmpresa().getIdEmpresa() : null);
        autorizacaoService.validarEmpresaAtual(registro.getIdEquipe() != null && registro.getIdEquipe().getIdEmpresa() != null
                ? registro.getIdEquipe().getIdEmpresa().getIdEmpresa() : null);
        autorizacaoService.validarEmpresaAtual(registro.getIdUsuarioEmpresa() != null
                && registro.getIdUsuarioEmpresa().getIdEmpresa() != null
                ? registro.getIdUsuarioEmpresa().getIdEmpresa().getIdEmpresa() : null);
    }

    private EquipeMembroModel buscarEquipeMembroExistente(Long idEquipeMembro) {
        return buscarPorId(idEquipeMembro)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Membro de equipe não encontrado."));
    }
}

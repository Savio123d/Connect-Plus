package conne.connect.connect.Setor.service;

import conne.connect.connect.Security.AutorizacaoService;
import conne.connect.connect.Setor.model.SetorModel;
import conne.connect.connect.Setor.repository.SetorRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class SetorService {

    private final SetorRepository setorRepository;
    private final AutorizacaoService autorizacaoService;

    public SetorService(SetorRepository setorRepository, AutorizacaoService autorizacaoService) {
        this.setorRepository = setorRepository;
        this.autorizacaoService = autorizacaoService;
    }

    @Transactional(readOnly = true)
    public List<SetorModel> findAll() {
        return setorRepository.findByIdEmpresa_IdEmpresaAndExcluidoIsNull(autorizacaoService.empresaAtual());
    }

    @Transactional
    public SetorModel criarSetor(SetorModel setorModel) {
        validarEscopo(setorModel);
        return setorRepository.save(setorModel);
    }

    @Transactional(readOnly = true)
    public Optional<SetorModel> buscarPorId(Long idSetor) {
        return setorRepository.findByIdSetorAndIdEmpresa_IdEmpresaAndExcluidoIsNull(idSetor, autorizacaoService.empresaAtual());
    }

    @Transactional
    public SetorModel atualizarSetor(Long idSetor, SetorModel setorModel) {
        validarEscopo(setorModel);
        SetorModel setor = buscarSetorExistente(idSetor);
        setor.setNome(setorModel.getNome());
        setor.setDescricao(setorModel.getDescricao());
        setor.setAtivo(setorModel.getAtivo());
        return setorRepository.save(setor);
    }

    @Transactional
    public void excluirSetor(Long idSetor) {
        SetorModel setor = buscarSetorExistente(idSetor);
        setor.setExcluido(LocalDate.now());
        setorRepository.save(setor);
    }

    private void validarEscopo(SetorModel registro) {
        autorizacaoService.validarEmpresaAtual(
                registro.getIdEmpresa() != null ? registro.getIdEmpresa().getIdEmpresa() : null
        );
    }

    private SetorModel buscarSetorExistente(Long idSetor) {
        return buscarPorId(idSetor)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Setor não encontrado."));
    }
}

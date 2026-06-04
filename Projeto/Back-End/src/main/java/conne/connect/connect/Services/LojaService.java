package conne.connect.connect.Services;

import conne.connect.connect.Models.LojaModel;
import conne.connect.connect.Repositories.LojaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class LojaService {

    @Autowired
    private LojaRepository lojaRepository;

    public List<LojaModel> findAll() {
        return lojaRepository.findByExcluidoIsNull()
                .stream()
                .map(this::prepararParaFront)
                .toList();
    }

    public List<LojaModel> listarAtivas() {
        return lojaRepository.findByAtivaTrueAndExcluidoIsNull()
                .stream()
                .map(this::prepararParaFront)
                .toList();
    }

    public List<LojaModel> listarPorEmpresa(Long idEmpresa, Boolean somenteAtivas) {
        List<LojaModel> lojas;

        if (Boolean.TRUE.equals(somenteAtivas)) {
            lojas = lojaRepository.findByIdEmpresaAndAtivaTrueAndExcluidoIsNull(idEmpresa);
        } else {
            lojas = lojaRepository.findByIdEmpresaAndExcluidoIsNull(idEmpresa);
        }

        return lojas.stream()
                .map(this::prepararParaFront)
                .toList();
    }

    public LojaModel criarLoja(LojaModel lojaModel) {
        if (lojaModel.getIdEmpresa() == null) {
            lojaModel.setIdEmpresa(1L);
        }

        validarLoja(lojaModel);

        if (lojaModel.getAtiva() == null) {
            lojaModel.setAtiva(true);
        }

        LojaModel lojaSalva = lojaRepository.save(lojaModel);
        return prepararParaFront(lojaSalva);
    }

    public LojaModel buscarPorId(Long idLoja) {
        LojaModel loja = lojaRepository.findByIdLojaAndExcluidoIsNull(idLoja)
                .orElseThrow(() -> new RuntimeException("Item da loja não encontrado"));

        return prepararParaFront(loja);
    }

    public LojaModel atualizarLoja(Long idLoja, LojaModel lojaModel) {
        LojaModel loja = lojaRepository.findByIdLojaAndExcluidoIsNull(idLoja)
                .orElseThrow(() -> new RuntimeException("Item da loja não encontrado"));

        if (lojaModel.getIdEmpresa() != null) {
            loja.setIdEmpresa(lojaModel.getIdEmpresa());
        }

        loja.setNome(lojaModel.getNome());
        loja.setDescricao(lojaModel.getDescricao());
        loja.setCustoXp(lojaModel.getCustoXp());

        if (lojaModel.getAtiva() != null) {
            loja.setAtiva(lojaModel.getAtiva());
        }

        validarLoja(loja);

        LojaModel lojaAtualizada = lojaRepository.save(loja);
        return prepararParaFront(lojaAtualizada);
    }

    public void excluirLoja(Long idLoja) {
        LojaModel loja = lojaRepository.findByIdLojaAndExcluidoIsNull(idLoja)
                .orElseThrow(() -> new RuntimeException("Item da loja não encontrado"));

        loja.setAtiva(false);
        loja.setExcluido(LocalDate.now());

        lojaRepository.save(loja);
    }

    public LojaModel esgotarLoja(Long idLoja) {
        LojaModel loja = lojaRepository.findByIdLojaAndExcluidoIsNull(idLoja)
                .orElseThrow(() -> new RuntimeException("Item da loja não encontrado"));

        loja.setAtiva(false);

        LojaModel lojaAtualizada = lojaRepository.save(loja);
        return prepararParaFront(lojaAtualizada);
    }

    public LojaModel reporLoja(Long idLoja) {
        LojaModel loja = lojaRepository.findByIdLojaAndExcluidoIsNull(idLoja)
                .orElseThrow(() -> new RuntimeException("Item da loja não encontrado"));

        loja.setAtiva(true);

        LojaModel lojaAtualizada = lojaRepository.save(loja);
        return prepararParaFront(lojaAtualizada);
    }

    public LojaModel resgatarLoja(Long idLoja) {
        LojaModel loja = lojaRepository.findByIdLojaAndExcluidoIsNull(idLoja)
                .orElseThrow(() -> new RuntimeException("Item da loja não encontrado"));

        if (!Boolean.TRUE.equals(loja.getAtiva())) {
            throw new RuntimeException("Item da loja está esgotado ou inativo");
        }


        loja.setAtiva(false);

        LojaModel lojaAtualizada = lojaRepository.save(loja);
        lojaAtualizada.setResgatada(true);

        return prepararParaFront(lojaAtualizada);
    }

    private void validarLoja(LojaModel lojaModel) {
        if (lojaModel.getNome() == null || lojaModel.getNome().isBlank()) {
            throw new RuntimeException("O nome do item da loja é obrigatório");
        }

        if (lojaModel.getCustoXp() == null || lojaModel.getCustoXp() <= 0) {
            throw new RuntimeException("O custo em XP deve ser maior que zero");
        }
    }

    private LojaModel prepararParaFront(LojaModel loja) {
        if (Boolean.TRUE.equals(loja.getAtiva())) {
            loja.setQuantidadeDisponivel(1);
        } else {
            loja.setQuantidadeDisponivel(0);
        }

        loja.setCategoria("Benefício");
        loja.setIcone("Presente");
        loja.setCor("Azul");

        if (loja.getResgatada() == null) {
            loja.setResgatada(false);
        }

        return loja;
    }
}
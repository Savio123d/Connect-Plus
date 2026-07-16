package conne.connect.connect.Empresa.controller;

import conne.connect.connect.Empresa.dto.CadastroEmpresaDTO;
import conne.connect.connect.Empresa.dto.CadastroEmpresaResponseDTO;
import conne.connect.connect.Empresa.model.EmpresaModel;
import conne.connect.connect.Empresa.service.EmpresaService;
import conne.connect.connect.Usuario.dto.CadastroUsuarioEmpresaDTO;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping(path = "/api/empresas")
@RestController
public class EmpresaController {

    private final EmpresaService empresaService;

    public EmpresaController(EmpresaService empresaService) {
        this.empresaService = empresaService;
    }

    @PreAuthorize("@autorizacao.ehGestor()")
    @GetMapping
    public ResponseEntity<List<EmpresaModel>> findAll() {
        List<EmpresaModel> empresas = empresaService.findAll();
        return ResponseEntity.ok(empresas);
    }

    @PostMapping
    public ResponseEntity<CadastroEmpresaResponseDTO> cadastrarEmpresa(@Valid @RequestBody CadastroEmpresaDTO dto) {
        CadastroEmpresaResponseDTO resposta = empresaService.cadastrarEmpresa(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(resposta);
    }

    // Criar usuário é exclusivo do gestor, e somente dentro da própria empresa.
    @PreAuthorize("@autorizacao.gestorDaEmpresa(#idEmpresa)")
    @PostMapping("/{idEmpresa}/usuarios")
    public ResponseEntity<String> cadastrarUsuarioEmpresa(
            @PathVariable Long idEmpresa,
            @Valid @RequestBody CadastroUsuarioEmpresaDTO dto) {
        empresaService.cadastrarUsuarioEmpresa(idEmpresa, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body("Usuário cadastrado e vinculado à empresa com sucesso.");
    }

    @PreAuthorize("@autorizacao.gestorDaEmpresa(#idEmpresa)")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletar(@PathVariable("id") Long idEmpresa) {
        empresaService.excluirEmpresa(idEmpresa);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("@autorizacao.mesmaEmpresa(#idEmpresa)")
    @GetMapping("/{id}")
    public ResponseEntity<EmpresaModel> buscarPorId(@PathVariable("id") Long idEmpresa) {
        return empresaService.buscarPorId(idEmpresa)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("@autorizacao.gestorDaEmpresa(#idEmpresa)")
    @PutMapping("/{id}")
    public ResponseEntity<EmpresaModel> atualizar(
            @PathVariable("id") Long idEmpresa,
            @RequestBody EmpresaModel empresaModel) {
        return ResponseEntity.ok(empresaService.atualizarEmpresa(idEmpresa, empresaModel));
    }
}

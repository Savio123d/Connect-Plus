package conne.connect.connect.Controllers;

import conne.connect.connect.Models.ProjetoModel;
import conne.connect.connect.Services.ProjetoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping(path = "/api/projetos")
@RestController
public class ProjetoController {

    @Autowired
    private ProjetoService projetoService;

    @GetMapping
    public ResponseEntity<List<ProjetoModel>> findAll() {
        List<ProjetoModel> projetos = projetoService.findAll();
        return ResponseEntity.ok(projetos);
    }

    @PostMapping
    public ResponseEntity<ProjetoModel> criarProjeto(@RequestBody ProjetoModel projetoModel) {
        ProjetoModel projeto = projetoService.criarProjeto(projetoModel);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(projeto.getIdProjeto()).toUri();
        return ResponseEntity.created(uri).body(projeto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletar(@PathVariable("id") Long idProjeto) {
        projetoService.excluirProjeto(idProjeto);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<ProjetoModel>> buscarPorId(@PathVariable("id") Long idProjeto) {
        return ResponseEntity.ok(projetoService.buscarPorId(idProjeto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProjetoModel> atualizar(@PathVariable("id") Long idProjeto, @RequestBody ProjetoModel projetoModel) {
        return ResponseEntity.ok(projetoService.atualizarProjeto(idProjeto, projetoModel));
    }
}

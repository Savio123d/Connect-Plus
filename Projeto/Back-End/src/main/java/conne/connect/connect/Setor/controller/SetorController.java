package conne.connect.connect.Setor.controller;

import conne.connect.connect.Setor.model.SetorModel;
import conne.connect.connect.Setor.service.SetorService;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RequestMapping(path = "/api/setores")
@RestController
public class SetorController {

    private final SetorService setorService;

    public SetorController(SetorService setorService) {
        this.setorService = setorService;
    }

    @GetMapping
    public ResponseEntity<List<SetorModel>> findAll() {
        List<SetorModel> setores = setorService.findAll();
        return ResponseEntity.ok(setores);
    }

    @PostMapping
    public ResponseEntity<SetorModel> criarSetor(@RequestBody SetorModel setorModel) {
        SetorModel setor = setorService.criarSetor(setorModel);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(setor.getIdSetor()).toUri();
        return ResponseEntity.created(uri).body(setor);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletar(@PathVariable("id") Long idSetor) {
        setorService.excluirSetor(idSetor);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<SetorModel> buscarPorId(@PathVariable("id") Long idSetor) {
        return ResponseEntity.of(setorService.buscarPorId(idSetor));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SetorModel> atualizar(@PathVariable("id") Long idSetor, @RequestBody SetorModel setorModel) {
        return ResponseEntity.ok(setorService.atualizarSetor(idSetor, setorModel));
    }
}

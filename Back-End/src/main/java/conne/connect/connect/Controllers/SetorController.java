package conne.connect.connect.Controllers;

import conne.connect.connect.Models.SetorModel;
import conne.connect.connect.Services.SetorService;
import org.springframework.beans.factory.annotation.Autowired;
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

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RequestMapping(path = "/api/setores")
@RestController
public class SetorController {

    @Autowired
    private SetorService setorService;

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
    public ResponseEntity<Optional<SetorModel>> buscarPorId(@PathVariable("id") Long idSetor) {
        return ResponseEntity.ok(setorService.buscarPorId(idSetor));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SetorModel> atualizar(@PathVariable("id") Long idSetor, @RequestBody SetorModel setorModel) {
        return ResponseEntity.ok(setorService.atualizarSetor(idSetor, setorModel));
    }
}

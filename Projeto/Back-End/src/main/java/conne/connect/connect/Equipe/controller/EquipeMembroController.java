package conne.connect.connect.Equipe.controller;

import conne.connect.connect.Equipe.model.EquipeMembroModel;
import conne.connect.connect.Equipe.service.EquipeMembroService;
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

@RequestMapping(path = "/api/equipes-membro")
@RestController
public class EquipeMembroController {

    private final EquipeMembroService equipeMembroService;

    public EquipeMembroController(EquipeMembroService equipeMembroService) {
        this.equipeMembroService = equipeMembroService;
    }

    @GetMapping
    public ResponseEntity<List<EquipeMembroModel>> findAll() {
        List<EquipeMembroModel> equipesMembro = equipeMembroService.findAll();
        return ResponseEntity.ok(equipesMembro);
    }

    @PostMapping
    public ResponseEntity<EquipeMembroModel> criarEquipeMembro(@RequestBody EquipeMembroModel equipeMembroModel) {
        EquipeMembroModel equipeMembro = equipeMembroService.criarEquipeMembro(equipeMembroModel);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(equipeMembro.getIdEquipeMembro()).toUri();
        return ResponseEntity.created(uri).body(equipeMembro);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletar(@PathVariable("id") Long idEquipeMembro) {
        equipeMembroService.excluirEquipeMembro(idEquipeMembro);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<EquipeMembroModel> buscarPorId(@PathVariable("id") Long idEquipeMembro) {
        return ResponseEntity.of(equipeMembroService.buscarPorId(idEquipeMembro));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EquipeMembroModel> atualizar(@PathVariable("id") Long idEquipeMembro, @RequestBody EquipeMembroModel equipeMembroModel) {
        return ResponseEntity.ok(equipeMembroService.atualizarEquipeMembro(idEquipeMembro, equipeMembroModel));
    }
}

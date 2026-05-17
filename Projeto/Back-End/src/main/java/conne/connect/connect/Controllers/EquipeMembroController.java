package conne.connect.connect.Controllers;

import conne.connect.connect.Models.EquipeMembroModel;
import conne.connect.connect.Services.EquipeMembroService;
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
@RequestMapping(path = "/api/equipes-membro")
@RestController
public class EquipeMembroController {

    @Autowired
    private EquipeMembroService equipeMembroService;

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
    public ResponseEntity<Optional<EquipeMembroModel>> buscarPorId(@PathVariable("id") Long idEquipeMembro) {
        return ResponseEntity.ok(equipeMembroService.buscarPorId(idEquipeMembro));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EquipeMembroModel> atualizar(@PathVariable("id") Long idEquipeMembro, @RequestBody EquipeMembroModel equipeMembroModel) {
        return ResponseEntity.ok(equipeMembroService.atualizarEquipeMembro(idEquipeMembro, equipeMembroModel));
    }
}

package conne.connect.connect.Controllers;

import conne.connect.connect.Dto.DashboardResumoDTO;
import conne.connect.connect.Services.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "http://localhost:4200")
public class DashboardController {

    private  DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/resumo/{empresaId}")
    public ResponseEntity<DashboardResumoDTO> buscarResumo(@PathVariable Long empresaId) {
        DashboardResumoDTO resumo = dashboardService.buscarResumo(empresaId);
        return ResponseEntity.ok(resumo);
    }
}

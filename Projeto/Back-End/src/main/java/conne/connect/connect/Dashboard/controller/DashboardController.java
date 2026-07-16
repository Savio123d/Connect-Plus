package conne.connect.connect.Dashboard.controller;

import conne.connect.connect.Dashboard.dto.DashboardResumoDTO;
import conne.connect.connect.Dashboard.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    // Escopo multi-tenant: só enxerga o resumo da própria empresa.
    @PreAuthorize("@autorizacao.mesmaEmpresa(#empresaId)")
    @GetMapping("/resumo/{empresaId}")
    public ResponseEntity<DashboardResumoDTO> buscarResumo(@PathVariable Long empresaId) {
        return ResponseEntity.ok(dashboardService.buscarResumo(empresaId));
    }
}

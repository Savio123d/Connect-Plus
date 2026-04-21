import { Component } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';
import { Sidebar } from '../../components/sidebar/sidebar';

@Component({
  selector: 'app-dashboard-gestor',
  standalone: true,
  imports: [Sidebar, MatIconModule],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css',
})
export class Menu {
  totalEmpresasCadastradas = 0;
  usuariosAtivos = 1;
}
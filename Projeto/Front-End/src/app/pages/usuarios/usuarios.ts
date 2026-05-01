import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Sidebar } from '../../components/sidebar/sidebar';

@Component({
  selector: 'app-usuarios',
  standalone: true,
  imports: [CommonModule, Sidebar],
  templateUrl: './usuarios.html',
  styleUrl: './usuarios.css'
})
export class Usuarios implements OnInit {
  private http = inject(HttpClient);

  usuarios: any[] = [];
  carregando = true;
  erro = '';

  ngOnInit(): void {
    this.buscarUsuarios();
  }

  buscarUsuarios(): void {
    this.http.get<any[]>('http://localhost:8080/usuarios').subscribe({
      next: (res) => {
        this.usuarios = res;
        this.carregando = false;
      },
      error: () => {
        this.erro = 'Não foi possível carregar os usuários.';
        this.carregando = false;
      }
    });
  }
}
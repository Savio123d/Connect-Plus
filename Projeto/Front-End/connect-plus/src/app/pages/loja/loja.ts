import { Component } from '@angular/core';

@Component({
  selector: 'app-loja',
  standalone: true,
  templateUrl: './loja.html',
  styleUrls: ['./loja.css']
})
export class Loja {
  comprar(nome: string, preco: number) {
    alert(`Você comprou ${nome} por ${preco} XP`);
  }
}

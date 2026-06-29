import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'app-suporte',
  standalone: true,
  imports: [CommonModule, RouterLink, MatIconModule],
  templateUrl: './suporte.html',
  styleUrl: './suporte.css',
})
export class Suporte {}

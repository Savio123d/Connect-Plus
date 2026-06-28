import 'package:flutter/material.dart';

class AppColors {
  // Fundo geral e cards
  static const Color background = Color(0xFFF4F5FB); 
  static const Color card = Color(0xFFFFFFFF);

  // Bordas e sombras
  static const Color borderSoft = Color(0xFFE5E7EB);
  static const Color shadow = Color(0x0F000000); // Equivalente a rgba(0, 0, 0, 0.06)

  // Tipografia
  static const Color title = Color(0xFF1F2A44);
  static const Color text = Color(0xFF1F2A44);
  static const Color textSecondary = Color(0xFF6B7280);

  // Cores principais (usadas nos ícones do CSS)
  static const Color primaryDark = Color(0xFF4F46E5); // icone-empresa
  static const Color primaryLight = Color(0xFF7C3AED); // icone-usuarios

  // Campos de texto (Adaptados para combinar com o novo tema)
  static const Color inputBg = Color(0xFFFFFFFF);
  static const Color inputBorder = Color(0xFFE5E7EB);
  static const Color inputFocus = Color(0xFF4F46E5);

  // Alertas e informações (Adaptados usando um tom claro da cor principal)
  static const Color infoBg = Color(0xFFEEF2FF); 
  static const Color infoText = Color(0xFF4F46E5);
}
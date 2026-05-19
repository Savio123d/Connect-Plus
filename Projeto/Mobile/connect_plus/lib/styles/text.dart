import 'package:flutter/material.dart';
import 'colors.dart';

class AppTextStyles {
  static const TextStyle titulo = TextStyle(
    fontSize: 28,
    fontWeight: FontWeight.bold,
    color: AppColors.title,
    letterSpacing: -0.8,
  );

  static const TextStyle subtitulo = TextStyle(
    fontSize: 14,
    color: AppColors.textSecondary,
  );

  static const TextStyle label = TextStyle(
    fontSize: 14,
    fontWeight: FontWeight.w600,
    color: AppColors.text,
  );

  static const TextStyle input = TextStyle(
    fontSize: 15,
    color: AppColors.title,
  );

  static const TextStyle botao = TextStyle(
    fontSize: 16,
    fontWeight: FontWeight.w600,
    color: Colors.white,
  );

  static const TextStyle resultado = TextStyle(
    fontSize: 14,
    color: AppColors.infoText,
    fontWeight: FontWeight.w500,
  );
}
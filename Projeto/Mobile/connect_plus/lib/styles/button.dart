import 'package:flutter/material.dart';
import 'text.dart';

class AppButtonStyles {
  static ButtonStyle primary = ElevatedButton.styleFrom(
    minimumSize: const Size(double.infinity, 52),
    elevation: 0,
    padding: const EdgeInsets.symmetric(horizontal: 20),
    shape: RoundedRectangleBorder(
      borderRadius: BorderRadius.circular(18),
    ),
    textStyle: AppTextStyles.botao,
  );
}
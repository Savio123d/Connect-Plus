import 'package:flutter/material.dart';
import 'colors.dart';
import 'text.dart';

class AppInputStyles {
  static InputDecoration campo({
    required String label,
    required IconData icon,
  }) {
    return InputDecoration(
      labelText: label,
      labelStyle: AppTextStyles.label,
      prefixIcon: Icon(
        icon,
        color: AppColors.primaryLight,
      ),
      filled: true,
      fillColor: AppColors.inputBg,
      contentPadding: const EdgeInsets.symmetric(
        horizontal: 18,
        vertical: 16,
      ),
      border: OutlineInputBorder(
        borderRadius: BorderRadius.circular(20),
        borderSide: const BorderSide(
          color: AppColors.inputBorder,
          width: 1,
        ),
      ),
      enabledBorder: OutlineInputBorder(
        borderRadius: BorderRadius.circular(20),
        borderSide: const BorderSide(
          color: AppColors.inputBorder,
          width: 1,
        ),
      ),
      focusedBorder: OutlineInputBorder(
        borderRadius: BorderRadius.circular(20),
        borderSide: const BorderSide(
          color: AppColors.inputFocus,
          width: 1.5,
        ),
      ),
    );
  }
}
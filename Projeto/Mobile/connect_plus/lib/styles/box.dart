import 'package:flutter/material.dart';
import 'colors.dart';

class AppBoxStyles {
  static BoxDecoration card = BoxDecoration(
    color: AppColors.card,
    borderRadius: BorderRadius.circular(28),
    border: Border.all(
      color: AppColors.borderSoft,
      width: 1,
    ),
    boxShadow: const [
      BoxShadow(
        color: AppColors.shadow,
        blurRadius: 40,
        offset: Offset(0, 18),
      ),
    ],
  );

  static BoxDecoration badgeBox = BoxDecoration(
    color: const Color(0xE0FFFFFF),
    borderRadius: BorderRadius.circular(22),
    border: Border.all(
      color: AppColors.borderSoft,
      width: 1,
    ),
    boxShadow: const [
      BoxShadow(
        color: AppColors.shadow,
        blurRadius: 40,
        offset: Offset(0, 18),
      ),
    ],
  );

  static BoxDecoration alertInfo = BoxDecoration(
    gradient: const LinearGradient(
      colors: [
        Color(0xF0E8F7FF),
        Color(0xF4F4F0FF),
      ],
      begin: Alignment.topLeft,
      end: Alignment.bottomRight,
    ),
    borderRadius: BorderRadius.circular(20),
    border: Border.all(
      color: AppColors.borderSoft,
      width: 1,
    ),
  );
}
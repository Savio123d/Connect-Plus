import 'package:flutter/material.dart';

// Importando os seus estilos
import '../styles/box.dart';
import '../styles/button.dart';
import '../styles/colors.dart';
import '../styles/input.dart';
import '../styles/text.dart';

class CadastroScreen extends StatefulWidget {
  const CadastroScreen({super.key});

  @override
  State<CadastroScreen> createState() => _CadastroScreenState();
}

class _CadastroScreenState extends State<CadastroScreen> {
  TextEditingController emailController = TextEditingController();
  TextEditingController senhaController = TextEditingController();
  String testeresultado = '';

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: AppColors.background,
      appBar: AppBar(
        title:  Text('Login', style: AppTextStyles.titulo),
        backgroundColor: Colors.transparent,
        elevation: 0,
        centerTitle: true,
      ),
      body: Center(
        child: SingleChildScrollView(
          padding:  EdgeInsets.all(16.0),
          child: Container(
            padding:  EdgeInsets.all(24.0),
            decoration: AppBoxStyles.card,
            child: Column(
              mainAxisSize: MainAxisSize.min,
              children: [
                TextField(
                  controller: emailController,
                  style: AppTextStyles.input,
                  decoration: AppInputStyles.campo(
                    label: 'Email',
                    icon: Icons.email_outlined,
                  ),
                ),
                SizedBox(height: 16),
                TextField(
                  controller: senhaController,
                  obscureText: true, // Oculta a senha por padrão
                  style: AppTextStyles.input,
                  decoration: AppInputStyles.campo(
                    label: 'Senha',
                    icon: Icons.lock_outline,
                  ),
                ),
                const SizedBox(height: 24),
                ElevatedButton(
                  // Aplica o estilo de botão definido, ajustando a cor de fundo
                  style: AppButtonStyles.primary.copyWith(
                    backgroundColor: MaterialStateProperty.all(AppColors.primaryDark)
                  ),
                  onPressed: () {
                    setState(() {
                      testeresultado = 'Email: ${emailController.text}\nSenha: ${senhaController.text}';
                    });
                  },
                  child:  Text('ENTRAR', style: AppTextStyles.botao),
                ),
                const SizedBox(height: 16),
             // Exibe o resultado de forma estilizada apenas se houver algo digitado
                  Container(
                    width: double.infinity,
                    padding:  EdgeInsets.all(16),
                    decoration: AppBoxStyles.alertInfo,
                    child: Text(
                      testeresultado,
                      style: AppTextStyles.resultado,
                      textAlign: TextAlign.center,
                    ),
                  ),
              ],
            ),
          ),
        ),
      ),
    );
  }
}
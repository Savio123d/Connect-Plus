export interface DesempenhoEquipe {
  mes: number;
  total: number;
}

export interface DashboardResumo {
  usuariosAtivos?: number;
  projetosAtivos?: number;
  tarefasConcluidas?: number;
  tarefasEmAndamento?: number;
  tarefasPendentes?: number;
  tarefasAtrasadas?: number;
  feedbacks?: number;

  desempenhoEquipe?: DesempenhoEquipe[];
  tarefasConcluidasPorMes?: DesempenhoEquipe[];
}

export interface DashboardCard {
  titulo: string;
  valor: number;
  icone: string;
  iconeClasse: string;
}

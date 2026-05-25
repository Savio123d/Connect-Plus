import {
  ApexAxisChartSeries,
  ApexChart,
  ApexDataLabels,
  ApexFill,
  ApexPlotOptions,
  ApexXAxis,
  ApexYAxis,
} from 'ng-apexcharts';

export type BarChartOptions = {
  series: ApexAxisChartSeries;
  chart: ApexChart;
  xaxis: ApexXAxis;
  yaxis: ApexYAxis;
  plotOptions: ApexPlotOptions;
  dataLabels: ApexDataLabels;
  fill: ApexFill;
};

export class BarChartBuilder {
  private options: BarChartOptions = {
    series: [],
    chart: {
      type: 'bar',
      height: 220,
      toolbar: { show: false },
    },
    xaxis: {
      categories: [],
    },
    yaxis: {
      min: 0,
      tickAmount: 4,
    },
    plotOptions: {
      bar: {
        borderRadius: 6,
        columnWidth: '50%',
        distributed: true,
      },
    },
    dataLabels: {
      enabled: false,
    },
    fill: {
      colors: ['#22c55e', '#3b82f6', '#eab308', '#ef4444'],
    },
  };

  withCategories(categories: string[]): this {
    this.options = {
      ...this.options,
      xaxis: {
        ...this.options.xaxis,
        categories,
      },
    };

    return this;
  }

  withSeries(name: string, data: number[]): this {
    this.options = {
      ...this.options,
      series: [
        {
          name,
          data,
        },
      ],
    };

    return this;
  }

  build(): BarChartOptions {
    return { ...this.options };
  }
}

import {
  ApexAxisChartSeries,
  ApexChart,
  ApexDataLabels,
  ApexFill,
  ApexStroke,
  ApexXAxis,
  ApexYAxis,
} from 'ng-apexcharts';

export type AreaChartOptions = {
  series: ApexAxisChartSeries;
  chart: ApexChart;
  xaxis: ApexXAxis;
  yaxis: ApexYAxis;
  stroke: ApexStroke;
  fill: ApexFill;
  dataLabels: ApexDataLabels;
};

export class AreaChartBuilder {
  private options: AreaChartOptions = {
    series: [],
    chart: {
      type: 'area',
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
    stroke: {
      curve: 'smooth',
      width: 3,
    },
    fill: {
      type: 'gradient',
      gradient: {
        shadeIntensity: 1,
        opacityFrom: 0.5,
        opacityTo: 0.1,
        stops: [0, 100],
      },
    },
    dataLabels: {
      enabled: false,
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

  build(): AreaChartOptions {
    return { ...this.options };
  }
}

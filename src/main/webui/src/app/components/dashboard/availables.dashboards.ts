import { HttpClient } from "@angular/common/http";
import { inject, Injectable } from "@angular/core";
import { ChartData } from "chart.js";
import { catchError, delay, Observable, of, retry, retryWhen, startWith, timer } from "rxjs";


export interface DataSupplier {
    loadData(projectId: number): Observable<ChartData<'pie', number[], string | string[]>>;
}

export interface DashboardWidget {
    id: string;
    title: string;
    type: 'chart' | 'table' | 'kpi';
    chartType?: 'line' | 'bar' | 'pie' | 'doughnut';
    cols: number;
    rows: number;
}

export interface DashboardLayout {
    id: string;
    name: string;
    widgets: DashboardWidget[];
    layout: string[]; // Grid layout representation
}

@Injectable({ providedIn: 'root' })
export class AvailablesDashboards {
    private readonly httpClient = inject(HttpClient);
    private readonly API_URL = 'http://localhost:8080/api/projects';

    public all: DashboardWidget[] = [{
        id: 'tickets-by-day',
        title: 'Tickets por Dia',
        type: 'chart',
        chartType: 'line',
        cols: 2,
        rows: 1
    }, {
        id: 'tickets-by-status',
        title: 'Tickets por Status',
        type: 'chart',
        chartType: 'pie',
        cols: 1,
        rows: 1
    }, {
        id: 'tickets-by-priority',
        title: 'Tickets por Prioridade',
        type: 'chart',
        chartType: 'bar',
        cols: 2,
        rows: 1
    }, {
        id: 'performance-kpi',
        title: 'KPIs de Performance',
        type: 'kpi',
        cols: 1,
        rows: 1
    }, {
        id: 'recent-tickets',
        title: 'Tickets Recentes',
        type: 'table',
        cols: 2,
        rows: 2
    }];

    public loadData(chart: DashboardWidget, projectId: number): Observable<ChartData<'pie', number[], string | string[]>> {
        return this.httpClient.get<ChartData<'pie', number[], string | string[]>>(`${this.API_URL}/${projectId}/dashboard/${chart.id}`)
                              .pipe(retry(1), 
                                    startWith(this.getLoadingChartData()),
                                    catchError(error =>{
                                        console.log("Error requesting data", error);
                                        return of(this.getErrorChartData());
                                    }),
                                    retry({
                                        delay: (error, retryCount) => {
                                            // Retenta apenas para erros de rede/timeout
                                            if (error.status && error.status >= 400 && error.status < 500) {
                                                throw error; // Não retenta para erros 4xx
                                            }
  
                                            console.log(`Tentativa ${retryCount} falhou. Retentando...`);
                                            return timer(1000 * retryCount); // Delay crescente
                                        }
                                    })
                                );
    }

    private getLoadingChartData(): ChartData<'pie', number[], string | string[]> {
        return {
            labels: ['Carregando...'],
            datasets: [{
            data: [100],
            backgroundColor: ['#e0e0e0'],
            label: 'Carregando dados'
            }]
        };
        }

        private getErrorChartData(): ChartData<'pie', number[], string | string[]> {
        return {
            labels: ['Erro ao carregar', 'Tente novamente'],
            datasets: [{
            data: [70, 30],
            backgroundColor: ['#ff6b6b', '#ffe66d'],
            label: 'Status'
            }]
        };
        }
}
<template>
    <div>
        <h3>Anomalies par Etat</h3>
        <Pie :chart-options="chartOptions" :chart-data="chartData" :chart-id="chartId" :dataset-id-key="datasetIdKey"
            :plugins="plugins" :css-classes="cssClasses" :styles="styles" :width="width" :height="height" />
    </div>
</template>

<script>
import { Pie } from 'vue-chartjs';
import { Chart as ChartJS, Title, Tooltip, Legend, ArcElement, CategoryScale } from 'chart.js'

ChartJS.register(Title, Tooltip, Legend, ArcElement, CategoryScale)

export default {
    name: 'PieChart',
    components: { Pie },
    props: {
        chartId: {
            type: String,
            default: 'Pie-chart'
        },
        datasetIdKey: {
            type: String,
            default: 'label'
        },
        width: {
            type: Number,
            default: 400
        },
        height: {
            type: Number,
            default: 400
        },
        cssClasses: {
            default: '',
            type: String
        },
        styles: {
            type: Object,
            default: () => { }
        },
        plugins: {
            type: Object,
            default: () => { }
        },
        treatments: Array
    },
    computed: {
        chartData() {
            let labelStatus = [];
            let countStatus = [];
            for (let index = 0; index < this.treatments.length; index++) {
                const element = this.$capitalize(this.treatments[index]["status"]);
                
                if (labelStatus.includes(element)) {
                    countStatus[labelStatus.indexOf(element)]++;
                } else {
                    labelStatus.push(element);
                    countStatus.push(1);
                }
            }
            return {
                labels: labelStatus,
                datasets: [{
                    data: countStatus,
                    backgroundColor: ['#41B883', '#E46651', '#00D8FF', '#DD1B16', '#FFFC00', '#FF5733', '#6eff33', '#33ffd4', '#3333ff', '#ff33e9']
                }]
            }
        },
        chartOptions() {
            return {
                responsive: true,
                maintainAspectRatio: true
            }
        }
    },
}
</script>
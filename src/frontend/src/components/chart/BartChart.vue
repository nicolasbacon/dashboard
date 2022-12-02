<template>
    <div>
        <h3>Anomalies par Criticité</h3>
        <Bar :chart-options="chartOptions" :chart-data="chartData" :chart-id="chartId" :dataset-id-key="datasetIdKey"
            :plugins="plugins" :css-classes="cssClasses" :styles="styles" :width="width" :height="height" />
    </div>
</template>
  
<script>
import { Bar } from 'vue-chartjs'
import { Chart as ChartJS, Title, Tooltip, Legend, BarElement, CategoryScale, LinearScale } from 'chart.js'

ChartJS.register(Title, Tooltip, Legend, BarElement, CategoryScale, LinearScale)



export default {
    name: 'BarChart',
    components: { Bar },
    props: {
        chartId: {
            type: String,
            default: 'bar-chart'
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
        issues: Array
    },
    computed: {
        chartData() {
            let labelImpact = [];
            let countImpact = [];
            for (let index = 0; index < this.issues.length; index++) {
                const element = this.$capitalize(this.issues[index]["impact"]);
                if (labelImpact.includes(element)) {
                    countImpact[labelImpact.indexOf(element)]++;
                } else {
                    labelImpact.push(element);
                    countImpact.push(1);
                }
            }
            return {
                labels: labelImpact,
                datasets: [{
                    data: countImpact,
                    backgroundColor: ['#41B883', '#E46651', '#00D8FF', '#DD1B16', '#FFFC00', '#FF5733', '#6eff33', '#33ffd4', '#3333ff', '#ff33e9'],
                    label: ['Criticité']
                }]
            }
        },
        chartOptions() {
            return { 
                responsive: true 
            }
        }
    }
}
</script>
  
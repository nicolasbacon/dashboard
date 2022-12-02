<template>
    <div>
        <div class="box">
            <div class="box">
                <label for="start-date">Start Date :</label>
                <input id="start-date" type="date" v-model="startDate">
                <label for="end-date">End Date :</label>
                <input id="end-date" type="date" v-model="endDate">
            </div>
            <div class="item">
                <button @click="actualiser()">Actualiser</button>
            </div>
        </div>
        <Line :chart-options="chartOptions" :chart-data="chartData" :chart-id="chartId" :dataset-id-key="datasetIdKey"
            :plugins="plugins" :css-classes="cssClasses" :styles="styles" :width="width" :height="height" />
    </div>
</template>

<script>
import { Line } from 'vue-chartjs';
import { Chart as ChartJS, Title, Tooltip, Legend, LineElement, LinearScale, PointElement, CategoryScale } from 'chart.js'

ChartJS.register(Title, Tooltip, Legend, LineElement, LinearScale, PointElement, CategoryScale)

export default {
    name: 'LineChar',
    components: { Line },
    props: {
        chartId: {
            type: String,
            default: 'Line-chart'
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
            return {
                labels: this.labels,
                datasets: this.datasets
            }
        },
        chartOptions() {
            return {
                responsive: true,
                maintainAspectRatio: true
            }
        }
    },
    data() {
        return {
            startDate: Date,
            endDate: Date,
            labels: [],
            datasets: [
                {
                    label: '',
                    backgroundColor: '',
                    data: []
                }
            ]
        }
    },
    methods: {
        actualiser() {
            let startDate = new Date(this.startDate);
            let endDate = new Date(this.endDate);
            if (!isNaN(startDate) && !isNaN(endDate)) {
                this.labels = getMonthLabel(startDate, endDate);

                // Trier par date sur la periode
                let treatments = [];
                this.treatments.forEach(element => {
                    let updatedAt = new Date(element['updatedAt']);
                    if ((updatedAt.getTime() <= endDate.getTime() && updatedAt.getTime() >= startDate.getTime())) treatments.push(element);
                });

                //Trier par status 
                let object = {};
                treatments.forEach(element => {
                    if (!object[element['status']]) object[element['status']] = [];
                    object[element['status']].push(element);
                });


                // Compter nombre par mois
                let array = [];
                // Pour chaque status dans object
                for (const key in object) {

                    let monthDiff = getMonthDifference(startDate, endDate) + 1;
                    let years = startDate.getFullYear();

                    // On declare un tableau qui vas contenir le nombre de treatment par mois
                    let data = [];
                    // Pour chaque mois de chaque année
                    for (let month = startDate.getMonth(); monthDiff > 0; month++, monthDiff--) {

                        if (month == 12) {
                            month = 0;
                            years++;
                        }

                        // On declare un compteur de nombre de treatment
                        let compteur = 0;
                        object[key].forEach(element => {
                            let elementDate = new Date(element['updatedAt']);
                            if (elementDate.getFullYear() == years) {
                                if (elementDate.getMonth() == month) {
                                    compteur++;
                                }
                            }
                        });
                        data.push(compteur);
                    }
                    let randomColor = '#' + Math.floor(Math.random() * 16777215).toString(16);
                    array.push({
                        label: key,
                        backgroundColor: randomColor,
                        data: data
                    });
                }
                this.datasets = array;
            }
        }
    }
}

function getMonthDifference(startDate, endDate) {
    return (
        endDate.getMonth() -
        startDate.getMonth() +
        12 * (endDate.getFullYear() - startDate.getFullYear())
    );
}

function getMonthLabel(startDate, endDate) {
    const months = ["Janvier", "Février", "Mars", "Avril", "Mai", "Juin", "Juillet", "Août", "Septembre", "Octobre", "Novembre", "Décembre"];
    let labels = [];
    let monthDiff = getMonthDifference(startDate, endDate) + 1;
    for (let index = startDate.getMonth(); monthDiff > 0; index++, monthDiff--) {
        if (index == months.length) index = 0;
        labels.push(months[index]);
    }
    return labels;
}
</script>

<style scoped>
.item {
    display: grid;
    justify-content: center;
    align-content: center;
}

.box {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    align-items: center;
    gap: 10px;
}

input {
    width: 10em;
    height: 2em;
}

button {
    background-color: #FFF;
    border: 1px grey solid;
    border-radius: 10px;
    padding: 7px;
}
</style>
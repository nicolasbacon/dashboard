<template lang="">
    <div v-if="project != null">
        <h1>{{ project.name }}</h1>

        <div class="grid-container" v-if="project.lstRequests != null && treatments != null">
            <BartChart :issues="project.lstRequests" />
            <DoughnutChart :treatments="treatments" />
            <LineChart :treatments="treatments" />
        </div>
        <div v-else>Aucune données disponible </div>
    </div>
    <div v-else>Waiting ...</div>
</template>
<script>
import axios from 'axios';
import BartChart from "./chart/BartChart.vue";
import DoughnutChart from "./chart/DoughnutChart.vue";
import LineChart from './chart/LineChart.vue'

export default {
    props: ['projectId', 'origine'],
    data() {
        return {
            project: null,
            treatments: null,
        };
    },
    beforeCreate() {
        axios
            .get('/project/' + this.origine + '/' + this.projectId)
            .then((response) => {
                this.project = response.data;
            }).catch( () => {
                this.$router.push({ path: '/NotFound' });
            });
        axios
            .get('/project/' + this.origine + '/' + this.projectId + '/treatments')
            .then((response) => {
                this.treatments = response.data;
            }).catch( () => {
                    this.$router.push({ path: '/NotFound' });
            });
    },
    components: { BartChart, DoughnutChart, LineChart }
}
</script>
<style scoped>
.grid-container {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 100px;
    padding: 100px;
}

.grid-item {
    grid-column: 1/3;
}
</style>
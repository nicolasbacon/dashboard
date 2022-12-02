<template lang="">
    <div v-if="detailsProject != null">
        <h1>{{ detailsProject.name }}</h1>

        <div class="grid-container" v-if="detailsProject.lstRequests != null && treatments != null">
            <BartChart :issues="detailsProject.lstRequests" />
            <DoughnutChart :treatments="treatments" />
            <LineChart :treatments="treatments" />
        </div>
        <div v-else>Aucune données disponible</div>
    </div>
    <div v-else>Waiting ...</div>
</template>
<script>
import { mapState } from 'vuex'
import axios from 'axios'
import BartChart from "./chart/BartChart.vue";
import DoughnutChart from "./chart/DoughnutChart.vue";
import LineChart from './chart/LineChart.vue'
export default {
    data() {
        return {
            treatments: null,
        }
    },
    computed: {
        ...mapState({
            detailsProject: 'detailsProject'
        }),
    },
    mounted() {
        axios
            .get('/project/' + this.detailsProject['origine'] + "/" + this.detailsProject['projectId'])
            .then((response) => {
                this.$store.dispatch('updateDetailsProject', response.data);
            }).catch(() => {
                this.$router.push({ path: '/NotFound' });
            });
        axios
            .get('/project/' + this.detailsProject['origine'] + "/" + this.detailsProject['projectId'] + "/treatments")
            .then((response) => {
                this.treatments = response.data;
            }).catch(() => {
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
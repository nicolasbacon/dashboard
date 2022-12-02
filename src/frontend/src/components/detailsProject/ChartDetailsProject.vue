<template lang="">
    <div>
        <h1>{{ detailsProject.name }}</h1>

        <div class="grid-container">
            <div>
                <img :src="getAnomaliesByCriticalityURL()" alt="Diagramme d'anomalies par criticité" class="img" @load="isAnomaliesByCriticalityLoaded = true" :key="reload"/>
                <div v-if="isAnomaliesByCriticalityLoaded == false">
                    <h2 >Ce diagramme n'est pas disponible</h2>
                </div>
                <div v-else class="div-download">
                    <a :href="getAnomaliesByCriticalityURL()" download="AnomaliesByCriticality.png">Download</a>
                </div>
            </div>
            <div>
                <img :src="getAnomaliesByStateURL()" alt="Diagramme d'anomalies par etat" class="img" @load="isAnomaliesByStateLoaded = true" :key="reload"/>
                <div v-if="isAnomaliesByStateLoaded == false" >
                    <h2 >Ce diagramme n'est pas disponible</h2>
                </div>
                <div v-else class="div-download">
                    <a :href="getAnomaliesByStateURL()" download="AnomaliesByState.png">Download</a>
                </div>
            </div>
            <div>
                <div class="input-date">
                    <label for="start-period">Start Date :</label>
                    <input id="start-period" type="date" v-model="startPeriod">
                    <label for="end-period">End Date :</label>
                    <input id="end-period" type="date" v-model="endPeriod">
                </div>
                <img :src="getStatusEvolutionChartByWeekURL()" alt="Diagramme d'évolution des états par semaines" class="img" @load="isStatusEvolutionChartByWeekLoaded = true" :key="reload"/>
                <div v-if="startPeriod == null || endPeriod == null">
                    <h2 >Veuillez selectioner deux dates</h2>
                </div>
                <div v-else-if="isStatusEvolutionChartByWeekLoaded == false" >
                    <h2 >Ce diagramme n'est pas disponible</h2>
                </div>
                <div v-else class="div-download">
                    <a :href="getStatusEvolutionChartByWeekURL()" download="StatusEvolutionChartByWeek.png">Download</a>
                </div>
            </div>
        </div>
    </div>
</template>
<script>
import { mapState } from 'vuex'
export default {
    data() {
        return {
            isAnomaliesByCriticalityLoaded: false,
            isAnomaliesByStateLoaded: false,
            isStatusEvolutionChartByWeekLoaded: false,
            startPeriod: null,
            endPeriod: null,
            reload: 0
        }
    },
    mounted() {

    },
    computed: {
        ...mapState({
            detailsProject: 'detailsProject'
        }),
    },
    methods: {
        getAnomaliesByCriticalityURL() {
            return ('/' + this.detailsProject['origin'] + '/' + this.detailsProject['originId'] + '/AnomalyByCriticality');
        },
        getAnomaliesByStateURL() {
            return ('/' + this.detailsProject['origin'] + '/' + this.detailsProject['originId'] + '/AnomalyByState');
        },
        getStatusEvolutionChartByWeekURL() {
            if (this.startPeriod != null && this.endPeriod != null) {
                return (
                    '/' + this.detailsProject['origin'] + 
                    '/' + this.detailsProject['originId'] + 
                    '/' + this.startPeriod +
                    '/' + this.endPeriod +
                    '/StatusEvolutionChartByWeek');
                }
        }
    }
}
</script>
<style scoped lang="scss">
.grid-container {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 90px;
    padding: 2vw;
    padding-bottom: 5vw;
}

.input-date {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 10px;
    padding: 10px;
}

.img {
    max-width: 100%;
    height: auto;
}

.div-download {
    padding: 5%;
    & a {
        display: block;
        width: fit-content;
        margin-left: 28%;
    }
}

a:link,
a:visited,
a:hover,
a:active {
    box-sizing: border-box;
    color: black;
    border: solid 2px black;
    border-radius: 10px;
    padding: 1%;
    text-align: center;
    text-decoration: none;
}
</style>
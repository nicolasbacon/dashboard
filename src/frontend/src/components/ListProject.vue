<template>
  <h1>Liste des projets</h1>

  <button @click="sendUpdateDatabase()">Mettre à jour la liste des projets</button>

  <div class="grid-container" v-if="lstProjects.length != 0">
    <div v-for="project in lstProjects" :key="project.projectId" class="grid-item" @click="goOnDetails(project)">
      <p>{{ project.name }}</p>
    </div>
  </div>
  <div v-else>{{ message }}</div>

</template>

<script>

import axios from 'axios'
import { mapState } from 'vuex'
export default {
  name: "ListProject",
  data() {
    return {
      message: String
    };
  },
  mounted() {
    this.message = "Waiting...";
    this.$store.dispatch('updateLstProjects').then(() => {
      if (this.lstProjects.length == 0) { this.message = "Please update database" }
      else { this.message = "" }
    });
  },
  methods: {
    goOnDetails(project) {
      this.$store.dispatch('updateDetailsProject', project);
      this.$router.push({ name: 'detailsProject' });
    },
    sendUpdateDatabase() {
      this.message = "Updating database in progress..."
      axios
        .get('/updateDatabase')
        .then(() => {
          this.$store.dispatch('updateLstProjects');
        }).catch((error) => {
          if (error.response) {
            console.log(error.response.data.message);
          }
        });
    }

  },
  computed: {

    ...mapState({
      lstProjects: 'lstProjects'
    })
  },
}
</script>

<style scoped lang="scss">
.grid-container {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 50px;
  margin: 50px 150px 0px 150px;
}

.grid-item {
  color: #2c3e50;
  font-weight: bold;
  text-align: center;
  background-color: #41B883;
  border: solid 1px #2c3e50;
  border-radius: 10px;
  cursor: pointer;

  &:hover {
    background-color: #eeeeee;
  }

  p {
    margin: 0;
    padding: 10px 0;
  }
}

button {
  color: #2c3e50;
  font-weight: bold;
  margin: 10px;
  cursor: pointer;
  padding: 7px;
  text-align: center;
  background-color: #41B883;
  border: solid 1px #2c3e50;
  border-radius: 10px;
  box-shadow: -1px 5px #999;

  &:active {
    box-shadow: 0 2px #666;
    transform: translate(-1px,3px);
  }
}
</style>
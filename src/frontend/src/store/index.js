import { createStore } from 'vuex'
import axios from 'axios';
import createPersistedState from "vuex-persistedstate";

export default createStore({
  state: {
    lstProjects: [],
    detailsProject: {},
  },
  getters: {
  },
  mutations: {
    ADD_PROJECTS_TO_LIST(state, lstProjects) {
      state.lstProjects = lstProjects;
    },
    PUT_DETAILS_PROJECT(state, project) {
      state.detailsProject = project;
    },
  },
  actions: {
    updateLstProjects({commit}) {
      return axios
      .get('/projects')
      .then((response) => {
        commit('ADD_PROJECTS_TO_LIST', response.data);
      });
    },
    updateDetailsProject({commit}, project) {
      commit('PUT_DETAILS_PROJECT', project)
    },
  },
  modules: {
  },
  plugins: [createPersistedState({
    paths: ['detailsProject']
  })]
})

import { createRouter, createWebHashHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    name: 'projects',
    component: () => import(/* webpackChunkName: "projects" */ "../views/HomePage.vue"),
  },
  {
    path: '/project/:origine/:projectId',
    name: 'project',
    props: true,
    component: () => import(/* webpackChunkName: "project" */ "@/components/DetailsProject.vue"),
  },
  { path: '/:pathMatch(.*)*',name: 'Not Found', redirect: '/'},//component: () => import(/* webpackChunkName: "notfound" */ "@/views/NotFound.vue")},
  // {
  //   path: '/detailsProject',
  //   name: 'detailsProject',
  //   component: () => import(/* webpackChunkName: "detailsProject" */ "@/components/DetailsProjectTest.vue"),
  // },
  {
    path: '/detailsProject',
    name: 'detailsProject',
    component: () => import(/* webpackChunkName: "detailsProject" */ "@/components/detailsProject/ChartDetailsProject.vue"),
  },
]

const router = createRouter({
  history: createWebHashHistory(),
  routes
})

export default router

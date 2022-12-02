// const { defineConfig } = require('@vue/cli-service')
// module.exports = defineConfig({
//   transpileDependencies: true
// })
module.exports = {
  // https://cli.vuejs.org/config/#devserver-proxy
  devServer: {
      port: 3000,
      proxy: {
          '/': {
              target: 'http://localhost:8080',
              ws: false,
              changeOrigin: true
          }
      }
  }
}
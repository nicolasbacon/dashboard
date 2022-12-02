<template>
  <nav>
    <h1 class="app-name">Dashboard</h1>
    <div class="menu-item">
      <router-link to="/">Projects</router-link>
    </div>
  </nav>

  <div>
    <router-view />
  </div>
  <footer>
    <div class="footer-text">
      Le footer de l'app
    </div>
  </footer>
</template>

<style lang="scss">
body {
  margin: 0px;
  min-height: 100vh;
}

footer {
  background-color: #41B883;
  position: fixed;
  bottom: 0;
  width: 100%;
  height: 2.5rem;
}

.footer-text {
  padding-top: 1%;
}

#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
}

nav {
  padding: 30px;
  background-color: #41B883;
  border-bottom-left-radius: 7px;
  border-bottom-right-radius: 7px;
  display: flex;
  
  a {
    text-decoration: none;
    font-weight: bold;
    color: #2c3e50;
    padding: 5px;

    &:hover{
      color: #e3b448;
    }

    &.router-link-exact-active {
    background-color: #1dd482;
    border-radius: 5px;
  }
  }
  
  .app-name {
    margin: 0px;
    text-align: start;
    width: fit-content;
  }
  
  .menu-item {
    display: flex;
    justify-content: flex-end;
    align-items: center;
    width: 100%;
  }
}
</style>

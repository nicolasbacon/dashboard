<template>
    <div>
        <h1>404 Not Found</h1>
        <p>
            <router-link :to="{name: 'projects'}">Go home</router-link>
        </p>
    </div>
</template>
<script>
export default {
    name: 'NotFound'
}
</script>
<style lang="">
    
</style>
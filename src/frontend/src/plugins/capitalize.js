export default {
    install: (app) => {
        app.config.globalProperties.$capitalize = (string) => {
            return string.charAt(0).toUpperCase() + string.toLowerCase().replaceAll('_', ' ').slice(1);
        }
    }
}

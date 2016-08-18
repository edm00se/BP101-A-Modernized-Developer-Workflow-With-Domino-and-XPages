module.exports = (request, next) => {
  let condition = (request.url === 'api.xsp/junkDrawer')
  if (condition) {
    request.url = 'static/mocks/junkDrawer.json'
  }
  next((response) => {
  })
}

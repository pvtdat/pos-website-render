const { createProxyMiddleware } = require("http-proxy-middleware");
const url_server = process.env.REACT_APP_API_ENDPOINT;
module.exports = function (app) {
  app.use(
    ["/api"],
    createProxyMiddleware({
      // target: "http://localhost:8080",
        target: url_server,
      //   target: "",
        changeOrigin: true,
    })
  );
};
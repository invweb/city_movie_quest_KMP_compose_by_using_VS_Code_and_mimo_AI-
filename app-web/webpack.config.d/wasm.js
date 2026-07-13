const path = require('path');

module.exports = {
    experiments: {
        asyncWebAssembly: true
    },
    resolve: {
        fallback: {
            "fs": false,
            "path": false
        }
    },
    devServer: {
        headers: {
            "Cross-Origin-Opener-Policy": "same-origin",
            "Cross-Origin-Embedder-Policy": "require-corp"
        }
    }
};

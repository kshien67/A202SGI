var isDev = true;

if (process && process.env && process.env.NODE_ENV === 'production') isDev = false;

module.exports = isDev;
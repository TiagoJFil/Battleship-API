FROM node:18.12.1

WORKDIR /usr/app

COPY package.json /usr/app
RUN npm install

COPY tsconfig.json /usr/app
COPY webpack.config.js /usr/app

COPY public/ /usr/app/public/
COPY src/ /usr/app/src/
ENV WEB_PORT=3030

CMD npx webpack serve --port $WEB_PORT

EXPOSE 8080
FROM node

RUN npm install -g serve

COPY build/ /home/build

WORKDIR /home/build

CMD [ "serve", "-s" ]

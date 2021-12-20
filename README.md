
# Vaccination monitoring system - SERVER

This is a server side for console application with client/server architecture. It can communicate and deal with multiple users at the same time. Depending on the choices entered by client, it shows different menus and takes various actions. Evry time the server is started, server connects to hosted MySQL database.


## Features

- Communication with multiple clients at the same time
- Communication with MySQL database (search, update..)
- Covid certificate generation (.txt file)
- Every exception is handled
- Every communication is handled in separete thread


## Run Locally

Clone the server side of application

```bash
  git clone https://github.com/Djolee00/vaccination-monitoring-system-SERVER.git
```

Clone the client side of application

```bash
  git clone https://github.com/Djolee00/vaccination-monitoring-system-CLIENT.git
```

Start the server in any IDE for Java



Start the client in any IDE for Java




## Purpose 

The main purpose of this application was to learn how to use sockets and manage TCP communcation between client and server. Server can communicate in the same time with multiple clients. Also every exception is carefully handled. I was dealing with MySQL databsase for the first time and it was also a grate challange.

# About this repository
This repository is only used for experimenting on unit testing, third-party api provider, documentation, Swagger, JWT, CI/CD, Logging and monitoring and more

This repository **doesn't focus** on the functionality of Banking system. 
- It has only 1 function, transferring money from one account to another.
- Accounts can have only one of any currency in ISO 4217 standard. 
- An account can transfer with different currency from its own.

More update will be added in this README.md

# Stack

- Maven 3
- Java 17
- Spring Boot 3
- JWT
- PostgresDB
- Swagger 3
- Exchange rate API (as a third party service provider for currency exchange rate calculation)
- CircleCI

# Project Structure

The project structure is organized into modules, currently has only 2 modules: **security** and **core**

The **security** module provide a security JWT template for the **core** module and any future modules. Any modules that need JWT and Filter can import the default configuration from the **security** module.

The **core** module handle all of the Banking logic suchs as
- Creating a user
- Log in function for users
- Account creation for users
- Transaction between accounts

The structure of **core** module consist of
1. `config` : store the configuration such as URL filter configuration, Beans creations and some Sprng Boot specific annotations
2. `controller` : define API endpoint and map to corresponding service classes
3. `exception` : Custom Exception classes are used to handle business errors such as wrong format string parameter, duplicated data creation. These exception classes have their own respones HTTP code and accept error messages from service functions.
4. `model` : define database tables, columns, constraints and data type related to specific columns, as well as Request and Respones template objects.
5. `repository` : Repository classes connect to the database using a configuration provided by `application.yml`.
6. `service` : The services handle business logics on top of the repository such as checking, validation and low level error translation.
7. `util` : contain a utility classes which are essential for the Transaction process such as converting a currency from one to another, making a connection to the exchange rate third party provider

# Exchange rate API
This project use an API service provided by a third party to convert an exchanged rate. The third party API is [Exchange Rate API](https://www.exchangerate-api.com/). The API require only API-KEY for authentication.
The project use only one endpoint which is a pair convertion with given base and target currency with an amount to be converted. The endpoint accept HTTP GET method. Here is an example of a GET request that convert 50000 EURO to GBP.
```
GET https://v6.exchangerate-api.com/v6/<YOUR-API-KEY>/pair/EUR/GBP/50000
```
The respones of the request is a json. Only one field in the json is used and it's the result of the convertion, and its name is `"conversion_result"`

<h3>API KEY storage</h3>

- for local testing, the key is stored in a secret configuration file. The key value is then injected into `application.yml`.
- for CircleCI, the API-KEY is defined as an environment variable, which also will be injected into `application.yml`.

# Database
This project uses `Postgres` and `H2` database
- `Postgres` is used to store every data. The connection configuration is defined in a secret configuration file for local testing and as an environment variable on CircleCI. Both will be injected the connection configuration into `application.yml`.
- `H2` is used for testing repository classes. H2 doesn't have some features that only belongs to Postgres, functions that use those features will fail. 
  - [Testcontainers](https://www.testcontainers.org) can solve this problem by creating a Postgres docker and let the code tests against the container. The branch for this is still in experiment process

























# Couchbase Lite example

This is a simple example of how to use Couchbase Lite in a standalone Java application. This example demonstrates how to
create a database, create a document, add it to the database and synchronize the local database with a Couchbase Capella
App Service.

## Prerequisites

- Java 8 or later
- Couchbase Capella account
  - If you don't have an account, you can create one [here](https://cloud.couchbase.com/signup).
  - After creating an account, create a new Couchbase Capella App Service instance and create a new App Endpoint to get
    the service URL.
  - Add three App Roles to the App Endpoint: `approle-store-1`, `approle-store-2`, and `approle-store-3`.

    | Role                | Admin channels |
    |---------------------|:--------------:|
    | `approle-store-1`   |   `store-1`    |
    | `approle-store-2`   |   `store-2`    |
    | `approle-store-3`   |   `store-3`    |
  
  - Add three App Users to the App Endpoint and assign the following roles to them:
  
    | User               |                       Roles assigned                        |
    |--------------------|:-----------------------------------------------------------:|
    | `appuser-store-1`  |                      `approle-store-1`                      |
    | `appuser-store-2`  |                      `approle-store-2`                      |
    | `appuser-store-3`  | `approle-store-1`, `approle-store-2`, and `approle-store-3` |

## Running the example

1. Clone the repository:

    ```bash
    git clone git@github.com:cvarela/couchbase-lite.git
    ```
   
2. Open the project in your favorite IDE.
3. Set the appropriate values in the `.properties` files in the `src/main/resources` directory:

    ```properties
    # e.g. wss://<ACCOUNT>.apps.cloud.couchbase.com:4984/<APP_ENDPOINT>
    replicator.url=<service-url>
    # Password for the App User created in the Couchbase Capella App Service
    replicator.password=<app-password>
    ```

4. Run the `Main` class (`com.github.cvarela.couchbase.lite.DemoLite`) passing a `.properties` file configured in step 3
   as an argument:

    ```bash
    java -jar target/demo-lite-standalone-client-1.0-SNAPSHOT.jar ./src/main/resources/replicator_capella_store_1.properties

    ```

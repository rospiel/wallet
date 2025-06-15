# wallet

## Requirements

For building and running the application you need:

- [JDK 17]
- [Maven 3]

## Running the application locally

There are several ways to run a Spring Boot application on your local machine. One way is to execute the `main` method in the `com.wallet.WalletApplication` class from your IDE.

Alternatively you can also after build de project run in your console:

```shell
java -jar apiwallet.jar com.wallet.WalletApplication
```

## Swagger 

http://localhost:8080/swagger-ui/index.html#


## Database Console 

http://localhost:8080/h2-console/login.jsp

Validate that JDBC URL is as jdbc:h2:mem:walletdb, access data is in application.yml


## Choices and high availability

Clean architecture was chosen to clearly outline what each module should do and contain fewer classes for maintenance.

With regard to high availability, I suggest publishing in a cloud such as AWS and containing a balancer to redirect instances, and as demand increases or decreases make more instances available for example.




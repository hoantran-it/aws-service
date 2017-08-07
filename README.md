# AWS service library for client API

Amazon Web Service client API library for Java application

## Getting Started

Fork source code to get more detail or just adding maven dependency for usage.

### Installing

Add below dependency to your maven project

```
<dependency>
    <groupId>com.github.hoantran-it.library</groupId>
    <artifactId>aws-service</artifactId>
    <version>1.1</version>
</dependency>
```

### Sample use caces

Call api to upload file to S3

```
import com.github.hoantran.lib.aws.s3.S3Service;
```

```
S3Service service = new S3Service();
service.uploadS3ObjectSingleOperation(bucket, assetFullPath, input, metadata);
```

## Built With

* [Maven](https://maven.apache.org/) - Dependency Management

## Versioning

For the versions available, see the [maven repository](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22aws-service%22) or [github release](https://github.com/hoantran-it/aws-service/releases)

## Authors

* **Hoan Tran** - *Initial work* - [hoantran-it](https://github.com/hoantran-it)

See also the list of [contributors](https://github.com/hoantran-it/aws-service/graphs/contributors) who participated in this project.

## License

This project is licensed under the Apache License - see the [LICENSE.md](LICENSE.md) file for details

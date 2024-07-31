# Getting Started

This is a Java / Spring Boot implementation of the take-home exercise from Illumio.
If you do not have Java on your machine, please download and install any Java version > 17. 
I recommend using [SDKMan](https://sdkman.io/install) to manage specific [Java versions](https://sdkman.io/usage).

## Assumptions made

1. We're assuming that the logs are using the default format with version 2, as described [here](https://docs.aws.amazon.com/vpc/latest/userguide/flow-log-records.html#flow-logs-default).
2. We're assuming that the protocols we care about in the logs are just TCP and UDP [IANA Protocol Numbers](https://www.iana.org/assignments/protocol-numbers/protocol-numbers.txt).

## Running the app
Download the project contents, and from the base directory, run this command:
```shell
$ ./gradlew bootRun --args='--lookup-table=path/to/lookup/table.txt --log-file=path/to/log/file.txt --output-file=path/to/output/file.txt'
```

where `path/to/lookup/table.txt` is the full path to the lookup table file,
`path/to/log/file.txt` is the full path to the log file to be used for analysis, and 
`path/to/output/file.txt` is the full path to where the generated report should be stored.

This is an example output generated with inputs located at `src/main/resources/lookup.txt` and `src/main/resources/logs.txt` by running the command:

```shell
$ ./gradlew bootRun --args='--lookup-table=src/main/resources/lookup.txt --log-file=src/main/resources/logs.txt --output-file=output.txt'
```

```shell
Tag Counts:
Tag      Count
SV_P3   1
ssh     10
sv_P1   2
sv_P2   2
Port/Protocol Combination Counts:
Port     Protocol        Count
22      tcp     10
23      tcp     1
25      tcp     1
31      udp     1
443     tcp     1
68      udp     1



```

### TODO:
1. Unit tests - while the app is pretty simple and has been tested with actual input files, in real applications, we should get decent coverage via unit tests. This makes future refactoring way less scary.
2. We could extend this to be a web app that accepts the files via REST controllers, and responds with the report in JSON / CSV format.
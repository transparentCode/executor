# Virtual Thread Executor

This project provides a custom wrapper/library built on top of Java's virtual threads, designed to maximize concurrency while maintaining context in multi-threaded environments.

## Features

- Utilizes Java's virtual threads for lightweight concurrency
- Thread-per-task executor for maximum parallelism
- Preserves request context and thread-local data across virtual threads
- Includes monitoring capabilities for running, blocked, and waiting threads
- Proper shutdown mechanism for clean application closure
- Error handling and logging throughout the execution lifecycle

## Requirements

- Java 21 or higher

## Installation

- Clone the repo
- run `mvn clean install` (Artifact will be saved onto your local .m2 folder, check your OS for specific locations)
- Add the following dependency to your project:

```xml
<dependency>
  <groupId>com.virtual</groupId>
  <artifactId>executor</artifactId>
  <version>1.0.0-SNAPSHOT</version>
</dependency>
```
- Configure the VirtualThreadExecutor as a bean in your Spring configuration

## Components

1. **VirtualThreadExecutor**: Main class for submitting tasks to be executed on virtual threads.
2. **VirtualThreadFactory**: Factory for creating virtual threads with custom naming and priorities.
3. **ContextCallable**: Wrapper for `Callable` tasks that preserves context.
4. **ContextRunnable**: Wrapper for `Runnable` tasks that preserves context.
5. **ThreadDataContext**: Utility for managing thread-local data.

## Usage

### Submitting a Callable Task

```java
@Autowired
private VirtualThreadExecutor executor;

public void someMethod() {
    Future<String> future = executor.submitTask(() -> {
        // Your task logic here
        return "Task Result";
    });

    // Use the future result as needed
}

```


## License

This project is licensed under the Apache License - see the LICENSE.md file for details
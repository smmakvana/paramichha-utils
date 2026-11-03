# paramichha-utils

Small, dependency-free Java utilities shared by [DataFactory](https://github.com/smmakvana/paramichha-datafactory)
and [TestKit](https://github.com/smmakvana/paramichha-testkit): deterministic seeds, naming, type inspection,
token templates, argument guards and Jakarta-style value checks.

---

## The Paramichha libraries

| Library | What it does | Maven coordinates |
|---|---|---|
| [paramichha-dev](https://github.com/smmakvana/paramichha-dev) | Parent POM: Java 21, shared versions, release setup | `com.paramichha:paramichha-dev` |
| [paramichha-utils](https://github.com/smmakvana/paramichha-utils) | Dependency-free helpers: seeds, naming, token templates, guards | `com.paramichha:paramichha-utils` |
| [datafactory-catalog](https://github.com/smmakvana/datafactory-catalog) | Test fixtures: 141 annotated classes in four styles, for testing DataFactory and TestKit | `com.paramichha:datafactory-catalog` (test scope) |
| [DataFactory](https://github.com/smmakvana/paramichha-datafactory) | Generates valid and invalid test data from Jakarta validation annotations | `com.paramichha:datafactory` (test scope) |
| [TestKit](https://github.com/smmakvana/paramichha-testkit) | Generates JUnit 5 tests for a class, using DataFactory for the data | `com.paramichha:testkit` |
| [TestBuddy](https://github.com/smmakvana/paramichha-testbuddy) | IntelliJ IDEA plugin that runs TestKit from the IDE (Free and Pro) | JetBrains Marketplace |

Dependencies flow downward: TestKit uses DataFactory, DataFactory and TestKit use utils, and the catalog is used only for testing.

---

## Install

```xml
<dependency>
    <groupId>com.paramichha</groupId>
    <artifactId>paramichha-utils</artifactId>
    <version>0.1.0</version>
</dependency>
```

Requires Java 21 or later. **No runtime dependencies.**

---

## What's inside

| Class | For | Example |
|---|---|---|
| `Seeds` | Stable, platform-independent hashes for seeding random data | `Seeds.stableHash(UserRequest.class)` |
| `Texts` | Blank checks and case conversion | `Texts.camelCase("user_name")` → `userName` |
| `JavaTypes` | Type inspection, boxing, source names | `JavaTypes.box(int.class)` → `Integer.class` |
| `Tokens` | `{token}` templates | `Tokens.resolve("Hi {name}", "name", "Ada")` → `Hi Ada` |
| `Guard` | Argument and state checks with readable messages | `Guard.inRange(age, 18, 120, "age")` |
| `DataValidation` | Value checks that mirror Jakarta annotations, for use in constructors | `DataValidation.email(value)` |
| `Boundary` | Boundary-value descriptors that mirror Jakarta annotations | `Boundary.range(1, 10)` |

```java
// Deterministic seeds — same input, same value, on every JVM
long seed = Seeds.stableHash("com.example.UserRequest");
Random rnd = Seeds.random(seed);

// Naming
Texts.pascalCase("hello world");   // HelloWorld
Texts.snakeCase("HelloWorld");     // hello_world
Texts.kebabCase("hello_world");    // hello-world

// Guards return the value, so they fit in assignments
this.name = Guard.notBlank(name, "name");          // IllegalArgumentException: name must not be blank
this.age  = Guard.inRange(age, 18, 120, "age");    // ... age must be between 18 and 120, got: 17

// Jakarta-style checks — the same rules your annotations declare, enforced in code
this.email = DataValidation.email(email);
DataValidation.min(1, quantity);
```

---

## Features

- **Zero dependencies** — plain Java, Java 21 or later.
- **Deterministic** — `Seeds.stableHash` uses a fixed algorithm (not `String.hashCode()` salting or
  `Object.hashCode()`), so seeds are the same across runs, JVMs and machines. Its values are frozen by tests:
  DataFactory and TestKit output depends on them.
- **Guards that return their input** — validate and assign in one line, with messages that name the argument.
- **Jakarta mirrors** — `DataValidation` and `Boundary` follow the semantics of `@NotNull`, `@NotBlank`,
  `@Size`, `@Min`/`@Max`, `@Email`, `@Pattern`, `@Past`/`@Future`, `@AssertTrue`/`@AssertFalse` and friends.

---

## Limitations

- **Acronyms aren't split into words.** `Texts.camelCase("HTTPServer")` gives `httpserver`, not `httpServer`;
  `Texts.decapitalize("URL")` gives `uRL` (the JavaBeans convention would keep `URL`).
- **`snakeCase` / `kebabCase` don't trim.** `Texts.snakeCase("  x  ")` gives `_x_`, while `camelCase` trims.
  Trim the input first.
- **These naming results are kept as they are on purpose** — TestKit uses them for generated test names,
  so changing them would rename tests in existing projects. Expect them to change only in a major version.
- **`Seeds.stableHash` is not a cryptographic hash** — use it for reproducible test data, not for security.
- **`DataValidation` covers common rules only** — it's a convenience for constructors, not a replacement for a
  Jakarta Validation implementation; custom constraints, groups and cross-field rules aren't supported.

---

## License

Apache 2.0

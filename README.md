# VersatiaExample-Tasks-Threads

The coroutines of a `PluginScope` run on the server thread; `withContext(Dispatchers.IO)` moves blocking work, here reading a file at every join, to a worker thread and comes back for the Bukkit calls.

Part of the [Versatia examples](https://github.com/VersatiaFramework/VersatiaExamples); documentation: [Threads](https://versatia.dev/docs/tasks/coroutines#threads).

**Start with** [`Greetings.kt`](src/main/kotlin/dev/versatia/example/threads/Greetings.kt); the message is [`resources/motd.txt`](src/main/resources/resources/motd.txt).

## What the log shows

```
(a player joins) [VersatiaExample-Tasks-Threads] Reading motd.txt on DefaultDispatcher-worker-1
[VersatiaExample-Tasks-Threads] Sending it to Steve on Server thread
```

The worker's number varies. The joining player reads the two lines of `motd.txt` in their chat.

## Things to try

- Edit `plugins/VersatiaExample-Tasks-Threads/motd.txt` and rejoin: the new text arrives without a
  reload, since the file is read at every join.
- Move a Bukkit call that changes the world, such as `player.teleport(player.world.spawnLocation)`,
  into the `withContext` block and rejoin: Paper's AsyncCatcher rejects it with an
  `IllegalStateException` ("Asynchronous ..."), logged as `A coroutine failed`, and the message is
  never sent.
- Add `Thread.sleep(5000)` before `withContext`, outside the block, and rejoin: the whole server
  stands still for five seconds, because the coroutine blocks the server thread. Inside the block
  the same line delays only this greeting.

## Building

Requires JDK 21 and VersatiaAPI and
VersatiaCatalog, which supplies every version, in your local Maven repository (`./gradlew publishToMavenLocal` in each checkout; see
[Building the framework](https://versatia.dev/docs/contributing/building));
running it requires a Paper 1.21.4 server with VersatiaCore installed.

```bash
./gradlew build
```

To have the JAR copied to your server after every build, copy `local.properties.example` to
`local.properties` and set `pluginsDir`. The generated descriptor is in
`build/generated/ksp/main/kotlin`; open it to see exactly what the runtime executes.

The Versatia version is `versatiaVersion` in `gradle.properties`; `settings.gradle.kts` imports
the matching [VersatiaCatalog](https://versatia.dev/docs/getting-started/installation#versatia-catalog),
so the build script writes `libs.versatia.api` and `alias(libs.plugins.ksp)` instead of version
numbers.

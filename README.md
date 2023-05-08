# Scramble

This is an application that accepts two strings and checks if one of them can be built using a portion of characters from the other string.

The check is implemented on the server side in the [scramble.scramble](https://github.com/7even/scramble/blob/master/src/clj/scramble/scramble.clj) namespace. The front end application includes a form with 2 inputs that is submitted to the server, and the result is sent back to be rendered in the interface.

## Usage

The simplest way to launch the app is to build a Docker image and run a container from it:

``` shell
# cd path/to/app
$ docker build -t scramble .
$ docker run -p 7000:7000 scramble
```

Then navigate to [localhost:7000](http://localhost:7000/).

It is also possible to start the application with `clj -A:clj:cljs:dev`, but this is designed to be launched by `cider-jack-in-clj&cljs` from Emacs/CIDER; the [.dir-locals.el](https://github.com/7even/scramble/blob/master/.dir-locals.el) file includes all necessary project-specific settings (including a custom ClojureScript REPL init form allowing the developer to run both REPLs from a single JVM process).

*Be sure to `npm install` first, so that React is installed.*

## Testing

Tests can be run with `clj -M:clj:test`.

## Building

It is possible to build a self-sufficient jar containing the application (including both backend and frontend code).

First you need to compile the frontend - shadow-cljs is configured to store the resulting JS inside `resources/public` so that the backend app can serve it over HTTP.

``` shell
$ npx shadow-cljs release :main
```

Then you can build the jar file:

``` shell
$ clj -T:build uberjar
```

The resulting file will be located at `target/scramble.jar`, you can run it with `java -jar target/scramble.jar`.

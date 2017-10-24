# shadow-cljs - browser quickstart

This is a minimum template you can use as the basis for CLJS projects intended to run in the browser.

You must have [`npm v5+`](https://www.npmjs.com/) installed for this example. You may also use `yarn`, just adjust the `npm&npmx` commands accordingly. `java` must also be installed.

```bash
git clone https://github.com/shadow-cljs/quickstart-browser.git quickstart
cd quickstart
npm install
npx shadow-cljs clj-repl
```

The first startup takes a bit of time since it has to download all the dependencies and do some prep work. Once this is running we can get started.

```txt
(shadow/watch :app)
```

You do not have to do this at the REPL, you can also run `npx shadow-cljs watch app` in another terminal. The result will be the same.

Either way you should see a message like this:

```txt
[:app] Build completed. (23 files, 4 compiled, 0 warnings, 7.41s)
```

When you do you can start using the integrated development server to open the page in the browser.

```txt
open http://localhost:8020
```

The app is only a very basic skeleton with the most useful development tools configured.

`shadow-cljs` is configured by the `shadow-cljs.edn` config. It looks like this:

```clojure
{:source-paths
 ["src"] ;; .cljs files go here

 :dependencies
 [] ;; covered later

 :builds
 {:app {:target :browser
        :output-dir "public/js"
        :asset-path "/js"

        :modules
        {:main ;; <- becomes public/js/main.js
         {:entries [starter.browser]}}

        :devtools
        ;; before live-reloading any code call this function
        {:before-load starter.browser/stop
         ;; after live-reloading finishes call this function
         :after-load starter.browser/start
         ;; serve the public directory over http at port 8020
         :http-root "public"
         :http-port 8020}
        }}}
```

It defines the `:app` build with the `:target` set to `:browser`. All output will be written to `public/js` which is a path relative to the project root (ie. the directory the `shadow-cljs.edn` config is in).

`:modules` defines the how the output should be bundled together. For now we just want one file. The `:main` module will be written to `public/js/main.js`, it will include the code from the `:entries` and all their dependencies.

`:devtools` configures some useful development things. The `http://localhost:8020` server we used earlier is controlled by the `:http-port` and serves the `:http-root` directory.

`:before-load` and `:after-load` are useful callbacks that will be used by the devtools when live-reloading code. They are optional but they control the live-reload. If you do not need any callbacks just configure `:autoload true`.

The last part is the actual `index.html` that is loaded when you open `http://localhost:8020`. It loads the generated `/js/main.js` and then calls `start.browser.init` which we defined in the `src/start/browser.cljs`.

```html
<!doctype html>
<html>
<head><title>Browser Starter</title></head>
<body>
<h1>shadow-cljs - Browser</h1>
<div id="app"></div>

<script src="/js/main.js"></script>
<script>starter.browser.init();</script>
</body>
</html>
```

`init` is only called once and it calls `start` when done. During development the devtools will then call `stop` whenever it wants to reload some code. When its done doing that it will call `start` again but not `init`. You do not have to use this setup but it is what I recommend and it has worked well for me.

## Live reload

To see the live reload in action you can edit the `src/start/browser.cljs`. Some output will be printed in the browser console.

## REPL

During development it the REPL is very useful. The `clj-repl` process we started by default is a Clojure REPL which can control the `shadow-cljs` tool itself. Every command can also be directly used from the command line, so you do not have to use the REPL.

To switch to the ClojureScript REPL for our build do

```
[1:0]~shadow.user=> (shadow/repl :app)
[1:1]~cljs.user=>
```

From the command line use `npx shadow-cljs cljs-repl app`.

This can now be used to eval code in the browser (assuming you still have it open). Try `(js/alert "Hi.")` and take it from there.

You can get back to the Clojure REPL by typing `:repl/quit`. You can switch back to the CLJS REPL at any point.

## Release

The `watch` process we started is all about development. It injects the code required for the REPL and the all other devtools but we do not want any of that when putting the code into "production" (ie. making it available publicly).

The `release` action will remove all development code and run the code through the Closure Compiler to produce a minified `main.js` file. Since that will overwrite the file created by the `watch` we first need to stop that.

```
(shadow/stop-worker :app)
(shadow/release :app)
```

Or in the command line stop the `npx shadow-cljs watch` process by CTRL+C and then `npx shadow-cljs release app`.

When done you can open `http://localhost:8020` and see the `release` build in action. At this point you would usually copy the `public` directory to the "production" web server.

Note that in the default config we overwrote the `public/js/main.js` created by the `watch`. You can also configure a different path to use for release builds but writing the output to the same file means we do not have to change the `index.html` and test everything as is.

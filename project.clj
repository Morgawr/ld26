(defproject ld26 "0.1.0-SNAPSHOT"
  :description "FIXME: write this!"
  :url "http://example.com/FIXME"
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [ring "1.1.8"]]
  :plugins [[lein-cljsbuild "0.3.0"]
            [jayq "2.3.0"]]
  :hooks [leiningen.cljsbuild]
  :source-paths ["src/clj"]
  :cljsbuild { 
    :builds {
      :main {
        :source-paths ["src/cljs"]
        :compiler {:output-to "site/js/cljs.js"
                   :optimizations :advanced
                   :pretty-print false}
        :jar true}}}
  :main ld26.server
)


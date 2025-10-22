(defproject clj-gephi "0.1.0-SNAPSHOT"
  :description "clojure wrapper for gephi toolkit api"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.11.1"]
                 [org.gephi/gephi-toolkit "0.10.1"]
                 [prismatic/schema "1.4.1"]]

  ;; JVM options for headless testing (required for CI/CD)
  :jvm-opts ["-Djava.awt.headless=true"
             "-Dorg.gephi.visualization.screenshot=false"]

  ;; Test configuration
  :test-selectors {:default (complement :integration)
                   :integration :integration
                   :all (constantly true)}

  ;; Profiles
  :profiles {:dev {:dependencies [[org.clojure/test.check "1.1.1"]]}
             :test {:jvm-opts ["-Djava.awt.headless=true"
                               "-Dorg.gephi.visualization.screenshot=false"]}})

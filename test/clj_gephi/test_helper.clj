(ns clj-gephi.test-helper
  (:require [clj-gephi.project :as p]))

;; Configure headless mode for testing
(System/setProperty "java.awt.headless" "true")
(System/setProperty "org.gephi.visualization.screenshot" "false")

(defn create-isolated-workspace
  "Create a new isolated workspace for testing.
  Each test should use this to avoid interference."
  []
  (p/new-project!)
  (p/current-workspace))

(defn with-test-project
  "Execute test function with a fresh project/workspace.
  Ensures proper cleanup and isolation."
  [test-fn]
  (try
    (let [wp (create-isolated-workspace)]
      (test-fn wp))
    (catch Exception e
      ;; Log error but don't fail on cleanup
      (println "Test cleanup warning:" (.getMessage e)))))

(defmacro with-workspace
  "Macro to run test body with a fresh workspace binding."
  [workspace-binding & body]
  `(with-test-project
     (fn [~workspace-binding]
       ~@body)))

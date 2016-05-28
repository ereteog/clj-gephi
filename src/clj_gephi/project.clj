(ns clj-gephi.project
  (import [org.openide.util Lookup])
  (import [org.gephi.project.api ProjectController Workspace])
  )

(def pc
  (.lookup (Lookup/getDefault) ProjectController))

(defn workspace
  []
  (.getCurrentWorkspace pc))

(defn new-project!
  "Init a project - and return workspace"
  []
  (.newProject pc)
  (workspace))
